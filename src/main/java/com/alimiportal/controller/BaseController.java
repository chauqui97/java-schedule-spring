package com.alimiportal.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alimiportal.scheduler.ScheduledConfiguration;
import com.alimiportal.service.AuthService;

@CrossOrigin("*")
@RestController
public class BaseController {

	@Autowired
	AuthService authService;

	@Autowired
	ScheduledConfiguration scheduledConfiguration;

	@GetMapping("/")
	public String hello() {
		return "Welcome to alimi";
	}

	@RequestMapping("/start")
	ResponseEntity<String> start(HttpServletRequest request) {
		try {
			scheduledConfiguration.jobDeviceStatusService(authService.extractToken(request));
			scheduledConfiguration.jobMotorService(authService.extractToken(request));
			scheduledConfiguration.jobPassageService(authService.extractToken(request));
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("faild", HttpStatus.IM_USED);
		}
	}

	// 3/3 on cf, start is avaible but stop not is. just ok for fisrt time
	// basiclly start and stop job on localhost okay.
	@RequestMapping("/stop")
	ResponseEntity<String> stop() {

		scheduledConfiguration.refreshCronSchedule();
		return new ResponseEntity<String>("success", HttpStatus.OK);

	}

	@GetMapping("/showToken")
	public String extractToken(HttpServletRequest request) {
		return authService.extractToken(request);
	}

}
