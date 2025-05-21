package com.appsdeveloperblog.photoapp.api.account.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	Environment environment;
	
	@GetMapping("/status/check")
	public String status() {
		System.out.println("UsersController.status()");
		return "Working..."+environment.getProperty("local.server.port");
		
	}

}
