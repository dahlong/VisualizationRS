package org.narl.hrms.visual.service;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/resources/applicationContext.xml" })
public class SampleServiceTestCase {
	
	@Autowired
	TestService mytest ;
	
	@Test
	public void sample(){
		
		assertEquals("Hi~dahlong",mytest.sayHi("dahlong"));
	}
	
}
