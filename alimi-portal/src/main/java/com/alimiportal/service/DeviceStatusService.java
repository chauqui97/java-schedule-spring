package com.alimiportal.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

@Component
public class DeviceStatusService {
	public String getVariablesFromFactory(Integer seconds, Integer minutes, Integer hours, String time) {
		System.out.println("getVariablesFromFactory hours= " + hours);
		System.out.println("getVariablesFromFactory minutes= " + minutes);
		System.out.println("getVariablesFromFactory seconds= " + seconds);
		Map<String, Object> result = new HashMap<>();
		if (hours <= 23) {
			System.err.println("============");
			if (minutes < 10) {
				result.put("on", "true");
			} else if (minutes >= 10 && minutes <= 20) {
				if (seconds <= 4) {
					result.put("on", "true");
				} else if (seconds <= 6) {
					result.put("on", "true");
				} else {
					result.put("on", "true");
				}
			} else if (minutes > 20 && minutes <= 30) {
				if (seconds <= 4) {
					result.put("on", "true");
				} else {
					result.put("on", "true");
				}
			} else if (minutes > 30 && minutes <= 40) {
				if (seconds <= 4) {
					result.put("on", "true");
				} else {
					result.put("on", "false");
				}
			} else if (minutes > 40 && minutes <= 50) {
				if (seconds <= 30) {
					result.put("on", "true");
				} else {
					result.put("on", "false");
				}
			} else if (minutes > 50 && minutes <= 60) {
				result.put("on", "true");
			} else {
				result.put("on", "true");
			}
		} else if (hours == 13) {
			result.put("on", "true");

		}

		result.put("_time", time);

		// Convert to Json and then toString()
		JSONArray jsonResult = new JSONArray();
		jsonResult.put(result);
		System.err.println(jsonResult.toString());

		return jsonResult.toString();
	}
}
