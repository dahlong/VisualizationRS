
# coding: utf-8

# In[1]:

#dataframes, org with total and month
from pyspark import SparkContext
from pyspark.sql.types import *
from pymongo import MongoClient
from collections import OrderedDict
from pyspark.sql.functions import *
import datetime

client = MongoClient("mongodb://192.168.187.128:27017")
db = client.hrvisual

sqlContext = SQLContext(sc)

strYear='2015'

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

arrayferialname=["特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假",                     "事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假", "安胎假"]

def getFerialDict(fList, start):
    mydic={}
    for i in range(0,len(arrayferialname),1):
        mydic[arrayferialname[i]]=int(fList[i+start])
    
    return mydic


#build up schema
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)

print "===========read csv data============", datetime.datetime.now()
data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),int(p[10]),int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),             int(p[16]),int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
#print lines_df.printSchema()

print "============group by org_id / month data============"
line_group=lines_df.groupBy("org_id", "org_name").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_group_month=lines_df.groupBy("org_id", "org_name", "month").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

line_sort=line_group.sort(asc("org_id")).collect()
print "============preparing list for mongodb============"
mylist = []
for a in line_sort: 
    rowDb={}
    rowDb["org_id"]=a[0]
    rowDb["org_name"]=a[1]
    rowDb["Total"]=getFerialDict(a, 2)
    line_filter=line_group_month.filter(line_group_month.org_id==a[0]).collect()
    for b in line_filter:
        rowDb[str(b[2])]=getFerialDict(b, 3)
        
    #print rowDb   
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    mylist.append(rowDb)
    #result=db[collectionName].insert_one(rowDb)    

print "============start insert organzation statistics into mongodb============"
result=db[collectionName].insert_many(mylist)       
print "============finish to insert org total into mongodb============", datetime.datetime.now()


# In[6]:

#dataframes including month
from pyspark import SparkContext
from pyspark.sql.types import *
from pymongo import MongoClient
from collections import OrderedDict
from pyspark.sql.functions import *

client = MongoClient("mongodb://192.168.187.128:27017")
db = client.hrvisual

sqlContext = SQLContext(sc)

strYear='2015'

collectionName = 'LEAVE_TYPE_TEST_%s' % strYear
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

arrayferialname=["特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假",                     "事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假", "安胎假"]

#build up schema
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)


data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),int(p[10]),int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),int(p[16]),     int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
#print lines_df.printSchema()

line_group=lines_df.groupBy("org_id", "org_name", "month").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

#insert into mongodb
line_sort=line_group.sort(asc("org_id")).collect()
for a in line_sort: 
    rowDb={}
    rowDb["org_id"]=a[0]
    rowDb["org_name"]=a[1]
    rowDb["month"]=a[2]
    
    sumArray={}
    for i in range(0,len(arrayferialname),1):
        sumArray[arrayferialname[i]]=int(a[i+3])
    
    rowDb["Total"]=sumArray
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    #print 'rowDb=', rowDb
    result=db[collectionName].insert_one(rowDb)
    

print "finish to insert into mongodb"


# In[1]:

#dataframes
from pyspark import SparkContext
from pyspark.sql.types import *
from pymongo import MongoClient
from collections import OrderedDict
from pyspark.sql.functions import *

client = MongoClient("mongodb://192.168.187.128:27017")
db = client.hrvisual

sqlContext = SQLContext(sc)

strYear='2015'

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

arrayferialname=["特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假",                     "事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假", "安胎假"]

#build up schema
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)


data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),int(p[16]),     int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
#print lines_df.printSchema()

line_group=lines_df.groupBy("org_id", "org_name").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')

#insert into mongodb
line_sort=line_group.sort(asc("org_id")).collect()
for a in line_sort: 
    rowDb={}
    rowDb["org_id"]=a[0]
    rowDb["org_name"]=a[1]
    
    sumArray={}
    for i in range(0,len(arrayferialname),1):
        sumArray[arrayferialname[i]]=int(a[i+2])
    
    rowDb["Total"]=sumArray
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    #print 'rowDb=', rowDb
    result=db[collectionName].insert_one(rowDb)
    
'''
lines_df.registerTempTable("tempTable")

sqlcon=sqlContext.sql("SELECT org_id, org_name, SUM(特別休假) as 特別休假, SUM(加班或假日出差轉補休) as 加班或假日出差轉補休, \
        SUM(生理假) as 生理假,SUM(傷病假) as 傷病假,SUM(婚假) as 婚假,SUM(家庭照顧假) as 家庭照顧假,SUM(事假) as 事假, \
        SUM(產檢假) as 產檢假,SUM(陪產假) as 陪產假,SUM(產假) as 產假,SUM(喪假) as 喪假,SUM(國內公假) as 國內公假, \
        SUM(國外公假) as 國外公假,SUM(公傷病假) as 公傷病假,SUM(安胎假) as 安胎假 FROM tempTable GROUP BY org_id, org_name")

#print sqlcon
print sqlcon.show()
'''
print "finish to insert into mongodb"


# In[ ]:

#以下統計各中心的各假別total
from pyspark import SparkContext
from pyspark.sql.types import *
from operator import add
from pymongo import MongoClient
from collections import OrderedDict

client = MongoClient("mongodb://192.168.187.128:27017")
db = client.hrvisual

strYear='2015'

collName = 'LEAVE_TYPE_%s' % strYear

dataFileName='LEAVE_TYPE_%s.csv' % strYear

def getRddWithoutHeader(csvfname, fieldname):
    f  = sc.textFile(csvfname)
    r = f.filter(lambda l: fieldname in l)
    r.collect()
    r1 = f.subtract(r)
    d=r1.map(lambda k: k.split(","))
    return d


def filter_org(s,pos, v):
    if s[pos] == v:
        return True
    else:
        return False


def getOrgIdList(orgFile):
    org_data=getRddWithoutHeader(orgFile,'org_id')
    #org_1 = org_parts.map(lambda p: (str(p[0]),  p[1].encode('utf-8')))
    o1 = org_data.map(lambda p: (str(p[0]),  unicode(p[1])))
    o2=sc.parallelize(o1.top(o1.count()))    
    orglist=sorted(o2.collect())
    arrId=[]
    arrName=[]
    for o in  range(0,o2.count(),1):
        arrId.append(orglist[o][0])
        arrName.append(orglist[o][1])
        
    return arrId, arrName
    
    
def getFerialArray(fName):
    f1=sc.textFile(fName)
    f2 = f1.filter(lambda l: 'org_id' in l)
    f3=f2.map(lambda k: k.split(","))
    arrf=[]
    for i in range (7, 22,1):
        arrf.append(f3.top(1)[0][i])
    
    return arrf


#特別休假,加班或假日出差轉補休,生理假,傷病假,婚假,家庭照顧假,事假,產檢假,陪產假,產假,喪假,國內公假,國外公假,公傷病假,安胎假
arrayferialname= getFerialArray(dataFileName)
#print arrayferialname

data=getRddWithoutHeader(dataFileName,'org_id')

#取組織 org_id, org_nmame
orgIdArray, orgNameArray=getOrgIdList(orgFileName)
narlOrg = dict(zip(orgIdArray, orgNameArray))
#print narlOrg

for org_id, org_name in narlOrg.iteritems() :
    total_org = data.map(lambda p: (str(p[0]),int(p[7]), int(p[8]),int(p[9]),int(p[10]),int(p[11]),int(p[12]),                                      int(p[13]),int(p[14]), int(p[15]),int(p[16]),int(p[17]),int(p[18]),int(p[19]),                                      int(p[20]),int(p[21])))
    rdd1 = sc.parallelize(total_org.top(total_org.count()))
    f_org_1 = rdd1.filter(lambda s: filter_org(s,0,org_id))
    print 'org_id=',org_id, " 共幾筆記錄:", f_org_1.count()
    rowDb={}
    rowDb["org_id"]=org_id
    rowDb["org_name"]=org_name
    #print 'rowDb=', rowDb
    
    sumArray={}
    for vi in range(1,len(arrayferialname) +1,1):
        if (f_org_1.count()>0):
            f_org_2=f_org_1.map(lambda p: (str(p[0]),int(p[vi])))
            rdd3 = sc.parallelize(f_org_2.top(f_org_2.count()))
            sum_1=sorted(rdd3.reduceByKey(add).collect())
            sumArray[arrayferialname[vi-1]]=sum_1[0][1]
        else:
            sumArray[arrayferialname[vi-1]]=int(0)
        
        
    rowDb["Total"]=sumArray
    rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    #print 'rowDb=',rowDb
    
    #if the collection exises in mongodb, then drop it
    #if collName in db.collection_names():
    #        db[collName].drop()            
    
    #print 'start to inser into mongodb'        
    if len([rowDb])>0:
        result=db[collName].insert_many([rowDb])
               


print '%s 年度，各中心寫入 mongodb 結束.....' %strYear

