package org.narl.hrms.visual.mongo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON; 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfigForUnitTest.class, loader = AnnotationConfigContextLoader.class)
public class MongoLeaveTypesDataTest {

	@Autowired 
	MongoTemplate mongoTemplate;
	
	@Autowired
	CommondServiceImpl cmdtest ;
	
	ApplicationContext ctx = null;
	
//	MongoClient client =null;
	
//	MongoDatabase database =null;
	
	MongoOperations mongoOperation=null;
	
	MongoTemplate temple=null;
	
	@Before
	public void init() {
		
//		ctx = new GenericXmlApplicationContext( "/applicationContext.xml");
//		mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
//		client = new MongoClient();
//		database = client.getDatabase("hrvisual");
		
	}


	@Test
	public void testConnection() {
		String strYear="2015";
		String collectionName="LEAVE_TYPE_"+strYear;
		DBCollection collection = mongoTemplate.getCollection(collectionName);
		System.out.println(">>" +collection.count());
		{
			
		}
		
	}
	
	@Test
	public void testLeaveTypeCount(){
		System.out.println(" 年度各類別請假次數統計 Test=====================================");
//		assertEquals(200,testservice.count());
		String strYear="2015";
		String collectionName="LEAVE_TYPE_"+strYear;
		DBCollection collection = mongoOperation.getCollection(collectionName);
		{
			System.out.println(">>Count=" +collection.count());
		}
		
		{
//			Query query = new Query(Criteria.where("org_id").is("82"));
//			query.with(new Sort(Sort.Direction.DESC, "emp_id"));
//			List<LeaveTypes> list = testservice.findByQuery(query, collectionName);
			
			DBObject query = null;
			String usuarioJSON = "{\"org_id\":\"82\"}";
			DBObject jsonObject = (DBObject) JSON.parse(usuarioJSON);
			
			DBCursor cursor = collection.find(jsonObject);
			if(cursor.hasNext()) {
			       DBObject obj = cursor.next();
			       System.out.println("find by DBObject >>" +obj);
			    }			
		}		
		
		
//		{
//			Sort s=new Sort(Sort.Direction.DESC, "org_id");
//	    	 List<LeaveTypes> list = testservice.findAll(s, collectionName);
//	    	 for (LeaveTypes b : list) {
//					System.out.println("find all and sort>>" +b);
//				}        
//		}
		
		
//		{
//			 Query query = new Query(Criteria.where("emp_name").is("林雅雯"));
//				query.with(new Sort(Sort.Direction.DESC, "ferial_name"));
//				List<LeaveTypes> list = testservice.findByQuery(query, collectionName);
//				 for (LeaveTypes b : list) {
//					System.out.println("find by name>>" +b);
//				}
//		}
		
	}
	
	/**
	 * 年度各類別請假次數統計
	 * convert from hrdev to mongodb
	 * @throws IOException 
	 */
//	@Test
//	public void testLeaveCount_convert_FromsqltoNosql_2() throws IOException{
//		{
//			String strYear="2010";
//			String ysDate=strYear+"/1/1";
//			String yeDate=strYear+"/12/31";
//			String collectionName="LEAVE_TYPES_DATA_"+strYear;
//			MongoCollection<Document> collection = database.getCollection(collectionName);
//		    
//		    LeaveTypesDataDaoImpl tf = ctx.getBean(LeaveTypesDataDaoImpl.class);
//		    
//			String orgId="84";
//			List<EmpVo> empList = tf.getEmpList(ysDate, orgId);
//			List<FerialHeaderVo> ferialList = tf.getFerialHeader(ysDate, yeDate);
//			
//			for (EmpVo emp : empList) {
//				List<Document> documents=new ArrayList<Document>();
//				for (int mon=1;mon<=12;mon++) {
//					for(FerialHeaderVo fvo : ferialList) {
//						Document  result=tf.getDocument(ysDate, yeDate, emp, mon, fvo);
//						if (result==null) {
//							Document document = new Document();
//							document.append("org_id", emp.getOrgId());
//							document.append("org_name", emp.getOrgName());
//							document.append("dept_id", emp.getDeptId());
//							document.append("dept_name", emp.getDeptName());
//							document.append("emp_id", emp.getEmpId());
//							document.append("emp_name", emp.getEmpName());
//							document.append("emp_number", emp.getEmpNumber());
//							document.append("leave_start_month", String.valueOf(mon));
//							document.append("ferial_name", fvo.getName());
//							document.append("leave_hours",0);
//							document.append("qty", 0);
//							documents.add(document);
//							
//						} else {
//							documents.add(result);
//						}
//					}
//				}
//				System.out.println(">>get data from SQL , list size:" +documents.size());
//				collection.insertMany(documents);
//			}
//		}
//		
//	}
	
	/**
	 * This one
	 * @throws IOException
	 */
//	@Test
//	public void testLeaveCount_convert_FromsqltoNosql_3() throws IOException{
//		String[] orgArray={"82","83","84","85","86","87","88","89","90","91","92","93"};
//			String strYear="2016";
//			String ysDate=strYear+"/1/1";
//			String yeDate=strYear+"/12/31";
//			String collectionName="LEAVE_TYPES_"+strYear;
//			
//			MongoCollection<Document> collection = database.getCollection(collectionName);
//			//cmdtest.dropCollection(collectionName);
//			
//		    LeaveTypesDataDaoImpl tf = ctx.getBean(LeaveTypesDataDaoImpl.class);
//		    
//			String orgId=orgArray[11];
//			String org_name=tf.getOrgName(orgId);
//			
//			
//			List<FerialHeaderVo> ferialList = tf.getFerialHeader(ysDate, yeDate);
//			for (int mon=1;mon<=12;mon++) {
//				String jsonresult = "{ 'org_id' : "+orgId +",'org_name':'" +org_name +"',";
//				jsonresult+="'Month':'"+String.valueOf(mon)  +"',";
//				
//				//month total
//				jsonresult+="'Total':{";
//				String sDate=strYear+"/"+String.valueOf(mon) +"/1";
//				List<String> ftotal = new ArrayList<String>();
//				for(FerialHeaderVo fvo : ferialList) {
//					String total =  tf.getLeaveHours(ysDate, yeDate, orgId, sDate, fvo.getCode());
//					ftotal.add("'" +fvo.getName() +"':" + Integer.parseInt(total));
//				}
//				jsonresult+=StringUtils.join(ftotal, ",");		
//				jsonresult+="},";  
//				
//				jsonresult+="'Data' : {";
//				List<EmpVo> empList = tf.getEmpList(sDate, orgId);
//				for (int k=0; k<empList.size();k++) { 
//					EmpVo emp =empList.get(k);
//					jsonresult+="'" +emp.getEmpId() +"' : {";
//					jsonresult+="'dept_id':'" +emp.getDeptId() +"',";
//					jsonresult+="'dept_name':'" +emp.getDeptName() +"',";
//					jsonresult+="'emp_name':'" +emp.getEmpName() +"',";
//					jsonresult+="'emp_number':'" +emp.getEmpNumber() +"',";
//					
//					List<String> fresult = new ArrayList<String>();
//					for(FerialHeaderVo fvo : ferialList) {
//						String hours =  tf.getLeaveHours(sDate, strYear+"/12/31", emp, sDate, fvo);
//						fresult.add("'" +fvo.getName() +"':" + Integer.parseInt(hours));
//					}
//					jsonresult+=StringUtils.join(fresult, ",");					
//					jsonresult+="}";
//					
//					if (k<(empList.size()-1))
//							jsonresult+=", ";
//				}
//				jsonresult+="}"; //Data end
//				
//				jsonresult+="'Dept' : {";
//				
//				jsonresult+="}";
//				
//				jsonresult+="}";
//				//System.out.println(">>"+jsonresult);
//				Document myDoc = Document.parse(jsonresult);
//				collection.insertOne(myDoc);
//				//System.out.println("-----");
//					
//			}
//		
//	}
	
	/**
	 *中心的total
	 */
//	@Test
//	public void testLeaveCount_convert_FromsqltoNosql_6() throws IOException{
//		String[] orgArray={"82","83","84","85","86","87","88","89","90","91","92","93"};
//			String strYear="2015";
//			String ysDate=strYear+"/1/1";
//			String yeDate=strYear+"/12/31";
//			String collectionName="LEAVE_TYPE_"+strYear;
//			
//			MongoCollection<Document> collection = database.getCollection(collectionName);
//			//cmdtest.dropCollection(collectionName);
//			
//		    LeaveTypesDataDaoImpl tf = ctx.getBean(LeaveTypesDataDaoImpl.class);
//		    
//		    for (int i=0; i<orgArray.length;i++) {
//					String orgId=orgArray[i];
//					String org_name=tf.getOrgName(orgId);
//					
//					List<FerialHeaderVo> ferialList = tf.getFerialHeader(ysDate, yeDate);
//					for (int mon=1;mon<=12;mon++) {
//						String jsonresult = "{'org_id' : "+orgId +",'org_name':'" +org_name +"',";
//						jsonresult+="'month':'"+String.valueOf(mon)  +"',";
//						
//						//month total
//						jsonresult+="'Total':{";
//						String sDate=strYear+"/"+String.valueOf(mon) +"/1";
//						List<String> ftotal = new ArrayList<String>();
//						for(FerialHeaderVo fvo : ferialList) {
//							String total =  tf.getLeaveHours(ysDate, yeDate, orgId, sDate, fvo.getCode());
//							ftotal.add("'" +fvo.getName() +"':" + Integer.parseInt(total));
//						}
//						jsonresult+=StringUtils.join(ftotal, ",");		
//						jsonresult+="},";  
//						jsonresult+="}";
//						//System.out.println(">>"+jsonresult);
//						Document myDoc = Document.parse(jsonresult);
//						collection.insertOne(myDoc);
//						//System.out.println("-----");
//							
//					}
//		    }
//	}
	
	
//	@Test
//	public void testLeaveCount_convert_FromsqltoNosql_7() throws IOException{
//		String[] orgArray={"82","83","84","85","86","87","88","89","90","91","92","93"};
//			String strYear="2013";
//			String ysDate=strYear+"/1/1";
//			String yeDate=strYear+"/12/31";
//			String collectionName="LEAVE_TYPES_"+strYear;
//			
//			MongoCollection<Document> collection = database.getCollection(collectionName);
//			//cmdtest.dropCollection(collectionName);
//			
//		    LeaveTypesDataDaoImpl tf = ctx.getBean(LeaveTypesDataDaoImpl.class);
//		    
//			String orgId=orgArray[3];
//			String org_name=tf.getOrgName(orgId);
//			
//			List<FerialHeaderVo> ferialList = tf.getFerialHeader(ysDate, yeDate);
//			
//			for (int mon=1;mon<=12;mon++) {
//				String sDate=strYear+"/"+String.valueOf(mon) +"/1";
//				List<Document> documents=new ArrayList<Document>();
//				List<EmpVo> empList = tf.getEmpList(sDate, orgId);
//				for (int k=0; k<empList.size();k++) { 
//					String jsonresult = "{ 'org_id' : "+orgId +",'org_name':'" +org_name +"',";
//					jsonresult+="'month':'"+String.valueOf(mon)  +"',";
//					EmpVo emp =empList.get(k);
//					jsonresult+="'dept_id':'" +emp.getDeptId() +"',";
//					jsonresult+="'dept_name':'" +emp.getDeptName() +"',";
//					jsonresult+="'emp_id':'" +emp.getEmpId() +"',";
//					jsonresult+="'emp_name':'" +emp.getEmpName() +"',";
//					jsonresult+="'emp_number':'" +emp.getEmpNumber() +"',";
//					
//					List<String> fresult = new ArrayList<String>();
//					for(FerialHeaderVo fvo : ferialList) {
//						String hours =  tf.getLeaveHours(sDate, strYear+"/12/31", emp, sDate, fvo);
//						fresult.add("'" +fvo.getName() +"':" + Integer.parseInt(hours));
//					}
//					jsonresult+=StringUtils.join(fresult, ",");					
//					jsonresult+="}";
//					
//					Document myDoc = Document.parse(jsonresult);
//					documents.add(myDoc);
//				}
//				//System.out.println(">>"+jsonresult);
//				collection.insertMany(documents);
//				//System.out.println("-----");
//					
//			}
//		
//	}


//	@Test
//	public void testGetOrgList(){
//		CommondDaoImpl tf = ctx.getBean(CommondDaoImpl.class);
//		String login_id="6252";
//		List<OrgVo> list = tf.getOrgIdList(login_id);
//		for (OrgVo b : list) {
//			System.out.println(">>" +b.getOrgId() +", " +b.getOrgName());
//		}
//	}
//	
	@Test
	public void testDropCollection(){
		cmdtest.dropCollection("TEST");	
	}
	
	@Test
	public void testCreateNewCollection(){
		DBCollection dbcollection = cmdtest.createCollection("TEST");
		assertEquals("TEST", dbcollection.getName());
	}
	
}
