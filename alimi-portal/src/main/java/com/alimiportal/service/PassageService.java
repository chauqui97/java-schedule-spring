package com.alimiportal.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
public class PassageService {
	
	private Random random = new Random();
	
	private String rdValue;

	public String getVariablesFromFactory(Integer seconds, Integer minutes, Integer hours, String time) {
		System.out.println("getVariablesFromFactory hours= " + hours);
		System.out.println("getVariablesFromFactory minutes= " + minutes);
		System.out.println("getVariablesFromFactory seconds= " + seconds);
		rdValue = String.format("%.1f", random.nextFloat() * (100 - 1) + 100);

		System.out.println("getVariablesFromFactory rdValue= " + rdValue);
		
		Map<String, Object> result = new HashMap<>();
		
		if (hours <= 23) {
			System.err.println("============");
			if (minutes < 10) {
				result.put("passage", rdValue);
			} else if (minutes >= 10 && minutes <= 20) {
				if (seconds <= 4) {
					result.put("passage", rdValue);
				} else if (seconds <= 6) {
					result.put("passage", rdValue);
				} else {
					result.put("passage", rdValue);
				}
			} else if (minutes > 20 && minutes <= 30) {
				if (seconds <= 4) {
					result.put("passage", rdValue);
				} else {
					result.put("passage", rdValue);
				}
			} else if (minutes > 30 && minutes <= 40) {
				if (seconds <= 4) {
					result.put("passage", rdValue);
				} else {
					result.put("passage", rdValue);
				}
			} else if (minutes > 40 && minutes <= 50) {
				if (seconds <= 30) {
					result.put("passage", rdValue);
				} else {
					result.put("passage", rdValue);
				}
			} else if (minutes > 50 && minutes <= 60) {
				result.put("passage", rdValue);
			} else {
				result.put("passage", rdValue);
			}
		} else if (hours == 13) {
			result.put("passage", rdValue);

		}

		result.put("_time", time);

		// Convert to Json and then toString()
		JSONArray jsonResult = new JSONArray();
		jsonResult.put(result);
		System.err.println(jsonResult.toString());

		return jsonResult.toString();
	}
}
