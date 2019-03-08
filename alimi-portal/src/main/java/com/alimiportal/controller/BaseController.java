package com.alimiportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alimiportal.scheduler.ScheduledConfiguration;
import com.alimiportal.service.AuthService;

@CrossOrigin("*")
@RestController
public class BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	AuthService authService;

	@Autowired
	ScheduledConfiguration scheduledConfiguration;

	@GetMapping("/hello")
	public Map<String, String> hello() {
		Map<String, String> persons = new HashMap<String, String>();

		persons.put("person", "hello");
		return persons;

	}

//	@GetMapping("/pushTimeseries")
//	public ResponseEntity<DeviceStatus> pushTimeseries(HttpServletRequest request)
//			throws ClientProtocolException, IOException {
//		return DeviceStatusService.pushTimeseries(authService.extractToken(request));
//	}

	@GetMapping("/showToken")
	public String extractToken(HttpServletRequest request) {
		return authService.extractToken(request);
	}

}
