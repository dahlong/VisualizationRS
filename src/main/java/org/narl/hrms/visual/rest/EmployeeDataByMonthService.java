package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

@Path("employeedatemonth")
public class EmployeeDataByMonthService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeDataByMonthService.class);
	
	private int months=12;
	    
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
	public List<Document> postFindEmpOrgTotal(@FormParam("year") String yearString, @FormParam("org_id") String org_id) {
		
		logger.info("postFindEmpOrgTotal>>  org_id: " +org_id +", yearString: "+yearString  );
		
		if (yearString==null )	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		List<Document> result=getDefaultDocuments();
		 
		for (int mon=1;mon<=this.months; mon++) {
			String monthString=String.format("%02d", Integer.valueOf(mon));
			 
			String regex=yearString;
			 if (!monthString.equals("")) {
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
					if (!whereCond.equals(""))
						criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)), Criteria.where(whereCond).regex(regex, "i"));
					else
						criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)));
				}
		
				 Aggregation aggregation=null;
				 if (org_id.equals("-1")) {
					 aggregation=newAggregation(
							 match(criteriaDefinition),   
							 group().count().as("count")
						  );	 
				 } else {
					 aggregation=newAggregation(
							 match(criteriaDefinition),   
							 group("org_name").count().as("count")
						  );	 
				 }

//				System.out.println(">>" +aggregation);
				AggregationResults groupResults = mongoTemplate.aggregate(
				    aggregation, collectName, EmpCountOutput.class);
				 
				  List<EmpCountOutput> aggList = groupResults.getMappedResults();
				  for (EmpCountOutput a : aggList) {
					  Document n1=new Document("name", monthString+"月");
					  n1.append("category", entry.getValue());
					  n1.append("count", 0);
					  n1.append("countNow", 0);
					  if (result.contains(n1)) {
						result.remove(n1) ;
					  }
					  n1.remove("count");
					  n1.remove("countNow");
					  if (entry.getKey().equals("now")) {
						  n1.append("count", 0);
						  n1.append("countNow", a.getCount());
					  }	  else {	  
						  n1.append("countNow", 0);
						  n1.append("count", a.getCount());
					  }
					  result.add(n1);	
				  }
			 }			 
		}
		

		reBuildDocuments(result);
		
		
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
			@FormParam("dept_id") final String dept_id) {
		
		logger.info("postFindEmpDeptTotal>>  org_id: " +org_id +", dept_id: " +dept_id +", yearString: "+yearString  );
		
		if (yearString==null || dept_id.equals("-1"))	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;		
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		
		List<Document> result=getDefaultDocuments();
		 
		for (int mon=1;mon<=this.months; mon++) {
			String monthString=String.format("%02d", Integer.valueOf(mon));
			 String regex=yearString;
			 if (!monthString.equals("")) {
				 regex=regex+"/" +monthString +"/";
			 }
			 
			 for (Map.Entry<String, String> entry : commondServiceImpl.byMap.entrySet()) {
				 String whereCond="start_date";		 
				 if (entry.getKey().equals("term"))
					 whereCond="end_date";
				 else if (entry.getKey().equals("now"))
					 whereCond="";
				 
				Criteria criteriaDefinition = new Criteria();
				if (whereCond.equals(""))
					criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
				else
					criteriaDefinition.andOperator( Criteria.where("org_id").is(Integer.valueOf(org_id)),Criteria.where("dept_id").is(Integer.valueOf(dept_id)),  Criteria.where(whereCond).regex(regex, "i"));
				
				Aggregation aggregation =newAggregation(
						 match(criteriaDefinition),   
						 group("org_name","dept_name").count().as("count")
					  );
				
//				 System.out.println(">>" +aggregation);
				AggregationResults groupResults = mongoTemplate.aggregate(
				    aggregation, collectName, EmpCountOutput.class);
				 
				  List<EmpCountOutput> aggList = groupResults.getMappedResults();
				  for (EmpCountOutput a : aggList) {
					  Document n1=new Document("name",monthString+"月");
					  n1.append("category", entry.getValue());
					  n1.append("count", 0);
					  n1.append("countNow", 0);
					  if (result.contains(n1)) {
						result.remove(n1) ;
					  }
					  n1.remove("count");
					  n1.remove("countNow");
					  if (entry.getKey().equals("now")) {
						  n1.append("countNow", a.getCount());
						  n1.append("count", 0);
					  }	  else {	  
						  n1.append("countNow", 0);
						  n1.append("count", a.getCount());
					  }
					  
					  result.add(n1);	
					  
				  }
			 }
		}
		reBuildDocuments(result);
		return result;
	}
	
	/**
	 * make up basic array of documents
	 * @return
	 */
	private List<Document> getDefaultDocuments() {
				final List<Document> result=new ArrayList<Document>();
				for (int mon=1;mon<=this.months; mon++) {
					String monthString=String.format("%02d", Integer.valueOf(mon));					
					 for (Map.Entry<String, String> entry : commondServiceImpl.byMap.entrySet()) {						
							 Document n1=new Document("name",monthString+"月");
							  n1.append("category", entry.getValue());
							  n1.append("count",0);
							  n1.append("countNow",0);
							  result.add(n1);
					 }					 
				}				
				return result;
			}


	/**
	 * 重整，目前在職人數=目前在職人數CURRENT-新增雇用NEW+結束雇用TERM
	 * @param result
	 */
	private void reBuildDocuments(List<Document> result) {
		 Map<String, Integer> nCount = new HashMap<String, Integer>();
		for (Document d : result) {
			 for (int k=0; k<this.months; k++) {
				 String monthString=String.format("%02d", Integer.valueOf(k+1));
				 if (d.get("category").toString().startsWith("C")) {
					 if (d.get("name").toString().startsWith(monthString+"月")) {				
						nCount.put(monthString+"月",  Integer.valueOf(d.get("countNow").toString()));						
					 }
				 }
			 }
		}
		System.out.println(">>" +nCount);
		for (Document d : result) {
			 for (int k=0; k<this.months; k++) {
				 String monthString=String.format("%02d", Integer.valueOf(k+1));
				 if (d.get("category").toString().startsWith("N")) {
					 if (d.get("name").toString().startsWith(monthString+"月")) {				
						int count = nCount.get(monthString+"月").intValue()- Integer.valueOf(d.get("count").toString());
						nCount.remove(monthString+"月");
						nCount.put(monthString+"月",  Integer.valueOf(count));						
					 }
				 }
				 
				 if (d.get("category").toString().startsWith("T")) {
					 if (d.get("name").toString().startsWith(monthString+"月")) {				
						int count = nCount.get(monthString+"月").intValue()+ Integer.valueOf(d.get("count").toString());
						nCount.remove(monthString+"月");
						nCount.put(monthString+"月",  Integer.valueOf(count));						
					 }
				 }
			 }
		}
		
//		System.out.println(">>" +nCount);
		for (Document d : result) {
			  if (d.get("category").toString().startsWith("C")) {
				  for (int k=0; k<this.months; k++) {
					  String monthString=String.format("%02d", Integer.valueOf(k+1));
					  if (d.get("name").toString().startsWith(monthString+"月")) {				
							  d.remove("countNow");
							  d.append("countNow", nCount.get(monthString+"月").intValue());
						}
				  }
			  }
		}
//		System.out.println(">>" +nCount);
	}

}

