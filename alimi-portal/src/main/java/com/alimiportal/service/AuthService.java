package com.alimiportal.service;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

	public String extractToken(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			LOGGER.info("header " + header + " " + request.getHeader(header));
		}
		return request.getHeader("authorization");
	}
}
