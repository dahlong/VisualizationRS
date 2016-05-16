
# coding: utf-8

# In[38]:

# 從 oracle sql 取得年度的 employee資料，存為 csv 檔 >> ORG_EMP_%s.csv % strYear, then insert into Mongodb
#records 1934 takes 1 mins 
# input parameter : year
import cx_Oracle
import os
import os.path
import csv
from collections import OrderedDict
from pymongo import MongoClient
import datetime

print cx_Oracle.version
os.environ["NLS_LANG"] = "AMERICAN_AMERICA.AL32UTF8"

strYear='2015'
ysDate=strYear+'/1/1'
yeDate=strYear+'/12/31'

#insert into mongodb
client = MongoClient("mongodb://192.168.187.128:27017")
db = client.hrvisual
collName = 'ORG_DEPT_EMP_TEST_%s' % strYear
#if the collection exises in mongodb, then drop it
#if collName in db.collection_names():
#        db[collName].drop()            

con = cx_Oracle.connect('apps/apps0677@127.0.0.1:1524/PROD')
print con.version
cur = con.cursor()


csvfilname='ORG_DEPT_EMP_TEST_%s.csv' % strYear

#write array to csv
def wirteDate(filename, writedata):
    f = open(filename,"w")  
    w = csv.writer(f)  
    w.writerows(writedata)  
    f.close()  

#call getNarlRole procedure    
def get_employee_role(user_id):
    curRole = con.cursor()
    l_role = curRole.callfunc('NARL_TL_MAIN_PKG.GETNARLROLE', cx_Oracle.STRING, [int(user_id)])
    curRole.close()
    return l_role 

QUERY_EMP="select v.legal_entity_id as org_id, v.legal_entity_name as org_name,        v.dept_id as dept_id, v.dept_name as dept_name,        v.employee_id as emp_id,v.emp_name as emp_name, v.employee_number as employee_number,        v.date_start as start_date, v.actual_termination_date as end_date, v.user_id as user_id        from narl_login_emp_info_hist_v v where v.date_start<=TO_DATE('%s', 'yyyy/mm/dd')        AND NVL(v.actual_termination_date,TO_DATE('9999/12/31','yyyy/mm/dd'))>=TO_DATE('%s', 'yyyy/mm/dd')        AND v.legal_entity_id>81        order by v.legal_entity_id, v.employee_id"


print 'query start...', datetime.datetime.now()
emp_list=cur.execute(QUERY_EMP % (yeDate,yeDate))
#print emp_list

arrayCVS=[]   
arrayCVS.append(['org_id','org_name','dept_id','dept_name','emp_id','emp_name','emp_number','start_date','end_date', 'user_id', 'current_role'])
for row in emp_list:
    rowDb={}
    rowDb["org_id"]=str(row[0])
    rowDb["org_name"]=str(row[1])
    rowDb["dept_id"]=str(row[2])
    rowDb["dept_name"]=str(row[3])    
    rowDb["emp_id"]=str(row[4])
    rowDb["emp_name"]=str(row[5])
    rowDb["emp_number"]=str(row[6])
    rowDb["start_date"]=row[7].strftime('%Y/%m/%d')
    arrayData=[str(row[0]),str(row[1]),str(row[2]),str(row[3]),str(row[4]),str(row[5]),                str(row[6]),row[7].strftime('%Y/%m/%d')]
    if row[8] is None:
        rowDb["end_date"]=""
        arrayData.append("")
    else:
        rowDb["end_date"]=row[8].strftime('%Y/%m/%d')
        arrayData.append(row[8].strftime('%Y/%m/%d'))
        
    rowDb["user_id"]=str(row[9])
    arrayData.append(str(row[9]))
    role  = get_employee_role(row[9])
    rowDb["current_role"]=role
    arrayData.append(role)
    
    arrayCVS.append(arrayData)
    #print 'start to inser into mongodb'
    #rowDb=OrderedDict(sorted(rowDb.items(), key=lambda t: t[0]))
    if len([rowDb])>0:
        result=db[collName].insert_many([rowDb])
        

print len(arrayCVS)
wirteDate(csvfilname, arrayCVS)

cur.close()
print 'query end.....', datetime.datetime.now()
con.close()


# In[33]:

# 從 oracle sql 取得人員的角色，input is user_id
import cx_Oracle
import os
import os.path

print cx_Oracle.version
os.environ["NLS_LANG"] = "AMERICAN_AMERICA.AL32UTF8"

con = cx_Oracle.connect('apps/apps0677@127.0.0.1:1524/PROD')
print con.version
cur = con.cursor()

out_parameter = cur.var(cx_Oracle.STRING)
#0203134-2810; 1503051-16871-
execute_func  = cur.callfunc('NARL_TL_MAIN_PKG.GETNARLROLE', cx_Oracle.STRING, [16871])
print execute_func

cur.close()
print 'query end.....'
con.close()


# In[8]:

#create distinct org_id & org_name
from pyspark import SparkContext
from pyspark.sql.types import *

sqlContext = SQLContext(sc)

schemaString = "id,name"
fields = [StructField(field_name, StringType(), True) for field_name in schemaString.split(',')]
schema = StructType(fields)
print schema

inputFile='ORG_DEPT_EMP_2015.csv'
lines = sc.textFile(inputFile)
header=lines.first()

linesHeader = lines.filter(lambda l: "org_id" in l)
linesHeader.collect()

linesNoHeader = lines.subtract(linesHeader)
lines_temp = linesNoHeader.map(lambda k: k.split(",")).map(lambda p: (p[0], p[1]))
lines_df = sqlContext.createDataFrame(lines_temp, schema)
print lines_df.printSchema()
#print lines_df.groupBy("org_id").count().show()
lines_df.registerTempTable("tempTable")
print sqlContext.sql("SELECT DISTINCT * FROM tempTable").show()

