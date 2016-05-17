package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.narl.hrms.visual.rest.output.LeaveTypesOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

@Path("leavetypestop")
public class LeaveTypesTopService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveTypesTopService.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	String collectionName="LEAVE_TYPE_";
	
	
	/**
	 *  角色:1，super user
	 *  組織 : 全選或選一
	 *  部門:全選
	 * 
	 *  角色:2，HR
	*   組織 : 選一(自己的中心)
	 *  部門:全選
	 *  */
	@POST
	@Path("postFindLeaveAllTotalTop")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveAllTotalTop(@FormParam("year") String yearString, @FormParam("org_id") String org_id,
			@FormParam("ferial_name") String ferial_name, @FormParam("n") String n) {
		
		logger.info("postFindLeaveAllTotalTop>>  org_id: " +org_id +", ferial_name: +" +ferial_name +", yearString: "+yearString +",n: " +n );
		
		if (yearString==null )	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 Aggregation aggregation =null;
		 Criteria criteriaDefinition = new Criteria();
		if (org_id.equals("-1")) {
			criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(ferial_name).gt(0));
		} else {
			criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(ferial_name).gt(0), Criteria.where("org_id").is(org_id));
		}
		aggregation = newAggregation(
				 match(criteriaDefinition),   
				 group("emp_name", "org_name").sum(ferial_name).as("total"),
				 sort(Sort.Direction.DESC, "total")  ,
				 limit(Long.valueOf(n))
			  );

		AggregationResults groupResults = mongoTemplate.aggregate(
		    aggregation, collectName, LeaveTypesOutput.class);
		  
		  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
		  for (LeaveTypesOutput a : aggList) {
			  Document n1=new Document("emp_name", a.getEmp_name());
			  n1.append("org_name", a.getOrg_name());
			  n1.append("ferial_name", ferial_name);
			  n1.append("leave_hours", a.getTotal());
			  result.add(n1);	
		  }

		  
		return result;
	}

	

	/**
	 * 角色:1,2,3
	 * 部門: 選一個 (角色3為自己所在部門)
	 */
	@POST
	@Path("postFindLeaveDeptTotalTop")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("year") String yearString,  @FormParam("org_id") String org_id,	
			@FormParam("dept_id") final String dept_id,  @FormParam("ferial_name") String ferial_name,  @FormParam("n") String n) {
		
		logger.info("postFindLeaveDeptTotalTop>>  org_id: " +org_id +", dept_id: " +dept_id +", ferial_name: +" +ferial_name +", yearString: "+yearString +",n: " +n );
		
		if (yearString==null || dept_id.equals("-1"))	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;		
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 Aggregation aggregation =null;
		Criteria criteriaDefinition = new Criteria();
		criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(ferial_name).gt(0), Criteria.where("org_id").is(org_id),Criteria.where("dept_id").is(dept_id));
		
		 aggregation = newAggregation(
				 match(criteriaDefinition),   
				 group("emp_name", "org_name").sum(ferial_name).as("total"),
				 sort(Sort.Direction.DESC, "total")  ,
				 limit(Long.valueOf(n))
			  );
		 
		 
			AggregationResults groupResults = mongoTemplate.aggregate(
			    aggregation, collectName, LeaveTypesOutput.class);
			  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("emp_name", a.getEmp_name());
				  n1.append("org_name", a.getOrg_name());
				  n1.append("ferial_name", ferial_name);
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);	
			  }

			  
			return result;
	}
	

	
	
}

