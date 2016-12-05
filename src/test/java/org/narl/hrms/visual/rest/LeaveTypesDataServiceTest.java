package org.narl.hrms.visual.rest;

import static org.junit.Assert.*;

import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.bson.Document;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/resources/applicationContext.xml" })
public class LeaveTypesDataServiceTest extends JerseyTest {

	@Autowired
	LeaveTypesDataService leaveTypesDataService ;
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	private static final Logger logger = LoggerFactory.getLogger(LeaveTypesDataServiceTest.class);
	
	@Override
    protected Application configure() {
        return new ResourceConfig(LeaveTypesDataService.class);
    }
	
	@Test
	public void postFindLeaveEmpTotalTest() {
		
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    formData.add("year", "2015");
	    formData.add("emp_id", "3980");
	    formData.add("month", "7");
	    
	    final String response = target("/leavetypesdata/postFindLeaveEmpTotal").request().post(Entity.form(formData),String.class);
	    
	    System.out.println("======================================>>>>>>>>>>>>>>>");
	    JSONArray ja = new JSONArray(response);
	    for (int i = 0; i < ja.length(); i++) {
	        JSONObject jo = ja.getJSONObject(i);
	        System.out.println(jo.get("id"));
	        System.out.println(jo.get("ferial_name"));
	        System.out.println(jo.get("leave_hours"));
	        //System.out.println(jo.get("category"));
	        System.out.println("=====================");
	    }   
	    
	    //assertEquals("楊大龍",ja.getJSONObject(0).);
	
	}
	
	@Test
	public void postFindLeaveEmpTotalAvgTest() {
		
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    formData.add("year", "2015");
	    formData.add("emp_id", "3980");
	    formData.add("month", "7");
	    formData.add("org_id", "84");
	    formData.add("dept_id", "458");
	    
	    final String response = target("/leavetypesdata/postFindLeaveEmpTotalAvg").request().post(Entity.form(formData),String.class);
	    
	    System.out.println("======================================>>>>>>>>>>>>>>>");
	    JSONArray ja = new JSONArray(response);
	    for (int i = 0; i < ja.length(); i++) {
	        JSONObject jo = ja.getJSONObject(i);
	        System.out.println(jo.get("id"));
	        System.out.println(jo.get("ferial_name"));
	        System.out.println(jo.get("leave_hours"));
	        System.out.println(jo.get("category"));
	        System.out.println("=====================");
	    }   
	    
	    //assertEquals("楊大龍",ja.getJSONObject(0).);
	
	}
	
	@Test
	public void postFindLeaveEmpTotalAvgWithMonthTest() {
		
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    formData.add("year", "2015");
	    formData.add("emp_id", "3980");
	    formData.add("month", "7");
	    formData.add("org_id", "84");
	    formData.add("dept_id", "458");
	    
	    final String response = target("/leavetypesdata/postFindLeaveEmpTotalAvg").request().post(Entity.form(formData),String.class);
	    
	    System.out.println("======================================>>>>>>>>>>>>>>>");
	    JSONArray ja = new JSONArray(response);
	    for (int i = 0; i < ja.length(); i++) {
	        JSONObject jo = ja.getJSONObject(i);
	        System.out.println(jo.get("id"));
	        System.out.println(jo.get("ferial_name"));
	        System.out.println(jo.get("leave_hours"));
	        System.out.println(jo.get("category"));
	        System.out.println("=====================");
	    }   
	    
	    //assertEquals("楊大龍",ja.getJSONObject(0).);
	
	}
	
	@Test
	public void getDeptAvgHoursByFerialNameTest(){
		String yearString =  "2015" ;
		//String emp_id = "3980" ;
		String monthString = "" ;
		String org_id = "84" ;
		String dept_id = "458" ;
	    String id = "楊大龍" ;
	    String collectName = "LEAVE_TYPE_" + yearString; ;
	    String groupName = "org_id" ;
	    String ferial_name = "國內公假" ;
	    String whereCond = "" + ferial_name + "";
	    if (!monthString.equals("")) {
			whereCond = monthString + "_sum(" + whereCond + ")";
		}
	    
	    List<Document> result= leaveTypesDataService.getDeptAvgHoursByFerialName(dept_id,org_id, monthString, id, collectName, groupName, ferial_name, whereCond);
	    
	    for(Document d : result){
	    	
	    	System.out.println();
	    	System.out.println(d.get("ferial_name"));
	    	System.out.println(d.get("leave_hours"));
	    	System.out.println(d.get("category"));
	    }
	    
	}
	
	@Test
	public void getOrgIdAvgHoursByFerialNameTest(){
		String yearString =  "2015" ;
		//String emp_id = "3980" ;
		String monthString = "" ;
		String org_id = "84" ;
		//String dept_id = "458" ;
	    String id = "楊大龍" ;
	    String collectName = "LEAVE_TYPE_" + yearString; ;
	    String groupName = "org_id" ;
	    String ferial_name = "國內公假" ;
	    String whereCond = "" + ferial_name + "";
	    if (!monthString.equals("")) {
			whereCond = monthString + "_sum(" + whereCond + ")";
		}
	    
	    List<Document> result= leaveTypesDataService.getOrgIdAvgHoursByFerialName(org_id, monthString, id, collectName, groupName, ferial_name, whereCond);
	    
	    for(Document d : result){
	    	
	    	System.out.println(d.get("id"));
	    	System.out.println(d.get("ferial_name"));
	    	System.out.println(d.get("leave_hours"));
	    	
	    }
	    
	    assertEquals(1, result.size());
	    assertEquals(new Float(145.18018), (Float)result.get(0).get("leave_hours"));
	    
	    
	}	
	
	// test2() is a case of using Jersey client library to test a REST API running on Server. 
	@Test
	public void test2(){
		Client client = Client.create();
		WebResource webResource = client
				   .resource("http://localhost:8080/VisualizationRS/");
		
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("year", "2015");
		formData.add("emp_id", "3980");
		formData.add("month", "");
		formData.add("org_id", "84");
		
		
		//JSONObject json 
		ClientResponse response = webResource.path("/leavetypesdata/postFindLeaveEmpTotalAvg")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
			    .post(ClientResponse.class, formData);
				//.get(JSONObject.class);
		
		String textEntity = response.getEntity(String.class);
		logger.info("===================>>>>"+textEntity);
		
	}
	
	
	
}
