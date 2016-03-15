package org.narl.hrms.visual.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.PathParam;

@Path("rest")
public class SampleService {

	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("sayHello")
	public String HelloWorld2(){
		return "Hello World !!" ;
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/sayHelloByName/{name}")
	public String HelloWorld(@PathParam("name") String name){
		return "Hello World, "+name ;
	}
	
}
