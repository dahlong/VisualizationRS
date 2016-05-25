package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
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
import org.narl.hrms.visual.rest.output.EmpOutput;
import org.narl.hrms.visual.rest.output.LeaveTypesOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

@Path("leavetypesdata")
public class LeaveTypesDataService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveTypesDataService.class);
	
	@Autowired 
	MongoTemplate mongoTemplate;	
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	String collectionName="LEAVE_TYPE_";
		
	
	/**
	 *  角色:1,2
	 * 只選組織，部門與人員為全選
	 */
	@POST
	@Path("postFindLeaveOrgTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveOrgTotal(@FormParam("org_id") String org_id,	@FormParam("year") String yearString) {
		
		logger.info("postFindLeaveOrgTotal>>  org_id: " +org_id +",yearString: "+yearString );
		
		//角色:1, 可能中心為全選
//		if (yearString==null || org_id.equals("-1"))
		if (yearString==null )	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		
		 String[] fs=commondServiceImpl.ferial_names;
		 for (int i=0;i<fs.length;i++) {
			 String ferial_name=fs[i];
			 
			 Criteria criteriaDefinition = new Criteria();
			 if (!org_id.equals("-1")) {
				 criteriaDefinition.andOperator(Criteria.where("Total").exists(true), Criteria.where("Total."+ferial_name).gte(0), Criteria.where("org_id").is(org_id));
			 } else {
			 		 criteriaDefinition=Criteria.where("Total."+ferial_name).gte(0);
			 }
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group("org_name").sum("Total."+ferial_name).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 
			 AggregationResults groupResults = mongoTemplate.aggregate(
					    aggregation, collectName, LeaveTypesOutput.class);
					  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("id", a.getIid());
				  n1.append("ferial_name", ferial_name);
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);
			  }
		 }
		return result;
	}


	/**
	 * 角色:1,2,3
	 * 只選組織與部門，人員為全選
	 * org_id<>-1 & dept_id<>-1
	 */
	@POST
	@Path("postFindLeaveDeptTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("org_id") String org_id,	@FormParam("year") String yearString,
			@FormParam("dept_id") final String dept_id) {
		
		logger.info("postFindLeaveDeptTotal>>  org_id: " +org_id +", dept_id: +" +dept_id +", yearString: "+yearString );
		
		if (yearString==null || org_id.equals("-1") || dept_id.equals("-1"))
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 String[] fs=commondServiceImpl.ferial_names;
		 for (int i=0;i<fs.length;i++) {
			 String ferial_name=fs[i];
			 
			 Criteria criteriaDefinition = new Criteria();
			 String groupName="dept_name";
			criteriaDefinition.andOperator(Criteria.where("Total").exists(true), Criteria.where("Total."+ferial_name).gte(0), Criteria.where("dept_id").is(dept_id));
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group(groupName).sum("Total."+ferial_name).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 
			 AggregationResults groupResults = mongoTemplate.aggregate(
					    aggregation, collectName, LeaveTypesOutput.class);
					  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("id", a.getIid());
				  n1.append("ferial_name", ferial_name);
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);	
			  }
		 }
		return result;
	}
	
	/**
	 * 角色:1,2,3,4
	 * 選組織，部門與人員
	 */
	@POST
	@Path("postFindLeaveEmpTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveEmpTotal(@FormParam("year") String yearString, @FormParam("emp_id") final String emp_id) {
		
		logger.info("postFindLeaveDeptTotal>>  emp_id: " +emp_id +", yearString: "+yearString );
		
		if (yearString==null ||  emp_id.equals("-1"))
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=new ArrayList<Document>();
		 String[] fs=commondServiceImpl.ferial_names;
		 for (int i=0;i<fs.length;i++) {
			 String ferial_name=fs[i];
			 
			 Criteria criteriaDefinition = new Criteria();
			 String groupName="emp_name";
			criteriaDefinition.andOperator(Criteria.where("Total").exists(false), Criteria.where(ferial_name).gte(0),Criteria.where("emp_id").is(emp_id));
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group(groupName).sum(ferial_name).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 
			 AggregationResults groupResults = mongoTemplate.aggregate(
					    aggregation, collectName, LeaveTypesOutput.class);
					  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("id", a.getIid());
				  n1.append("ferial_name", ferial_name);
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);	
			  }
		 }
		return result;
	}
	
	

}


