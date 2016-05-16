
# coding: utf-8

# In[3]:

# 從 HR oracle db 取請假資訊，存為 csv 檔
#input paremeter: year
import cx_Oracle
import os
import os.path
import csv

print cx_Oracle.version
os.environ["NLS_LANG"] = "AMERICAN_AMERICA.AL32UTF8"
orgArray=['82','83','84','85','86','87','88','89','90','91','92','93'];
#con = cx_Oracle.connect('apps/apps@140.110.143.144:1524/TESTDEV')
# Connect to tunnel of StandByDB 140.110.143.148
# $ docker exec -it containner_id /bin/bash -c "/root/sshTunnel.sh"
con = cx_Oracle.connect('apps/apps0677@127.0.0.1:1524/PROD')
print con.version
cur = con.cursor()

strYear='2015'
ysDate=strYear+'/1/1'
yeDate=strYear+'/12/31'
print ysDate, yeDate

#csvfilname = filname % (strYear, str("{:0>2d}".format(mon)))    
csvfilname = 'LEAVE_TYPE_%s.csv' % (strYear)


#write array to csv
def wirteDate(filename, writedata):
    f = open(filename,"w")  
    w = csv.writer(f)  
    w.writerows(writedata)  
    f.close() 

    
QUERY_LEAVE_ORG_HOURS_2="SELECT * FROM (                SELECT m.legal_entity_id as org_id, o.name as org_name, m.dept_id as dept_id, d.name as dept_name,                  m.emp_id as emp_id, p.last_name as emp_name, NVL(p.employee_number, p.npw_number) as emp_number,                 d.LEAVE_DATE as leave_date, m.ferial_code as ferial_code, m.detail_code as detail_code,                 TO_CHAR(d.LEAVE_DATE, 'MM') as month,d.SUB_HOURS as leave_hours, f.ferial_name AS ferial_name                 FROM narl_leave_main m, narl_leave_detail d, narl_sign_header h, narl_ferial_header f,                      HR_ALL_ORGANIZATION_UNITS d, HR_ALL_ORGANIZATION_UNITS o, PER_ALL_PEOPLE_F p                 WHERE m.leave_id = d.leave_id AND h.source_type = 'L'                 AND m.dept_id=d.organization_id AND m.legal_entity_id=o.organization_id                 AND p.person_id=m.emp_id                 AND m.leave_id = h.document_id                 AND h.status IN ('APPROVE','INPROCESS','PROCESSING','FREE')                 AND m.ferial_code=f.ferial_code                 AND d.LEAVE_DATE >=TO_DATE('%s', 'YYYY/MM/DD')                 AND d.LEAVE_DATE  <=TO_DATE('%s', 'YYYY/MM/DD')                 AND m.legal_entity_id='%s'                 AND TO_CHAR(d.LEAVE_DATE , 'MM')=TO_CHAR(TO_DATE('%s', 'YYYY/MM/DD'), 'MM')             )             PIVOT             (               SUM(Leave_Hours) FOR ferial_name IN ('特別休假','加班或假日出差轉補休','生理假','傷病假','婚假',               '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假')             )             ORDER BY emp_id, leave_date"    
            
        

arrayheader=['org_id','org_name','dept_id','dept_name','emp_id','emp_name','emp_number','leave_date','ferial_code','detail_code','month',             '特別休假','加班或假日出差轉補休','生理假','傷病假','婚假',               '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']

arrayferialname=['特別休假','加班或假日出差轉補休','生理假','傷病假','婚假',               '家庭照顧假','事假', '產檢假','陪產假','產假','喪假','國內公假','國外公假','公傷病假','安胎假']

arrayCVS=[]   
arrayCVS.append(arrayheader)
for mon in range(1, 13, 1):
    for orgId in orgArray:
        sDate="%s/%s/1" % (strYear, str(mon))
        leave_type_list=cur.execute(QUERY_LEAVE_ORG_HOURS_2 % (ysDate, yeDate, orgId, sDate))
        for row in leave_type_list:
            arrayData=[str(row[0]),str(row[1]),str(row[2]),str(row[3]),str(row[4]),str(row[5]),str(row[6]),                        row[7].strftime('%Y/%m/%d'),str(row[8]),str(row[9]),int(row[10])]
            #for i in range(len(arrayData)11, 26, 1):
            for i in range(len(arrayData), len(arrayData)+15, 1):
                if row[i] is not None:
                    arrayData.append(int(row[i]))
                else:
                    arrayData.append(int(0))
            
                       
            #print arrayData
            arrayCVS.append(arrayData)
        
        
    print len(arrayCVS)
    
    
wirteDate(csvfilname, arrayCVS)

cur.close()
print 'query end.....'
con.close()


