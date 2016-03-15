package org.narl.hrms.visual.rest;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.narl.hrms.visual.base.BaseTestCase;
import org.narl.hrms.visual.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BaseTestCase.class, loader = AnnotationConfigContextLoader.class)
public class SampleServiceTestCase {
	
	@Autowired
	TestServiceImpl mytest ;
	
	@Test
	public void sample(){
		
		assertEquals("Hi~dahlong",mytest.sayHi("dahlong"));
	}
	
}
