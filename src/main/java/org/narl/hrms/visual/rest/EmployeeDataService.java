package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.narl.hrms.visual.rest.output.EmpCountOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

@Path("employeedate")
public class EmployeeDataService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeDataService.class);
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	String collectionName="ORG_DEPT_EMP_";
	
	
	/**
	 *  角色:1，super user
	 *  組織 : 全選或選一
	 *  部門:全選
	 * 
	 *  角色:2，HR
	*   組織 : 選一(自己的中心)
	 *  部門:全選
	 *  by:新增雇用、結束雇用
	 *  */
	@POST
	@Path("postFindEmpOrgTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindEmpOrgTotal(@FormParam("year") String yearString, @FormParam("org_id") String org_id,
			@FormParam("month") String monthString) {
		
		logger.info("postFindEmpOrgTotal>>  org_id: " +org_id +", yearString: "+yearString +", monthString: "+monthString );
		
		if (yearString==null )	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		final List<Document> result=getOrgDeptEmpCount(org_id,  collectName);
		 
		 String regex=yearString;
		 if (!monthString.equals("")) {
			 monthString=String.format("%02d", Integer.valueOf(monthString));
			 regex=regex+"/" +monthString +"/";
		 }
		 
		 
		 for (Map.Entry<String, String> entry : commondServiceImpl.byMap.entrySet()) {
			 String whereCond="start_date";		 
			 if (entry.getKey().equals("term"))
				 whereCond="end_date";
			 else if (entry.getKey().equals("now"))
				 whereCond="";
			 
			 Criteria criteriaDefinition = new Criteria();
			 if (org_id.equals("-1")) {
				 if (!whereCond.equals(""))
					 criteriaDefinition.andOperator(Criteria.where(whereCond).regex(regex, "i"));
			} else {
				 if (whereCond.equals(""))
					 criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)));
				 else
					 criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)), Criteria.where(whereCond).regex(regex, "i"));
			}
	
			 Aggregation aggregation=null;
			 if (org_id.equals("-1")) {
				 aggregation = newAggregation(
						 match(criteriaDefinition),   
						 group("org_name").count().as("count")
					  );	 
			 } else {
				  aggregation = newAggregation(
						 match(criteriaDefinition),   
						 group("org_name", "dept_name").count().as("count")
					  );	 
			 }
//			System.out.println(">>" +aggregation);
			AggregationResults groupResults = mongoTemplate.aggregate(
			    aggregation, collectName, EmpCountOutput.class);
			 
			  List<EmpCountOutput> aggList = groupResults.getMappedResults();
			  System.out.println(">>" +entry.getValue());
			  for (EmpCountOutput a : aggList) {
				  Document n1=new Document("name", org_id.equals("-1")?a.getIid():a.getDept_name());
				  n1.append("category", entry.getValue());
				  n1.append("count", 0);
				  if (result.contains(n1)) {
					result.remove(n1) ;
				  }
				  n1.remove("count");
				  n1.append("count", a.getCount());
				  if (!result.contains(n1)) {
					  result.add(n1);	
				 }
			  }
		 }
		return result;
	}

	

	/**
	 * 角色:1,2,3
	 * 部門: 選一個 (角色3為自己所在部門)
	 *  *  by:新增雇用、結束雇用
	 */
	@POST
	@Path("postFindEmpDeptTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("year") String yearString,  @FormParam("org_id") String org_id,	
			@FormParam("dept_id") final String dept_id,	@FormParam("month") String monthString) {
		
		logger.info("postFindEmpDeptTotal>>  org_id: " +org_id +", dept_id: " +dept_id +", yearString: "+yearString +", monthString: "+monthString );
		
		if (yearString==null || dept_id.equals("-1"))	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;		
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		
		final List<Document> result=getDeptEmpCount(org_id, dept_id, collectName);
		 String regex=yearString;
		 if (!monthString.equals("")) {
			 monthString=String.format("%02d", Integer.valueOf(monthString));
			 regex=regex+"/" +monthString +"/";
		 }
		 
		 for (Map.Entry<String, String> entry : commondServiceImpl.byMap.entrySet()) {
			 String whereCond="start_date";		 
			 if (entry.getKey().equals("term"))
				 whereCond="end_date";
			 else if (entry.getKey().equals("now"))
				 whereCond="";
			 
			Criteria criteriaDefinition = new Criteria();
			 if (!whereCond.equals(""))
				 criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(Integer.valueOf(dept_id)),  Criteria.where(whereCond).regex(regex, "i"));
			 else
				 criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
			
			Aggregation aggregation =null;
			 aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group("org_name","dept_name").count().as("count")
				  );
			
//			 System.out.println(">>" +aggregation);
			AggregationResults groupResults = mongoTemplate.aggregate(
			    aggregation, collectName, EmpCountOutput.class);
			 
			  List<EmpCountOutput> aggList = groupResults.getMappedResults();
			  for (EmpCountOutput a : aggList) {
				  Document n1=new Document("name", a.getDept_name());
				  n1.append("category", entry.getValue());
				  n1.append("count", 0);
				  if (result.contains(n1)) {
					result.remove(n1) ;
				  }
				  n1.remove("count");
				  n1.append("count", a.getCount());
				  if (!result.contains(n1)) {
					  result.add(n1);	
				  }
			  }

		 }
			return result;
	}
	
	
	
	/**
	 *已知中心、部門，取得部門人數
	 */
	public List<Document> getDeptEmpCount(String org_id,  String dept_id, String collectName) {

		final List<Document> result=new ArrayList<Document>();
		
		Criteria criteriaDefinition = new Criteria();
		 criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
		
		 Aggregation aggregation =null;
		aggregation = newAggregation(
				 match(criteriaDefinition),   
				 group("org_name","dept_name").count().as("count"),
				 sort(Sort.Direction.DESC, "count") 
			  );
		
//		System.out.println(">>" +aggregation);
		AggregationResults groupResults = mongoTemplate.aggregate(
		    aggregation, collectName, EmpCountOutput.class);
		  
		  List<EmpCountOutput> aggList = groupResults.getMappedResults();
		  
		  for (EmpCountOutput a : aggList) {
			  String name=a.getDept_name();
			  
			  Document n2=new Document("name", name);
			  n2.append("category", commondServiceImpl.byMap.get("new"));
			  n2.append("count", 0);	
			  result.add(n2);
			  
			  Document n3=new Document("name",name);
			  n3.append("category", commondServiceImpl.byMap.get("term"));
			  n3.append("count", 0);		
			  result.add(n3);
			  
			  Document n1=new Document("name", name);
			  n1.append("category", commondServiceImpl.byMap.get("now"));
			  n1.append("count", a.getCount());
			  result.add(n1);
			  
		  }


		return result;
	}
	
	
	/**
	 * 中心<>-1，取得各部門人數
	 * 中心==-1，取得各中心人數
	 */
	public List<Document> getOrgDeptEmpCount(String org_id, String collectName) {

		final List<Document> result=new ArrayList<Document>();
		 Aggregation aggregation =null;
		 
		 Criteria criteriaDefinition = new Criteria();
		 if (org_id.equals("-1")) {
			 aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group("org_name").count().as("count"),
					 sort(Sort.Direction.DESC, "count") 
				  );
		 } else {
			 criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)));
			 aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group("org_name","dept_name").count().as("count"),
					 sort(Sort.Direction.DESC, "count") 
				  );
		 } 
		
//		System.out.println(">>" +aggregation);
		AggregationResults groupResults = mongoTemplate.aggregate(
		    aggregation, collectName, EmpCountOutput.class);
		  
		  List<EmpCountOutput> aggList = groupResults.getMappedResults();
		  for (EmpCountOutput a : aggList) {
			  String name=org_id.equals("-1")?a.getIid() :a.getDept_name();
			  
			  Document n2=new Document("name", name);
			  n2.append("category", commondServiceImpl.byMap.get("new"));
			  n2.append("count", 0);	
			  result.add(n2);
			  
			  Document n3=new Document("name",name);
			  n3.append("category", commondServiceImpl.byMap.get("term"));
			  n3.append("count", 0);		
			  result.add(n3);
			  
			  Document n1=new Document("name", name);
			  n1.append("category", commondServiceImpl.byMap.get("now"));
			  n1.append("count", a.getCount());
			  result.add(n1);
		  }


		return result;
	}
	

}

