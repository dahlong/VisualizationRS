package org.narl.hrms.visual.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class SayHelloController {

	@RequestMapping("/hello")
	public String sayHello(Model model){
		model.addAttribute("msg","This is a Spring MVC with Jersey");
		return "hello" ;
	}
}
