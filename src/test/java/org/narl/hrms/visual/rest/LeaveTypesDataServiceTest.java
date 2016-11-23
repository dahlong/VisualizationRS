package org.narl.hrms.visual.rest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class LeaveTypesDataServiceTest extends JerseyTest {

	@Override
    protected Application configure() {
        return new ResourceConfig(LeaveTypesDataService.class);
    }
	
	@Test
	public void test1() {
		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    formData.add("year", "2015");
	    formData.add("emp_id", "3980");
	    formData.add("month", "7");
	    //Response response = target("/leavetypesdata/postFindLeaveEmpTotalAvg").request().post(Entity.form(formData));
	    final String response = target("/leavetypesdata/postFindLeaveEmpTotalAvg").request().post(Entity.form(formData),String.class);
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
	
	//@Test
	public void test2(){
		Client client = Client.create();
		WebResource webResource = client
				   .resource("http://localhost:8080/VisualizationRS/");
		
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("year", "2015");
		formData.add("emp_id", "3980");
		formData.add("month", "7");
		
		//JSONObject json 
		ClientResponse response = webResource.path("/leavetypesdata/postFindLeaveEmpTotalAvg")
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
			    .post(ClientResponse.class, formData);
				//.get(JSONObject.class);
		
		String textEntity = response.getEntity(String.class);
		System.out.println("===================>>>>"+textEntity);
		
	}
	
	
	
}
