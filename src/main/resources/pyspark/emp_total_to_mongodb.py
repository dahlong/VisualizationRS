
# coding: utf-8

# In[2]:

#dataframes
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

collectionName = 'LEAVE_TYPE_EMP_%s' % strYear
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
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()),               StructField("dept_id", StringType()), StructField("dept_name", StringType()),               StructField("emp_id", StringType()), StructField("emp_name", StringType()),               StructField("emp_number", StringType()), StructField("month", IntegerType())]
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


print "============finish to insert org total into mongodb============", datetime.datetime.now()


# In[5]:

#dataframes
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
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()),               StructField("dept_id", StringType()), StructField("dept_name", StringType()),               StructField("emp_id", StringType()), StructField("emp_name", StringType()),               StructField("emp_number", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)
print "===========read csv data============", datetime.datetime.now()
data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),str(p[2]),unicode(p[3]), str(p[4]),unicode(p[5]), str(p[6]),int(p[10]),                     int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),int(p[16]),                     int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
lines_df.registerTempTable("tempTable")

sqlOrg=sqlContext.sql("SELECT DISTINCT org_id, org_name FROM tempTable ORDER BY org_id")
for org in sqlOrg.collect():
    #print org[0], org[1]
    sqlStr="SELECT DISTINCT dept_id, dept_name FROM tempTable WHERE org_id=%s ORDER BY dept_id" % str(org[0])
    sqlDept=sqlContext.sql(sqlStr)
    for dept in sqlDept.collect():
        #print dept[0], dept[1]
        sqlStr1="SELECT emp_id, emp_name, emp_number,                  SUM(特別休假) as 特別休假, SUM(加班或假日出差轉補休) as 加班或假日出差轉補休,                 SUM(生理假) as 生理假,SUM(傷病假) as 傷病假,SUM(婚假) as 婚假,SUM(家庭照顧假) as 家庭照顧假,SUM(事假) as 事假,                 SUM(產檢假) as 產檢假,SUM(陪產假) as 陪產假,SUM(產假) as 產假,SUM(喪假) as 喪假,SUM(國內公假) as 國內公假,                 SUM(國外公假) as 國外公假,SUM(公傷病假) as 公傷病假,SUM(安胎假) as 安胎假 FROM tempTable                 WHERE org_id=%s AND dept_id=%s                 GROUP BY emp_id, emp_name, emp_number                 ORDER BY emp_id" % (str(org[0]), str(dept[0]),)
        sqlEmpTotal=sqlContext.sql(sqlStr1)
        for empTotal in sqlEmpTotal.collect():
            for mon in range(1, 13, 1):
                sqlStr2="SELECT month, SUM(特別休假) as 特別休假, SUM(加班或假日出差轉補休) as 加班或假日出差轉補休,                     SUM(生理假) as 生理假,SUM(傷病假) as 傷病假,SUM(婚假) as 婚假,SUM(家庭照顧假) as 家庭照顧假,SUM(事假) as 事假,                     SUM(產檢假) as 產檢假,SUM(陪產假) as 陪產假,SUM(產假) as 產假,SUM(喪假) as 喪假,SUM(國內公假) as 國內公假,                     SUM(國外公假) as 國外公假,SUM(公傷病假) as 公傷病假,SUM(安胎假) as 安胎假 FROM tempTable                     WHERE org_id=%s AND dept_id=%s AND emp_id=%s AND month=%s                     GROUP BY month ORDER BY month" % (str(org[0]), str(dept[0]), str(empTotal[0]), str(mon))
                sqlEmpMonth=sqlContext.sql(sqlStr2)
                rowDb[str(mon)]=getFerialDict(sqlEmpMonth.collect(),1)
            

print "===========end============", datetime.datetime.now()            


# In[3]:

#dataframes
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
structFields=[StructField("org_id", StringType()), StructField("org_name", StringType()),               StructField("dept_id", StringType()), StructField("dept_name", StringType()),               StructField("emp_id", StringType()), StructField("emp_name", StringType()),               StructField("emp_number", StringType()), StructField("month", IntegerType())]
for i in range(1,len(arrayferialname) +1,1):
    structFields.append(StructField(arrayferialname[i-1], IntegerType()))

schema = StructType(structFields)

sqlStr="SELECT org_id, org_name, dept_id, dept_name, emp_id, emp_name, emp_number, SUM(特別休假) as 特別休假,         SUM(加班或假日出差轉補休) as 加班或假日出差轉補休, SUM(生理假) as 生理假,SUM(傷病假) as 傷病假,SUM(婚假) as 婚假,         SUM(家庭照顧假) as 家庭照顧假,SUM(事假) as 事假, SUM(產檢假) as 產檢假,SUM(陪產假) as 陪產假,SUM(產假) as 產假,         SUM(喪假) as 喪假,SUM(國內公假) as 國內公假, SUM(國外公假) as 國外公假,SUM(公傷病假) as 公傷病假,SUM(安胎假) as 安胎假         FROM tempTable WHERE org_id=%s AND dept_id=%s AND month=%s         GROUP BY org_id, org_name, dept_id, dept_name, emp_id, emp_name, emp_number         ORDER BY org_id, dept_id, emp_id, month"

print "===========read csv data============", datetime.datetime.now()
data=getRddWithoutHeader('LEAVE_TYPE_%s.csv' % strYear,'org_id')
lines_temp = data.map(lambda p: (str(p[0]),unicode(p[1]),str(p[2]),unicode(p[3]), str(p[4]),unicode(p[5]), str(p[6]),int(p[10]),                     int(p[11]),int(p[12]),int(p[13]),int(p[14]),int(p[15]),int(p[16]),                     int(p[17]),int(p[18]),int(p[19]),int(p[20]),int(p[21]),int(p[22]),int(p[23]),int(p[24]),int(p[25])))

lines_df = sqlContext.createDataFrame(lines_temp, schema)
lines_df.registerTempTable("tempTable")

line_org=lines_df.groupBy("org_id").count().sort(asc("org_id")).collect()
for org in line_org: 
    line_dept=lines_df.filter(lines_df.org_id==str(org[0])).groupBy("dept_id").count().sort(asc("dept_id")).collect()
    for dept in line_dept:
        #get the total of each employee
        line_emp=lines_df.filter(lines_df.dept_id==str(dept[0])).groupBy("emp_id").sum('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假',             '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').sort(asc("emp_id"))
        print line_emp.first()
            
        '''
        for mon in range(1, 13, 1):
            sqlEmp=sqlContext.sql(sqlStr % (str(org[0]), str(dept[0]), str(mon)))
            for emp in sqlEmp.collect():
                rowDb={}
                rowDb["org_id"]=emp[0]
                rowDb["org_name"]=emp[1]
                rowDb["dept_id"]=emp[2]
                rowDb["dept_name"]=emp[3]
                rowDb["emp_id"]=emp[4]
                rowDb["emp_name"]=emp[5]
                rowDb["emp_number"]=emp[6]
                
                rowDb[str(mon)]=getFerialDict(emp, 7)
                
                rowDb["特別休假"]=emp[7]
                rowDb["加班或假日出差轉補休"]=emp[8]
                rowDb["生理假"]=emp[9]
                rowDb["傷病假"]=emp[10]
                rowDb["婚假"]=emp[11]
                rowDb["家庭照顧假"]=emp[12]
                rowDb["事假"]=emp[13]
                rowDb["產檢假"]=emp[14]
                rowDb["陪產假"]=emp[15]
                rowDb["產假"]=emp[16]
                rowDb["喪假"]=emp[17]
                rowDb["國內公假"]=emp[18]
                rowDb["國外公假"]=emp[19]
                rowDb["公傷病假"]=emp[20]
                rowDb["安胎假"]=emp[21]
                result=db[collectionName].insert_one(rowDb)

           '''     
                
    
print "===========End============", datetime.datetime.now()

