package org.narl.hrms.visual.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.narl.hrms.visual.rest.output.DeptOutput;
import org.narl.hrms.visual.rest.output.EmpOutput;
import org.narl.hrms.visual.rest.output.OrgOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Path("commondata")
public class CommonDataService {

	private static final Logger logger = LoggerFactory.getLogger(CommonDataService.class);
	
	@Autowired 
	MongoTemplate mongoTemplate;
	
	@Autowired
	CommondServiceImpl commondServiceImpl;
	
	String collectionName="ORG_DEPT_EMP_";
	
	/**
	 * 取得organization according to login user's role
	 * * 1代表super user,2代表人事,3代表主管,4代表員工
	 * ,2代表人事,3代表主管,4代表員工>>都是只有自己的中心
	 * db.getCollection('ORG_DEPT_EMP_2015').aggregate([
					{ $match : { "$and" : [ {'emp_id': '957' }]}},
					{  "$group": { "_id": { org_id: "$org_id", org_name: "$org_name" }}},
					{   "$sort" : { "org_id" : -1}}
					])
	 */
	@POST
	@Path("postOrgList")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<OrgOutput> getOrgList(@FormParam("login_id") String login_id,	@FormParam("year") String yearString,	@FormParam("role") String role){
	
		logger.info("postDeptList>> login id : " +",yearString: "+yearString +", role:" +role);
		
		if (yearString==null || login_id==null || login_id.equals(""))
			return new ArrayList<OrgOutput>();
		
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<OrgOutput>();
		
		
		Criteria criteriaDefinition = new Criteria();
		if (role!=null && "1".equals(role)) {
			
		} else {
			criteriaDefinition.andOperator(Criteria.where("emp_id").is(Integer.valueOf(login_id)));
		}
		
		 Aggregation aggregation=newAggregation(
				 match(criteriaDefinition),   
				 group("org_id", "org_name"),
				 sort(Sort.Direction.ASC, "org_id") 
			  );

		 AggregationResults groupResults = mongoTemplate.aggregate(
				    aggregation, collectName, OrgOutput.class);
		 
		  List<OrgOutput> aggList = groupResults.getMappedResults();	
			return aggList;
	}
	
	/**
	 * 取得Department according to organization 
	 *  * * 1代表super user,2代表人事,3代表主管,4代表員工
	 *  3代表主管,4代表員工>>自己的部門
	 *  db.getCollection('ORG_DEPT_EMP_2015').aggregate([
		{ $match : { "$and" : [ {'emp_id': '957' }]}},
		{ "$group": { "_id": { dept_id: "$dept_id", dept_name: "$dept_name" }}},
		{ "$sort" : { "dept_id" : -1}}])
	 *  1代表主管,2代表員工>>自己中心的所有部門 
	 */
	@POST
	@Path("postDeptList")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<DeptOutput> getDeptList(@FormParam("org_id") String org_id, @FormParam("login_id") String login_id,	
			@FormParam("year") String yearString, @FormParam("role") String role){
		logger.info("postDeptList>> login id : " +login_id+ ", org_id: " +org_id +",yearString: "+yearString +", role:" +role);
		
		if (yearString==null || org_id.equals("-1") || login_id==null || login_id.equals(""))
			return new ArrayList<DeptOutput>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<DeptOutput>();
		
		Criteria criteriaDefinition = new Criteria();
		if (role!=null && "1".equals(role)) {
				criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)));
				
		} else if (role!=null && "2".equals(role)) {
			criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)));
		} else {
			criteriaDefinition.andOperator(Criteria.where("emp_id").is(Integer.valueOf(login_id)));
		}
		
		 Aggregation aggregation=newAggregation(
				 match(criteriaDefinition),   
				 group("dept_id", "dept_name"),
				 sort(Sort.Direction.ASC, "dept_id") 
			  );

		 AggregationResults groupResults = mongoTemplate.aggregate(
				    aggregation, collectName, DeptOutput.class);
		 
		  List<DeptOutput> aggList = groupResults.getMappedResults();	
			return aggList;
	}
	
	/**
	 * 取得Emp List accordintgto organization , department and  login user's role
	 *  1代表super user,2代表人事,3代表主管,4代表員工
	 *  1代表super user,2代表人事,3代表主管>>ALL
	 *  db.getCollection('ORG_DEPT_EMP_2015').aggregate([
		{ $match : { "$and" : [ {'org_id': '82' }, {'dept_id': '614' }]}},
		{ "$group": { "_id": { emp_id: "$emp_id", emp_name: "$emp_name" }}},
		{ "$sort" : { "emp_id" : -1}}])
	 * 4代表員工>>自己
	 * db.getCollection('ORG_DEPT_EMP_2015').aggregate([
		{ $match : { "$and" : [ {'emp_id': '957' }]}},
		{ "$group": { "_id": { emp_id: "$emp_id", emp_name: "$emp_name" }}},
		{ "$sort" : { "emp_id" : -1}}])
	 */
	@POST
	@Path("postEmpList")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<EmpOutput> postEmpList(@FormParam("org_id") String org_id, @FormParam("dept_id") String dept_id,
			 @FormParam("login_id") String login_id, @FormParam("year") String yearString, @FormParam("role") String role){

		logger.info("postEmpList>> login id : " +login_id+ ", org_id: " +org_id +", dept_id:" +dept_id +",yearString: "+yearString +", role:" +role);
		
		//if (yearString==null || org_id.equals("-1") || dept_id.equals("-1") || login_id==null || login_id.equals(""))
		if (yearString==null || org_id.equals("-1") || login_id==null || login_id.equals(""))
			return new ArrayList<EmpOutput>();
		
		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<EmpOutput>();
		
		Criteria criteriaDefinition = new Criteria();
		if (role!=null && "4".equals(role)) {
			criteriaDefinition.andOperator(Criteria.where("emp_id").is(Integer.valueOf(login_id)));
		}  else {
			if (dept_id.equals("-1"))
				criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)));
			else
				criteriaDefinition.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)), Criteria.where("dept_id").is(Integer.valueOf(dept_id)));
		}
		
		 Aggregation aggregation=newAggregation(
				 match(criteriaDefinition),   
				 group("emp_id", "emp_name", "emp_number"),
				 sort(Sort.Direction.ASC, "emp_id") 
			  );

		 AggregationResults groupResults = mongoTemplate.aggregate(
				    aggregation, collectName, EmpOutput.class);
		 
		  List<EmpOutput> aggList = groupResults.getMappedResults();	
			return aggList;
	}
	
	@POST
	@Path("postFindFerialName")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> postFindFerialName() {
		List<String> result = Arrays.asList(commondServiceImpl.ferial_names);
		return result;
	}


	
	/**
	 * get last 3 years list
	 * @return
	 */
	@POST
	@Path("postYearList")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<String> getYearList(){
	    List<String> resultList=new ArrayList<String>();
	    Calendar cal=Calendar.getInstance();
	    for (int i=0; i<3;i++) {
	    	int y=cal.get(Calendar.YEAR)-i;
	    	resultList.add(String.valueOf(y));
	    }
	    return resultList;
	}
	
	/**
	 * 取得login user's employee information including role
	 */
	@POST
	@Path("postLoginEmp")
	@Produces({ MediaType.APPLICATION_JSON })
	public EmpOutput postLoginEmp(@FormParam("login_id") String login_id, @FormParam("year") String yearString ){
		
		return  getEmpOutput(login_id, yearString);
	}

	public EmpOutput getEmpOutput(String login_id, String yearString) {
		logger.info("Login>> login id : " +login_id+ ", yearString: "+yearString);

		String collectName=this.collectionName+yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return null;
		
		Criteria criteriaDefinition = new Criteria();
		criteriaDefinition.andOperator(Criteria.where("emp_id").is(Integer.valueOf(login_id)));
	  
		Query query = new Query();
		query.addCriteria(criteriaDefinition);
		List<EmpOutput> result = mongoTemplate.find(query, EmpOutput.class, collectName);
		if (result.size()>0) {
			EmpOutput emp=result.get(0);
			return emp;
		}
		
		return null;
	}
	
}
