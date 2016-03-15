package org.narl.hrms.visual;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

public class ApplicationConfig extends ResourceConfig {

	private Logger logger = Logger.getLogger(ApplicationConfig.class);
	
	public ApplicationConfig(){
		
		logger.info("MyApplication started!.........................");
		
		register(RequestContextFilter.class);
		register(JacksonFeature.class);
		packages("org.narl.hrms.visual.rest");

	}
}
