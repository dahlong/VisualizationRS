
# coding: utf-8

# In[2]:

#dataframes
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

conf = SparkConf().setAppName("building emp_total_to_mongodb")
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

arrayferialname=["特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假","事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假", "安胎假"]
def getFerialDict(fList, start):
    mydic={}
    for i in range(0,len(arrayferialname),1):
        mydic[arrayferialname[i]]=int(fList[i+start])
    
    return mydic

#build up schema
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()),StructField("dept_id", StringType()), StructField("dept_name", StringType()),StructField("emp_id", StringType()), StructField("emp_name", StringType()),StructField("emp_number", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)


print "===========read csv data============", datetime.datetime.now()
data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),str(p[2]),unicode(p[3]), str(p[4]),unicode(p[5]), str(p[6]),int(p[10]),                     int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),int(p[16]),                     int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
#print lines_df.printSchema()
print "============group by org_id / month data============"
line_group=lines_df.groupBy("org_id", "org_name", "dept_id", "dept_name","emp_id","emp_name","emp_number").sum('特別休假',             '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_group_month=lines_df.groupBy("org_id", "org_name", "dept_id", "dept_name","emp_id","emp_name","emp_number", "month").sum('特別休假',             '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_sort=line_group.sort(asc("org_id")).collect()
print "============preparing dict and insert into mongodb============"
for a in line_sort: 
    rowDb={}
    rowDb["org_id"]=a[0]
    rowDb["org_name"]=a[1]
    rowDb["dept_id"]=a[2]
    rowDb["dept_name"]=a[3]
    rowDb["emp_id"]=a[4]
    rowDb["emp_name"]=a[5]
    rowDb["emp_number"]=a[6]
    for i in range(0,len(arrayferialname),1):
        rowDb[arrayferialname[i]]=int(a[i+7])
    line_filter=line_group_month.filter(line_group_month.emp_id==a[4]).collect()
    for b in line_filter:
        rowDb[str(b[7])]=getFerialDict(b,8)
    
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    #print 'rowDb=', rowDb
    result=db[collectionName].insert_one(rowDb)


print "============finish to insert emp total into mongodb============", datetime.datetime.now()

