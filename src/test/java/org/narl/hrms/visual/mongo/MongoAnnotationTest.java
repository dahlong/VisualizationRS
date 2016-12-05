package org.narl.hrms.visual.mongo;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.rest.output.LeaveTypesOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringMongoConfigForUnitTest.class)
public class MongoAnnotationTest {
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	String strYear="2015";
	
	
	@Before
	public void init() {
		
	}
	@Test
	public void testConnection(){
		String collectionName="LEAVE_TYPE_"+strYear;
		DBCollection collection = mongoTemplate.getCollection(collectionName);
		System.out.println(">>Count=" +collection.count());
		
	}
	
	
	@Test
	public void testWithAggregation(){
		String collectionName="LEAVE_TYPE_"+strYear;
		DBCollection collection = mongoTemplate.getCollection(collectionName);		
		{
			List<Document> result=new ArrayList<Document>();
			 Criteria criteriaDefinition = new Criteria();
			  criteriaDefinition.andOperator(Criteria.where("Total.特別休假").gte(0), Criteria.where("org_id").is("82"));
			 
			 Aggregation aggregation = newAggregation(
					 match(criteriaDefinition),   
					 group("org_name").sum("Total.特別休假").as("total"),
					 sort(Sort.Direction.DESC, "total") 
				  );
			 
			 AggregationResults groupResults = mongoTemplate.aggregate(
					    aggregation, collectionName, LeaveTypesOutput.class);
					  
			  List<LeaveTypesOutput> aggList = groupResults.getMappedResults();
			  for (LeaveTypesOutput a : aggList) {
				  Document n1=new Document("id", a.getIid());
				  n1.append("ferial_name", "特別休假");
				  n1.append("leave_hours", a.getTotal());
				  result.add(n1);	
			  }
			  
			  for (Document document : result) {
				  System.out.println(">>" +document);
			  }
			  
		}
	}
	
	@Test
	public void testWithDBObject(){
		String collectionName="LEAVE_TYPE_"+strYear;
		DBCollection collection = mongoTemplate.getCollection(collectionName);
		{

			DBObject matchFields = new BasicDBObject();
			List<DBObject> obj = new ArrayList<DBObject>();
	    	obj.add(new BasicDBObject("org_id", "82"));
	    	obj.add(new BasicDBObject("Total.特別休假",new BasicDBObject("$gte", 0 )));
	    	matchFields.put("$and", obj); 
			
			DBObject groupFields = new BasicDBObject();
		    groupFields.put("_id", "$org_name");
		    groupFields.put("total", new BasicDBObject("$sum", "$Total.特別休假"));
		    
		    DBObject match = new BasicDBObject("$match", matchFields);
		    DBObject group = new BasicDBObject("$group", groupFields);
		    DBObject sort = new BasicDBObject("$sort", new BasicDBObject("total", -1));
		    
		    List<DBObject> pipeline = Arrays.asList(match, group, sort);
		    System.out.println(">>" +pipeline);
		    AggregationOutput output = collection.aggregate(pipeline);
		    System.out.println(">>" +output);
		    
		    List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		    for (DBObject dbObject : output.results()) {
		        Map<String, String> result = new HashMap<String, String>();
		        result.put(dbObject.get("_id").toString(), dbObject.get("total").toString());
		        results.add(result);
		    }
		    
		    for (Map<String, String> m: results)
		    	System.out.println(">>" +m);
		    
		}
		
	}
	
}
