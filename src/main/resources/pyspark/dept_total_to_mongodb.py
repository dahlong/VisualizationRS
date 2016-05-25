#dataframes department with total and month>> take around 3mins30sec
from pyspark import SparkConf, SparkContext
from pyspark.sql import SQLContext
from pyspark.sql.types import *
from pymongo import MongoClient
from collections import OrderedDict
from pyspark.sql.functions import *
import datetime
import sys

client = MongoClient("mongodb://192.168.187.129:27017")
db = client.hrvisual

conf = SparkConf().setAppName("building department_total_to_mongodb")
sc = SparkContext(conf=conf)
sqlContext = SQLContext(sc)

#strYear='2015'
strYear=sys.argv[1]

collectionName = 'LEAVE_TYPE_%s' % strYear
#if the collection exises in mongodb, then drop it
#if collName in db.collection_names():
#        db[collName].drop()            

def getRddWithoutHeader(csvfname, fieldname):
    f  = sc.textFile(csvfname)
    r = f.filter(lambda l: fieldname in l)
    r.collect()
    r1 = f.subtract(r)
    d=r1.map(lambda k: k.split(","))
    return d

arrayferialname=["特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假", \
                    "事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假", "安胎假"]

def getFerialDict(fList, start):
    mydic={}
    for i in range(0,len(arrayferialname),1):
        mydic[arrayferialname[i]]=int(fList[i+start])
    
    return mydic

#build up schema
structFields=[StructField("dept_id", StringType()), StructField("dept_name", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)

print "===========read csv data============", datetime.datetime.now()
data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'dept_id')
lines_temp = data.map(lambda p: (str(p[2]),unicode(p[3]),int(p[10]),int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]), \
    int(p[16]), int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))


lines_df = sqlContext.createDataFrame(lines_temp, schema)
#print lines_df.printSchema()
print "============group by dept_id / month data============"
line_group=lines_df.groupBy("dept_id", "dept_name").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', \
            '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_group_month=lines_df.groupBy("dept_id", "dept_name", "month").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', \
            '家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_sort=line_group.sort(asc("dept_id")).collect()
print "============preparing list for mongodb============"
mylist = []
for a in line_sort: 
    rowDb={}
    rowDb["dept_id"]=a[0]
    rowDb["dept_name"]=a[1]
    rowDb["Total"]=getFerialDict(a,2)
    line_filter=line_group_month.filter(line_group_month.dept_id==a[0]).collect()
    for b in line_filter:
        rowDb[str(b[2])]=getFerialDict(b,3)
        
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    mylist.append(rowDb)
    print 'list size>>',len(mylist)   
    
print "============start insert organzation statistics into mongodb============"
result=db[collectionName].insert_many(mylist)       
print "============finish to insert org total into mongodb============", datetime.datetime.now()