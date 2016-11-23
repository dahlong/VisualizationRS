package org.narl.hrms.visual.mongo;

import org.narl.hrms.visual.mongo.service.CommondRepo;
import org.narl.hrms.visual.mongo.service.CommondServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories
public class SpringMongoConfigForUnitTest {
	
	@Bean
	public MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient("192.168.30.129",27017), "hrvisual");
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}

	
	
	@Bean
	public CommondRepo getCommondService() {
		return new CommondServiceImpl();
    }
	
	
//	@Bean
//	public LeaveTypesRepo getLeaveTypesService() {
//		return new LeaveTypesServiceImpl();
//    }
}
