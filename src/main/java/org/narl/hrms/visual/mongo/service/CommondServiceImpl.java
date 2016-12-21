package org.narl.hrms.visual.mongo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;

@Service
public  class CommondServiceImpl  implements  CommondRepo {
	
	@Autowired
    private MongoOperations mongoOperation;

	public String[] ferial_names={"特別休假","加班或假日出差轉補休","生理假","傷病假","婚假","家庭照顧假","事假","產檢假","陪產假","產假","喪假","國內公假","國外公假","公傷病假","安胎假"};
	
	public static final Map<String, String> byMap = new HashMap<String, String>();
	    static {
	    	byMap.put("new", "NEW (到職人數)");
	    	byMap.put("term", "TERM (離職人數)");
	    	byMap.put("now", "CURRENT (在職人數)");	    	
	    }
	
	    
	/**drop collection if existed
	 * then create new One
	 */
	public DBCollection createCollection(String collectionName) {
		 DBCollection newCollection=null;
		 if(!mongoOperation.collectionExists(collectionName))		{
			 newCollection =mongoOperation.createCollection(collectionName);
		 }
		return  newCollection;
	}
	
	public void dropCollection(String collectionName) {
		 if(mongoOperation.collectionExists(collectionName))		{
			 mongoOperation.dropCollection(collectionName);
		 }
	}

	public boolean collectionExisted(String collectionName) {
		 if(!mongoOperation.collectionExists(collectionName))
			 return false;
		 else
			 return true;
	}
	
}
