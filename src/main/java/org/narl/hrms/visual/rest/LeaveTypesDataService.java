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
import org.narl.hrms.visual.rest.output.LeaveTypesOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@Path("leavetypesdata")
public class LeaveTypesDataService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveTypesDataService.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	CommondServiceImpl commondServiceImpl;

	String collectionName = "LEAVE_TYPE_";

	/**
	 * 角色:1,2 只選組織，部門與人員為全選
	 */
	@POST
	@Path("postFindLeaveOrgTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveOrgTotal(@FormParam("org_id") String org_id,
			@FormParam("year") String yearString, @FormParam("month") String monthString) {

		logger.info("postFindLeaveOrgTotal>>  org_id: " + org_id + ",yearString: " + yearString);

		// 角色:1, 可能中心為全選
		if (yearString == null)
			return new ArrayList<Document>();

		String collectName = this.collectionName + yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();

		final List<Document> result = new ArrayList<Document>();

		String[] fs = commondServiceImpl.ferial_names;
		for (int i = 0; i < fs.length; i++) {
			String ferial_name = fs[i];

			Criteria criteriaDefinition = new Criteria();

			String whereCond = "" + ferial_name + "";
			if (!monthString.equals("")) {
				whereCond = monthString + "_sum(" + whereCond + ")";
			}

			if (!org_id.equals("-1")) {
				criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0),
						Criteria.where("org_id").is(Integer.valueOf(org_id)));
			} else {
				criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false),
						Criteria.where(whereCond).gte(0));
			}

			Aggregation aggregation = newAggregation(match(criteriaDefinition),
					group("org_name").sum(whereCond).as("total"), sort(Sort.Direction.DESC, "total"));

			AggregationResults<LeaveTypesOutput> groupResults = mongoTemplate.aggregate(aggregation, collectName,
					LeaveTypesOutput.class);

			List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			for (LeaveTypesOutput a : aggList) {
				Document n1 = new Document("id", a.getIid());
				n1.append("ferial_name", ferial_name);
				n1.append("leave_hours", a.getTotal());
				result.add(n1);
			}
		}
		return result;
	}

	/**
	 * 角色:1,2,3 只選組織與部門，人員為全選 org_id<>-1 & dept_id<>-1
	 */
	@POST
	@Path("postFindLeaveDeptTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveDeptTotal(@FormParam("org_id") String org_id,
			@FormParam("year") String yearString, @FormParam("dept_id") final String dept_id,
			@FormParam("month") String monthString) {

		logger.info("postFindLeaveDeptTotal>>  org_id: " + org_id + ", dept_id: +" + dept_id + ", yearString: "
				+ yearString);

		if (yearString == null || org_id.equals("-1") || dept_id.equals("-1"))
			return new ArrayList<Document>();

		String collectName = this.collectionName + yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();

		final List<Document> result = new ArrayList<Document>();
		String[] fs = commondServiceImpl.ferial_names;
		for (int i = 0; i < fs.length; i++) {
			String ferial_name = fs[i];

			Criteria criteriaDefinition = new Criteria();
			String groupName = "dept_name";

			String whereCond = "" + ferial_name + "";
			if (!monthString.equals("")) {
				whereCond = monthString + "_sum(" + whereCond + ")";
			}
			criteriaDefinition.andOperator(Criteria.where("emp_id").exists(false), Criteria.where(whereCond).gte(0),
					Criteria.where("dept_id").is(Integer.valueOf(dept_id)));

			Aggregation aggregation = newAggregation(match(criteriaDefinition),
					group(groupName).sum(whereCond).as("total"), sort(Sort.Direction.DESC, "total"));

			AggregationResults<LeaveTypesOutput> groupResults = mongoTemplate.aggregate(aggregation, collectName,
					LeaveTypesOutput.class);

			List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			for (LeaveTypesOutput a : aggList) {
				Document n1 = new Document("id", a.getIid());
				n1.append("ferial_name", ferial_name);
				n1.append("leave_hours", a.getTotal());
				result.add(n1);
			}
		}
		return result;
	}

	/**
	 * 角色:1,2,3,4 選組織，部門與人員
	 */
	@POST
	@Path("postFindLeaveEmpTotal")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveEmpTotal(@FormParam("year") String yearString,
			@FormParam("emp_id") final String emp_id, @FormParam("month") String monthString) {

		logger.info("postFindLeaveDeptTotal>>  emp_id: " + emp_id + ", yearString: " + yearString);

		if (yearString == null || emp_id.equals("-1"))
			return new ArrayList<Document>();

		String collectName = this.collectionName + yearString;
		if (!commondServiceImpl.collectionExisted(collectName))
			return new ArrayList<Document>();

		final List<Document> result = new ArrayList<Document>();
		String[] fs = commondServiceImpl.ferial_names;
		for (int i = 0; i < fs.length; i++) {
			String ferial_name = fs[i];

			Criteria criteriaDefinition = new Criteria();
			String groupName = "emp_name";

			String whereCond = "" + ferial_name + "";
			if (!monthString.equals("")) {
				whereCond = monthString + "_sum(" + whereCond + ")";
			}

			criteriaDefinition.andOperator(Criteria.where("emp_id").exists(true), Criteria.where(whereCond).gte(0),
					Criteria.where("emp_id").is(Integer.valueOf(emp_id)));

			Aggregation aggregation = newAggregation(match(criteriaDefinition),
					group(groupName).sum(whereCond).as("total"), sort(Sort.Direction.DESC, "total"));

			AggregationResults<LeaveTypesOutput> groupResults = mongoTemplate.aggregate(aggregation, collectName,
					LeaveTypesOutput.class);

			List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			for (LeaveTypesOutput a : aggList) {
				Document n1 = new Document("id", a.getIid());
				n1.append("ferial_name", whereCond);
				n1.append("leave_hours", a.getTotal());
				result.add(n1);
			}
		}
		return result;
	}

	/**
	 * 角色:1,2,3,4 選組織，部門與人員 取得人員各類假別時數與平均時數
	 */
	@POST
	@Path("postFindLeaveEmpTotalAvg")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<Document> postFindLeaveEmpTotalAvg(@FormParam("year") String yearString,
			@FormParam("emp_id") final String emp_id, @FormParam("month") String monthString,
			@FormParam("org_id") String org_id, @FormParam("dept_id") String dept_id) {

		logger.info("postFindLeaveDeptTotalAvg>>  emp_id: " + emp_id + ", yearString: " + yearString,
				"dept_id" + dept_id, "org_id" + org_id);

		List<Document> result = new ArrayList<Document>();
		result = this.postFindLeaveEmpTotal(yearString, emp_id, monthString);

		// Add a new field to specify as the personal hours.
		String id = null;
		for (Document d : result) {
			d.append("category", "員工個人");
			if (null == id)
				id = (String) d.get("id");
		}

		// Add dept_avg info into result list.
		String collectName = this.collectionName + yearString;
		String[] fs = commondServiceImpl.ferial_names;

		logger.info("員工休假別統計筆數:" + result.size());

		List<Document> deptAvgResult = null;
		List<Document> orgIdAvgResult = null;
		for (int i = 0; i < fs.length; i++) {
			String ferial_name = fs[i];
			String whereCond = "" + ferial_name + "";

			// Get dept's average values .
			String groupByDept = "dept_name";
			deptAvgResult = getDeptAvgHoursByFerialName(dept_id, org_id, monthString, id, collectName, groupByDept,
					ferial_name, whereCond);
			result.addAll(deptAvgResult);

			// Get org's average values .
			orgIdAvgResult = getOrgIdAvgHoursByFerialName(org_id, monthString, id, collectName, "org_id", ferial_name,
					whereCond);
			result.addAll(orgIdAvgResult);

		}
		logger.info("增加部門統計後筆數" + deptAvgResult.size());

		return result;
	}

	/**
	 * 取得中心、各組，各類假別的平均時數
	 */
	public List<Document> getDeptAvgHoursByFerialName(String dept_id, String org_id, String monthString, String id,
			String collectName, String groupName, String ferial_name, String whereCond) {

		List<Document> deptAvgResult = new ArrayList<Document>();

		if (!monthString.equals("")) {
			whereCond = monthString + "_sum(" + whereCond + ")";
		}

		Criteria deptAvgCriteria = new Criteria();
		
		logger.info("部門_id : =====> "+ dept_id);
		if( "".equals(dept_id) || null == dept_id ){
			deptAvgCriteria.andOperator(Criteria.where("org_id").is(Integer.valueOf(org_id)),
					Criteria.where(whereCond).gte(0),Criteria.where("emp_id").exists(true));
		}else{
			deptAvgCriteria.andOperator(Criteria.where("dept_id").is(Integer.valueOf(dept_id)),Criteria.where("org_id").is(Integer.valueOf(org_id)),
					Criteria.where(whereCond).gte(0),Criteria.where("emp_id").exists(true));
		}
		
		Aggregation deptAvgAggregation = newAggregation(match(deptAvgCriteria),
				group(groupName).avg(whereCond).as("average"), sort(Sort.Direction.DESC, "average"));

		AggregationResults<LeaveTypesOutput> deptAvg = mongoTemplate.aggregate(deptAvgAggregation, collectName,
				LeaveTypesOutput.class);

		List<LeaveTypesOutput> deptAvgList = deptAvg.getMappedResults();
		for (LeaveTypesOutput a : deptAvgList) {
			Document n1 = new Document("id", a.getIid());
			n1.append("ferial_name", whereCond);
			n1.append("leave_hours", a.getAverage());
			n1.append("category", "部門平均");
			deptAvgResult.add(n1);
		}

		return deptAvgResult;
	}

	/**
	 * 取得各中心各類假別的平均時數
	 */
	public List<Document> getOrgIdAvgHoursByFerialName(String org_id, String monthString, String id, String collectName,
			String groupName, String ferial_name, String whereCond) {

		List<Document> orgAvgResult = new ArrayList<Document>();

		if (!monthString.equals("")) {
			whereCond = monthString + "_sum(" + whereCond + ")";
		}

		Criteria orgAvgCriteria = new Criteria();

		if ("".equals(org_id) || null == org_id) {
			orgAvgCriteria.andOperator(Criteria.where(whereCond).gte(0), Criteria.where("emp_id").exists(true));
		} else {
			orgAvgCriteria.andOperator(Criteria.where(whereCond).gte(0), Criteria.where("emp_id").exists(true),
					Criteria.where("org_id").is(Integer.valueOf(org_id)));
		}

		Aggregation orgAvgAggregation = newAggregation(match(orgAvgCriteria),
				group(groupName).avg(whereCond).as("average"), sort(Sort.Direction.DESC, "average"));

		AggregationResults<LeaveTypesOutput> orgAvg = mongoTemplate.aggregate(orgAvgAggregation, collectName,
				LeaveTypesOutput.class);

		List<LeaveTypesOutput> orgtAvgList = orgAvg.getMappedResults();
		for (LeaveTypesOutput a : orgtAvgList) {
			Document n1 = new Document("id", a.getIid());
			n1.append("ferial_name", whereCond);
			n1.append("leave_hours", a.getAverage());
			n1.append("category", "中心平均");
			orgAvgResult.add(n1);
		}

		return orgAvgResult;
	}

}
