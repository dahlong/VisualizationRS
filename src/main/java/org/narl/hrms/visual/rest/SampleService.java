package org.narl.hrms.visual.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.narl.hrms.visual.mongo.collection.Overtime;
import org.narl.hrms.visual.mongo.service.OvertimeServiceImpl;
import org.narl.hrms.visual.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.ws.rs.PathParam;

@Path("rest")
public class SampleService {

	@Autowired
	TestService test ;

	@Autowired
	OvertimeServiceImpl otest ;
	
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
		return test.sayHi(name);
		//return "Hello World, "+name ;
	}
	
	@POST
	@Path("postTest")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Produces(MediaType.TEXT_HTML) //有中文 return不行
	@Produces(MediaType.TEXT_XML)
	public String postMethod(@FormParam("yearmonth") String yearmonth, @FormParam("org") String org ) {
	  return "<h2>Hello, post (年-月)= " + yearmonth + ", 組織=" +org +"</h2>";
	}
	

	
	/**
	 * findAll 國外出差地點
	 * filter : organization
	 */
	@POST
	@Path("postFindByOrg")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Overtime> postFindOTByOrg(@FormParam("yearmonth") String yearmonth, @FormParam("org") String org ) {
		Query query = new Query(Criteria.where("forg").is(org));
		query.with(new Sort(Sort.Direction.DESC, "fmonth"));
		List<Overtime> list = otest.findByQuery(query);
		return list;
	}
	
	@POST
	@Path("postFindAll")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Overtime> postFindAllOT(@FormParam("yearmonth") String yearmonth, @FormParam("org") String org ) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "fmonth"));
		List<Overtime> list = otest.findAll();
		return list;
	}
	
}


