# -*- coding: utf-8 -*-
from pyspark import SparkConf, SparkContext
from pyspark.sql import SQLContext
from pyspark.sql.functions import udf
from pyspark.sql.functions import format_number,date_format,concat,lit,format_string
import sys
import datetime

def employee_trans(employee_number):
    employee_number_s = str(employee_number)
    
    if len(employee_number_s) == 6:
        return '0'+employee_number_s
    
    if len(employee_number_s) == 5:
        return '00'+employee_number_s
    
    if len(employee_number_s) == 4:
        return '000'+employee_number_s
    
    return employee_number

if __name__ == "__main__":
    print 'Starting...', datetime.datetime.now()
    strYear = sys.argv[1]
    mongodbIp=sys.argv[2]
    db_location=sys.argv[3]
    conf=SparkConf()
    conf.setMaster("local")
    conf.setAppName("MongoSparkLeaveTypeInfo")
    conf.set("spark.app.id", "MongoSparkLeaveTypeInfo")
    #conf.set('spark.mongodb.output.uri','mongodb://127.0.0.1/hrvisual.LEAVE_TYPE_%s' % strYear)
    conf.set('spark.mongodb.output.uri','mongodb://%s:27017/hrvisual.LEAVE_TYPE_%s' % (mongodbIp,strYear))
    sc =SparkContext(conf=conf)
    logger = sc._jvm.org.apache.log4j
    logger.LogManager.getRootLogger().setLevel(logger.Level.FATAL)
    
    sqlContext = SQLContext(sc)
    u_employee_trans = udf( employee_trans )
    
    # Load the data
    url_String='jdbc:oracle:thin:apps/apps@140.110.143.144:1524/TESTDEV'
    if (db_location=='PROD'):
        url_String='jdbc:oracle:thin:apps/apps0677@140.110.143.148:1524/PROD'
        
    tblname="(select * from (select v.legal_entity_id as org_id, org.name as org_name, v.dept_id as dept_id, dept.name as dept_name, v.emp_id as emp_id, emp.emp_name, emp.employee_number as emp_number, v.sub_hours, f.ferial_name, v.leave_date from narl_leave_detail_info_v v, narl_leave_main m, narl_ferial_header f, narl_login_emp_info_hist_v emp, HR_ALL_ORGANIZATION_UNITS org, HR_ALL_ORGANIZATION_UNITS dept where v.leave_id=m.leave_id and m.ferial_code=f.ferial_code and v.emp_id=emp.employee_id and v.legal_entity_id=org.ORGANIZATION_ID and v.dept_id=dept.ORGANIZATION_ID and v.status in ('APPROVE','INPROCESS','PROCESSING','FREE') and TO_CHAR(v.leave_date,'YYYY')='%s') ORDER BY org_id, dept_id, emp_id) tmp" %strYear
    
    df= sqlContext.read.format('jdbc').options(url=url_String, dbtable=tblname).load() 
    #oracle 取出值，其欄位都是大寫
    df = df.select(df.ORG_ID.cast('int').alias('org_id'),df.ORG_NAME.alias('org_name'), df.DEPT_ID.cast('int').alias('dept_id'), df.DEPT_NAME.alias('dept_name'), df.EMP_ID.cast('int').alias('emp_id'), df.EMP_NAME.alias('emp_name'),df.EMP_NUMBER.alias('emp_number'),date_format(df.LEAVE_DATE, 'E').alias('name_day'),concat(lit('Day_'),date_format(df.LEAVE_DATE,'dd')).alias('day_month') ,df.FERIAL_NAME.alias('ferial_name'), df.SUB_HOURS.cast('int').alias('sub_hours'))
    
    df = df.withColumn( 'employee_num', u_employee_trans('emp_number') ).drop('emp_number')
    df = df.withColumnRenamed("employee_num", "emp_number")
    df.cache()
    
    #Load org by WEEK data--start
    print 'start Load org by WEEK data>>', datetime.datetime.now()
    df_groupBy_org_name_day = df.select('org_id','org_name','name_day','ferial_name', 'sub_hours').groupBy('org_id','org_name','name_day').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_org_name_day=df_groupBy_org_name_day.fillna(0)
    
    df_groupBy_org_name_day = df_groupBy_org_name_day.select('org_id','org_name','name_day', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('org_id','org_name').pivot("name_day", ['Mon', 'Tue', 'Wed','Thu', 'Fri', 'Sat','Sun']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_org_name_day=df_groupBy_org_name_day.fillna(0)
    df_groupBy_org_name_day=df_groupBy_org_name_day.orderBy(df_groupBy_org_name_day.org_id)   
    print 'end Load org by WEEK data>>', datetime.datetime.now()
    #Load org by WEEK data--end
    
     #Load org by DAY 每月的1,2~30、31 data--start
    print 'start Load org by DAY 每月的1,2~30、31 data>>', datetime.datetime.now()
    df_groupBy_org_day_month = df.select('org_id','day_month','ferial_name', 'sub_hours').groupBy('org_id','day_month').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_org_day_month=df_groupBy_org_day_month.fillna(0)
    
    df_groupBy_org_day_month = df_groupBy_org_day_month.select('org_id','day_month', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('org_id').pivot("day_month", ['Day_1', 'Day_2', 'Day_3','Day_4', 'Day_5', 'Day_6','Day_7','Day_8','Day_9','Day_10','Day_11','Day_12','Day_13','Day_14','Day_15','Day_16','Day_17','Day_18','Day_19','Day_20','Day_21','Day_22','Day_23','Day_24','Day_25','Day_26','Day_27','Day_28','Day_29','Day_30','Day_31']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_org_day_month=df_groupBy_org_day_month.fillna(0)
    df_groupBy_org_day_month=df_groupBy_org_day_month.orderBy(df_groupBy_org_day_month.org_id)   
    print 'end Load org by DAY 每月的1,2~30、31 data>>', datetime.datetime.now()
    #Load org by DAY 每月的1,2~30、31  data--end
    
    #Load dept by WEEK data--start
    print 'start Load dept by WEEK data>>', datetime.datetime.now()
    df_groupBy_dept_name_day = df.select('dept_id','dept_name','name_day','ferial_name', 'sub_hours').groupBy('dept_id','dept_name','name_day').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_dept_name_day=df_groupBy_dept_name_day.fillna(0)
    
    df_groupBy_dept_name_day = df_groupBy_dept_name_day.select('dept_id','dept_name','name_day', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('dept_id','dept_name').pivot("name_day", ['Mon', 'Tue', 'Wed','Thu', 'Fri', 'Sat','Sun']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_dept_name_day=df_groupBy_dept_name_day.fillna(0)
    df_groupBy_dept_name_day=df_groupBy_dept_name_day.orderBy(df_groupBy_dept_name_day.dept_id)   
    print 'end Load dept by WEEK data>>', datetime.datetime.now()
    #Load dept by WEEK data--end
    
    
    #Load dept by DAY 每月的1,2~30、31 data--start
    print 'start Load dept by DAY 每月的1,2~30、31  data>>', datetime.datetime.now()
    df_groupBy_dept_day_month = df.select('dept_id','day_month','ferial_name', 'sub_hours').groupBy('dept_id','day_month').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_dept_day_month=df_groupBy_dept_day_month.fillna(0)
    
    df_groupBy_dept_day_month = df_groupBy_dept_day_month.select('dept_id','day_month', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('dept_id').pivot("day_month", ['Day_1', 'Day_2', 'Day_3','Day_4', 'Day_5', 'Day_6','Day_7','Day_8','Day_9','Day_10','Day_11','Day_12','Day_13','Day_14','Day_15','Day_16','Day_17','Day_18','Day_19','Day_20','Day_21','Day_22','Day_23','Day_24','Day_25','Day_26','Day_27','Day_28','Day_29','Day_30','Day_31']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_dept_day_month=df_groupBy_dept_day_month.fillna(0)
    df_groupBy_dept_day_month=df_groupBy_dept_day_month.orderBy(df_groupBy_dept_day_month.dept_id)   
    print 'end Load dept by DAY 每月的1,2~30、31  data>>', datetime.datetime.now()
    #Load dept by DAY 每月的1,2~30、31  data--end
    
    #Load emp by WEEK data--start
    print 'start Load dept by WEEK data>>', datetime.datetime.now()
    df_groupBy_emp_name_day = df.select('emp_id','emp_name','emp_number','name_day','ferial_name', 'sub_hours').groupBy('emp_id','emp_name','emp_number','name_day').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_emp_name_day=df_groupBy_emp_name_day.fillna(0)
    
    df_groupBy_emp_name_day = df_groupBy_emp_name_day.select('emp_id','emp_name','emp_number','name_day', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('emp_id','emp_name','emp_number').pivot("name_day", ['Mon', 'Tue', 'Wed','Thu', 'Fri', 'Sat','Sun']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_emp_name_day=df_groupBy_emp_name_day.fillna(0)
    df_groupBy_emp_name_day=df_groupBy_emp_name_day.orderBy(df_groupBy_emp_name_day.emp_id)   
    print 'end Load dept by WEEK data>>', datetime.datetime.now()
    #Load emp by WEEK data--end
    
    #Load emp by DAY 每月的1,2~30、31 data--start
    print 'start Load emp by DAY 每月的1,2~30、31  data>>', datetime.datetime.now()
    df_groupBy_emp_day_month = df.select('emp_id','day_month','ferial_name', 'sub_hours').groupBy('emp_id','day_month').pivot("ferial_name",['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假', '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']).sum('sub_hours')
    
    df_groupBy_emp_day_month=df_groupBy_emp_day_month.fillna(0)
    
    df_groupBy_emp_day_month = df_groupBy_emp_day_month.select('emp_id','day_month', '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假', '事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假').groupBy('emp_id').pivot("day_month", ['Day_1', 'Day_2', 'Day_3','Day_4', 'Day_5', 'Day_6','Day_7','Day_8','Day_9','Day_10','Day_11','Day_12','Day_13','Day_14','Day_15','Day_16','Day_17','Day_18','Day_19','Day_20','Day_21','Day_22','Day_23','Day_24','Day_25','Day_26','Day_27','Day_28','Day_29','Day_30','Day_31']).sum('特別休假', '加班或假日出差轉補休','生理假','傷病假','婚假','家庭照顧假','事假','產檢假', '陪產假','產假','喪假','國內公假','國外公假','公傷病假', '安胎假')
    
    df_groupBy_emp_day_month=df_groupBy_emp_day_month.fillna(0)
    df_groupBy_emp_day_month=df_groupBy_emp_day_month.orderBy(df_groupBy_emp_day_month.emp_id)   
    print 'end Load emp by DAY 每月的1,2~30、31  data>>', datetime.datetime.now()
    #Load emp by DAY 每月的1,2~30、31  data--end
    
    #Load data to mongodb-->start
    print 'start to save org data>>', datetime.datetime.now()
    df_org_all=df_groupBy_org_name_day.join(df_groupBy_org_day_month, df_groupBy_org_name_day.org_id == df_groupBy_org_day_month.org_id, 'inner').drop(df_groupBy_org_day_month.org_id)
    df_org_all.write.format("com.mongodb.spark.sql.DefaultSource").mode("append").save()
    
    print 'start to save dept data>>', datetime.datetime.now()
    df_dept_all=df_groupBy_dept_name_day.join(df_groupBy_dept_day_month, df_groupBy_dept_name_day.dept_id == df_groupBy_dept_day_month.dept_id, 'inner').drop(df_groupBy_dept_day_month.dept_id)
    df_dept_all.write.format("com.mongodb.spark.sql.DefaultSource").mode("append").save()
    
    print 'start to save emp data>>', datetime.datetime.now()
    df_emp_all=df_groupBy_emp_name_day.join(df_groupBy_emp_day_month, df_groupBy_emp_name_day.emp_id == df_groupBy_emp_day_month.emp_id, 'inner').drop(df_groupBy_emp_day_month.emp_id)
    df_emp_all.write.format("com.mongodb.spark.sql.DefaultSource").mode("append").save()    
    #Load data to mongodb-->end    
    
    sc.stop()
    
    # END    
    print 'end .....>> hrvisual.LEAVE_TYPE_%s' % strYear, ' ', datetime.datetime.now()
    
    