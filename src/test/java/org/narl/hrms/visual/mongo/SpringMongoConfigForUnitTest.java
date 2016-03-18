package org.narl.hrms.visual.mongo;

import org.narl.hrms.visual.mongo.service.BusinessTripRepo;
import org.narl.hrms.visual.mongo.service.BusinessTripServiceImpl;
import org.narl.hrms.visual.mongo.service.OvertimeRepo;
import org.narl.hrms.visual.mongo.service.OvertimeServiceImpl;
import org.narl.hrms.visual.mongo.service.PresentTimeRepo;
import org.narl.hrms.visual.mongo.service.PresentTimeServiceImpl;
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
		return new SimpleMongoDbFactory(new MongoClient(), "hrvisual");
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}

	@Bean
	public OvertimeRepo getOvertimeService() {
		return new OvertimeServiceImpl();
    }
	
	@Bean
	public BusinessTripRepo getBusinessTripService() {
		return new BusinessTripServiceImpl();
    }
	
	@Bean
	public PresentTimeRepo getPresentTimeService() {
		return new PresentTimeServiceImpl();
    }
}
