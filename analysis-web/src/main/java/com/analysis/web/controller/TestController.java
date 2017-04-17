package com.analysis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class TestController {

	@RequestMapping("/testJsp")
	public String testJsp(Model model) {
		return "index";
	}

	public static void main(String[] args) {
		
	}
}
