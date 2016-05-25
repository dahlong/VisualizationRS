package org.narl.hrms.visual.mongo;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase; 

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfigForUnitTest.class, loader = AnnotationConfigContextLoader.class)
public class MongoTest {


	@Autowired
	CommondServiceImpl cmdtest ;
	
	ApplicationContext ctx = null;
	
	MongoClient client =null;
	
	MongoDatabase database =null;
	@Before
	public void init() {
		ctx = new GenericXmlApplicationContext( "/applicationContext.xml");
		client = new MongoClient();
		database = client.getDatabase("hrvisual");
		
	}
	
	
	
	/**
	 * dos command to import tsv
	 */
	@Test
	public void testImportCommand() {
		Runtime r = Runtime.getRuntime();
		  Process p = null;
		  String command =  "D:/MongoDB/Server/3.2/bin/mongoimport --host=127.0.0.1 -d hrvisual -c example_ferial_code_14_2015 --type tsv --file d:/test/example_ferial_code_14_2015.tsv --headerline";

		  try {
		   p = r.exec(command);
		   System.out.println("Reading tsv into Database");

		  } catch (Exception e){
		   System.out.println("Error executing " + command + e.toString());
		  }
	}
	
	@Test
	public void testDropCollection(){
		cmdtest.dropCollection("TEST");	
	}
	
	@Test
	public void testCreateNewCollection(){
		DBCollection dbcollection = cmdtest.createCollection("TEST");
		assertEquals("TEST", dbcollection.getName());
	}
	
	@Test
	public void testCalYearMonth() throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		java.util.Date sd=sdf.parse("2015/12/31");
		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(sd);
		int startYear = calendarStart.get(Calendar.YEAR);
		int startMonth = calendarStart.get(Calendar.MONTH);
		int startDay = calendarStart.get(Calendar.DAY_OF_MONTH);
		
		//get corrent year
		Calendar today = Calendar.getInstance();   // Gets the current date and time
		int curYear = today.get(Calendar.YEAR);
		int curMonth = today.get(Calendar.MONTH);
		int curDay = today.get(Calendar.DAY_OF_MONTH);
		
		// return end of date of year		
		Calendar calendarEnd=Calendar.getInstance();
	    calendarEnd.set(Calendar.YEAR, curYear);
	    calendarEnd.set(Calendar.MONTH,11);
	    calendarEnd.set(Calendar.DAY_OF_MONTH,31);
	    int endYear = calendarEnd.get(Calendar.YEAR);
		int endMonth = calendarEnd.get(Calendar.MONTH);
		int endDay = calendarEnd.get(Calendar.DAY_OF_MONTH);
	     
	     
	     int age = endYear - startYear;
	     if (endMonth < startMonth || (startMonth == endMonth && endDay < startDay)) {
	         age--;
	     }
	     
	     int mons=endMonth-startMonth;
	     if (endDay>=startDay) {
	    	 mons++;
	     }
	     
	     if (mons>=12) {
	    	 mons=mons-12;
	    	 age++;
	     }
	    	 
	     System.out.println(">>" +calendarStart.getTime() +"~" +calendarEnd.getTime() +">> years=" +age +", month="+mons);
	}
	
	/**
	 * test connection to ubuntu mongodb
	 */
	@Test
	public void testConnection(){
		Builder o = MongoClientOptions.builder().connectTimeout(3000);  
		MongoClient mongo = new MongoClient(new ServerAddress("192.168.187.128", 27017), o.build());    

		try {
		  
			ServerAddress a = mongo.getAddress();			
			System.out.println(">>" +a);
			
			MongoDatabase db = mongo.getDatabase("hrvisual");
			MongoCollection<Document> col = db.getCollection("LEAVE_TYPE_2015");
			System.out.println(">>" +col.count());
			
		} catch (Exception e) {
		  System.out.println("Mongo is down >>" +e.toString());
		  mongo.close();
		  return;
		}
	}
}
