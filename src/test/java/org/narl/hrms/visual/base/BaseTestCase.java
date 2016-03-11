package org.narl.hrms.visual.base;

import org.narl.hrms.visual.service.TestService;
import org.narl.hrms.visual.service.impl.TestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseTestCase {
	@Bean
	public TestService getSampleService() {
		return new TestServiceImpl();
    }

}

