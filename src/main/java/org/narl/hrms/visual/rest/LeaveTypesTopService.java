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
	 * 組織 & 部門可選或為全選
	 * 
	 *  角色:2，HR
	 *  組織固定，部門可選或為全選
	 *  */
	@POST
	@Path("postFindLeaveAllTotalTop")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveAllTotalTop(@FormParam("year") String yearString, @FormParam("org_id") String org_id,
			@FormParam("ferial_name") String ferial_name, @FormParam("n") String n) {
		
		collectionName+=yearString;
		if (!commondServiceImpl.collectionExisted(collectionName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 Aggregation aggregation =null;
		 Criteria criteriaDefinition = new Criteria();
		if (org_id.equals("-1")) {
			criteriaDefinition.andOperator(Criteria.where(ferial_name).gt(0));
		} else {
			criteriaDefinition.andOperator(Criteria.where(ferial_name).gt(0), Criteria.where("org_id").is(Integer.valueOf(org_id)));
		}
		aggregation = newAggregation(
				 match(criteriaDefinition),   
				 group("emp_name", "org_name").sum(ferial_name).as("total"),
				 sort(Sort.Direction.DESC, "total")  ,
				 limit(Long.valueOf(n))
			  );

		AggregationResults groupResults = mongoTemplate.aggregate(
		    aggregation, collectionName, LeaveTypesOutput.class);
		  
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
	 * 有選部門時
	 */
	@POST
	@Path("postFindLeaveDeptTotalTop")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("year") String yearString,
			@FormParam("org_id") String org_id,	
			@FormParam("dept_id") final String dept_id,
			@FormParam("ferial_name") String ferial_name,
			@FormParam("n") String n) {
		
		collectionName+=yearString;
		if (!commondServiceImpl.collectionExisted(collectionName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 Aggregation aggregation =null;
		 Criteria criteriaDefinition = new Criteria();
		 if (dept_id.equals("-1")) {
				criteriaDefinition.andOperator(Criteria.where(ferial_name).gt(0), Criteria.where("org_id").is(Integer.valueOf(org_id)));
				 
		 } else {
			criteriaDefinition.andOperator(Criteria.where(ferial_name).gt(0), Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(dept_id));
		 }
		 aggregation = newAggregation(
				 match(criteriaDefinition),   
				 group("emp_name", "org_name").sum(ferial_name).as("total"),
				 sort(Sort.Direction.DESC, "total")  ,
				 limit(Long.valueOf(n))
			  );
		 
		 
			AggregationResults groupResults = mongoTemplate.aggregate(
			    aggregation, collectionName, LeaveTypesOutput.class);
			  
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

