package org.narl.hrms.visual.service.impl;

import org.narl.hrms.visual.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService{

	public String sayHi(String name){
		return "Hi~"+name ;
	}
	
}
