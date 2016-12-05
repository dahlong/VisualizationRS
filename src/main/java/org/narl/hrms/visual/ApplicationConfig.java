package org.narl.hrms.visual;

import javax.ws.rs.ApplicationPath;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

@ApplicationPath("/") // path 改為 "/rest" ， test.html 網頁才能顯示 
public class ApplicationConfig extends ResourceConfig {

	private Logger logger = Logger.getLogger(ApplicationConfig.class);
	
	public ApplicationConfig(){
		
		logger.info("MyApplication started!.........................");
		
		register(RequestContextFilter.class);
		register(JacksonFeature.class);
		packages("org.narl.hrms.visual.rest");

	}
}
