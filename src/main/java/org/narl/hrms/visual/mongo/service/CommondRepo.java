package org.narl.hrms.visual.mongo.service;

import com.mongodb.DBCollection;

public interface CommondRepo  {

	public boolean collectionExisted(String collectionName);
	
	public DBCollection createCollection(String collectionName);
	
	public void dropCollection(String collectionName) ;
}
