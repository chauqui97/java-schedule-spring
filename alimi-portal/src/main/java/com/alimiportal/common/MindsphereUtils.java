package com.alimiportal.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MindsphereUtils {
	// Init a http header
	public HttpHeaders createHttpHeaders(String token) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization",
				token);

		return headers;
	}
}
