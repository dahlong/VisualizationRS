package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

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
import org.narl.hrms.visual.rest.output.LeaveTypesOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

@Path("leavetypesby")
public class LeaveTypesByService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveTypesByService.class);
	
	private static final Map<Integer, String[]> weeksMap = new HashMap<Integer, String[]>();
    static {
    	weeksMap.put(1, new String[]{"Mon", "星期一"});
    	weeksMap.put(2, new String[]{"Tue", "星期二"});
    	weeksMap.put(3, new String[]{"Wed", "星期三"});
    	weeksMap.put(4, new String[]{"Thu", "星期四"});
    	weeksMap.put(5, new String[]{"Fri", "星期五"});
    	weeksMap.put(6, new String[]{"Sat", "星期六"});
    	weeksMap.put(7, new String[]{"Sun", "星期日"});
    }
    
    private static final Map<String, int[]> byMap = new HashMap<String, int[]>();
    static {
    	byMap.put("month", new int[]{1,12});
    	byMap.put("week", new int[]{1,7});
    	byMap.put("day", new int[]{1,31});
    }
    
	@Autowired 
	MongoTemplate mongoTemplate;	
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	String collectionName="LEAVE_TYPE_";
		
	
	/**
	 *  角色:1,2
	 * 只選組織，部門與人員為全選
	 * by: month、week、day
	 */
	@POST
	@Path("postFindLeaveOrgTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveOrgTotal(@FormParam("org_id") String org_id,	@FormParam("year") String yearString,
			@FormParam("ferial_name") String ferial_name, @FormParam("by") String by) {
		
		logger.info("postFindLeaveOrgTotal>>  org_id: " +org_id +",yearString: "+yearString +",ferial_name: "+ferial_name  +",by: "+by);
		
		//角色:1, 可能中心為全選
		if (yearString==null )	
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();

		int[] byLoopInit =byMap.get(by);
		Map<Integer, String> whereCondArray = new HashMap<Integer, String>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++) {
			 String sFerialName= "_sum("+ferial_name+")";
			 
			 if (!by.equals("") && by.equals("month"))
				 sFerialName=String.valueOf(i) +sFerialName;
			 
			 if (!by.equals("") && by.equals("week"))
				 sFerialName=weeksMap.get(i)[0]  +sFerialName;
				 
			 
			 if (!by.equals("") && by.equals("day"))
				 sFerialName="Day_" +String.valueOf(i)   +sFerialName;
			 
			 
			 whereCondArray.put(i, sFerialName);
		 }
		  
		  
		  
		 String groupName="org_name"; 
		 List<Aggregation> aggregationList=new ArrayList<Aggregation>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++)  {
			 String whereCond= whereCondArray.get(i);
			 Criteria criteriaDefinition = new Criteria();
			 if (!org_id.equals("-1")) {
				 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("org_id").is(Integer.valueOf( org_id)));			 
			 } else {
				 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0));			 
			 }
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group(groupName).sum(whereCond).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 aggregationList.add(aggregation);
		 }
		 
		 
		 
//		 List<Aggregation> aggregationList=new ArrayList<Aggregation>();
//		 if (!by.equals("") && by.equals("month")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=12;i++) {
//				 whereCond=String.valueOf(i) + "_sum("+ferial_name+")";
//				
//				 Criteria criteriaDefinition = new Criteria();
//				 if (!org_id.equals("-1")) {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("org_id").is(Integer.valueOf( org_id)));			 
//				 } else {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0));			 
//				 }
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }		
//		 }
//		 
//		 if (!by.equals("") && by.equals("week")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=7;i++) {			
//				 whereCond=weeksMap.get(i)[0] + "_sum("+ferial_name+")";
//				
//				 Criteria criteriaDefinition = new Criteria();
//				 if (!org_id.equals("-1")) {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("org_id").is(Integer.valueOf( org_id)));			 
//				 } else {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0));			 
//				 }
//				
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }		
//		 }
//		 
//		 if (!by.equals("") && by.equals("day")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=31;i++) {				 
//				 whereCond="Day_" +String.valueOf(i) + "_sum("+ferial_name+")";
//				
//				 Criteria criteriaDefinition = new Criteria();
//				 if (!org_id.equals("-1")) {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("org_id").is(Integer.valueOf( org_id)));			 
//				 } else {
//					 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0));			 
//				 }
//				
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }		
//		 }
		 List<Document> result= getDocuments(by, collectName, aggregationList);
		return result;
	}


	/**
	 * 角色:1,2,3
	 * 只選組織與部門，人員為全選
	 * org_id<>-1 & dept_id<>-1
	 * 	 by: month、week、day
	 */
	@POST
	@Path("postFindLeaveDeptTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("org_id") String org_id,	@FormParam("year") String yearString,
			@FormParam("dept_id") final String dept_id, @FormParam("ferial_name") String ferial_name, @FormParam("by") String by) {
		
		logger.info("postFindLeaveDeptTotal>>  org_id: " +org_id +",yearString: "+yearString +",ferial_name: "+ferial_name  +",by: "+by);
		
		if (yearString==null || org_id.equals("-1") || dept_id.equals("-1"))
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		
		int[] byLoopInit =byMap.get(by);
		Map<Integer, String> whereCondArray = new HashMap<Integer, String>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++) {
			 String sFerialName= "_sum("+ferial_name+")";
			 
			 if (!by.equals("") && by.equals("month"))
				 sFerialName=String.valueOf(i) +sFerialName;
			 
			 if (!by.equals("") && by.equals("week"))
				 sFerialName=weeksMap.get(i)[0]  +sFerialName;
				 
			 
			 if (!by.equals("") && by.equals("day"))
				 sFerialName="Day_" +String.valueOf(i)   +sFerialName;
			 
			 
			 whereCondArray.put(i, sFerialName);
		 }
		
		 String groupName="dept_name";
		 List<Aggregation> aggregationList=new ArrayList<Aggregation>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++) {
			 String whereCond= whereCondArray.get(i);
			 
			 Criteria criteriaDefinition = new Criteria();	
			  criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group(groupName).sum(whereCond).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 aggregationList.add(aggregation);
		 }
		 
		 
//		 String whereCond="";		 
//		 if (!by.equals("") && by.equals("month")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=12;i++) {
//				 whereCond=String.valueOf(i) + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();	
//				  criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }
//		 }
//		 
//		 if (!by.equals("") && by.equals("week")) {
//			 aggregationList.clear();
//			    for (int i=1;i<=7;i++) {				 
//				 whereCond=weeksMap.get(i)[0]  + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();	
//				  criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }
//		 }
//		 
//		 if (!by.equals("") && by.equals("day")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=31;i++) {				 
//				 whereCond="Day_" +String.valueOf(i)  + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();	
//				  criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0), Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }
//		 }

		 List<Document> result= getDocuments(by, collectName, aggregationList);
		return result;
	}
	
	/**
	 * 角色:1,2,3,4
	 * 選組織，部門與人員
	 * 	by: month、week、day
	 */
	@POST
	@Path("postFindLeaveEmpTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveEmpTotal(@FormParam("year") String yearString, 
			@FormParam("emp_id") final String emp_id, @FormParam("ferial_name") String ferial_name, @FormParam("by") String by) {
		
		logger.info("postFindLeaveDeptTotal>>  emp_id: " +emp_id +",yearString: "+yearString +",ferial_name: "+ferial_name  +",by: "+by);
		
		if (yearString==null ||  emp_id.equals("-1"))
			return new ArrayList<Document>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();
		
		int[] byLoopInit =byMap.get(by);
		Map<Integer, String> whereCondArray = new HashMap<Integer, String>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++) {
			 String sFerialName= "_sum("+ferial_name+")";
			 
			 if (!by.equals("") && by.equals("month"))
				 sFerialName=String.valueOf(i) +sFerialName;
			 
			 if (!by.equals("") && by.equals("week"))
				 sFerialName=weeksMap.get(i)[0]  +sFerialName;
				 
			 
			 if (!by.equals("") && by.equals("day"))
				 sFerialName="Day_" +String.valueOf(i)   +sFerialName;
			 
			 
			 whereCondArray.put(i, sFerialName);
		 }
		 
		 String groupName="emp_name";
		 List<Aggregation> aggregationList=new ArrayList<Aggregation>();
		 for (int i=byLoopInit[0];i<=byLoopInit[1];i++) {
			 String  whereCond= whereCondArray.get(i);
			 
			 Criteria criteriaDefinition = new Criteria();
			 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(whereCond).gte(0),Criteria.where("emp_id").is(Integer.valueOf( emp_id)));
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group(groupName).sum(whereCond).as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 aggregationList.add(aggregation);
		 } 
		 
		 
//		 String whereCond="";		 
//		 if (!by.equals("") && by.equals("month")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=12;i++) {
//				  whereCond=String.valueOf(i) + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();
//				 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(whereCond).gte(0),Criteria.where("emp_id").is(Integer.valueOf( emp_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }
//		 }
//		 
//		 if (!by.equals("") && by.equals("week")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=7;i++) {				 
//				  whereCond=weeksMap.get(i)[0] + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();
//				 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(whereCond).gte(0),Criteria.where("emp_id").is(Integer.valueOf( emp_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation); 
//			 }
//		 }
//		 
//		 if (!by.equals("") && by.equals("day")) {
//			 aggregationList.clear();
//			 for (int i=1;i<=31;i++) {				 
//				  whereCond="Day_" +String.valueOf(i)  + "_sum("+ferial_name+")";
//				 
//				 Criteria criteriaDefinition = new Criteria();
//				 criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(whereCond).gte(0),Criteria.where("emp_id").is(Integer.valueOf( emp_id)));
//				 
//				 Aggregation aggregation = newAggregation(
//						 match(criteriaDefinition),   
//						 group(groupName).sum(whereCond).as("total"),
//						 sort(Sort.Direction.DESC, "total") 
//					  );
//				 aggregationList.add(aggregation);
//			 }
//		 }
		 
		 List<Document> result= getDocuments(by, collectName, aggregationList);
		return result;
	}


	private  List<Document> getDocuments(String by, String collectName,  List<Aggregation> aggregationList) {
		List<Document> result=new ArrayList<Document>();
		int count=1;
		
		 for (Aggregation aggregation : aggregationList) {		
			 AggregationResults groupResults = mongoTemplate.aggregate( aggregation, collectName, LeaveTypesOutput.class);					  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("id", a.getIid());
				  n1.append("order", count);
				  
				  if (by.equals("month"))					  
					  n1.append("name",String.valueOf(count)+"月");
				  
				  if (by.equals("week"))	{		
					  if (weeksMap.get(count)==null)
						  n1.append("name","未知");
					  else
						  n1.append("name",weeksMap.get(count)[1]);
				  }
				  
				  if (by.equals("day"))					  
					  n1.append("name", "Day_"+String.valueOf(count) );
				  
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);
			  }
			  count++;
		 }
		 
		 return result;
	}
	
}


