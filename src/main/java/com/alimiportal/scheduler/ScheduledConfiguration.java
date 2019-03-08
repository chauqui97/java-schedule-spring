package com.alimiportal.scheduler;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alimiportal.common.MindsphereUtils;
import com.alimiportal.service.AuthService;
import com.alimiportal.service.DeviceStatusService;
import com.alimiportal.service.MotorService;
import com.alimiportal.service.PassageService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ScheduledConfiguration {

	@Autowired
	TaskScheduler taskScheduler;

	private ScheduledFuture<?> jobDeviceStatusService;
	private ScheduledFuture<?> jobMotorService;
	private ScheduledFuture<?> jobPassageService;

	private ScheduledFuture<?> scheduledFuture;

	@Autowired
	DeviceStatusService deviceStatusService;

	@Autowired
	MotorService motorService;

	@Autowired
	PassageService passageService;

	@Autowired
	AuthService authService;

	@Autowired
	MindsphereUtils mindsphereUtils;

	public void jobDeviceStatusService(String token) {
		jobDeviceStatusService = taskScheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = deviceStatusService.getVariablesFromFactory(LocalTime.now().getSecond(),
						LocalTime.now().getMinute(), LocalTime.now().getHour(), dateFormat.format(new Date()));

				System.err.println(new Date());
				try {
					Thread.sleep(10000);
					pushTimeseries(poin, "DeviceStatus", token);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Trigger() {

			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				String cronExp = "0/5 * * * * ?";
				// every 10 minutes for hours 12-23 (2 0/10 12-23 * * ?)
				return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
			}
		});
	}

	public void jobMotorService(String token) {
		jobMotorService = taskScheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = motorService.getVariablesFromFactory(LocalTime.now().getSecond(), LocalTime.now().getMinute(),
						LocalTime.now().getHour(), dateFormat.format(new Date()));

				try {
					Thread.sleep(10000);
					pushTimeseries(poin, "Motor", token);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Trigger() {

			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				String cronExp = "0/5 * * * * ?";
				return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
			}
		});
	}

	public void jobPassageService(String token) {
		jobPassageService = taskScheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = passageService.getVariablesFromFactory(LocalTime.now().getSecond(), LocalTime.now().getMinute(),
						LocalTime.now().getHour(), dateFormat.format(new Date()));
				try {
					Thread.sleep(10000);
					pushTimeseries(poin, "PassageStatus", token);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}, new Trigger() {

			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				String cronExp = "0/5 * * * * ?";
				return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
			}
		});
	}

	public Boolean refreshCronSchedule() {
		Boolean response = false;
		try {
			if (!jobDeviceStatusService.isCancelled() || jobDeviceStatusService != null) {
				response = jobDeviceStatusService.cancel(true);
			}
			if (!jobMotorService.isCancelled() || jobMotorService != null) {
				response = jobMotorService.cancel(true);
			}
			if (!jobPassageService.isCancelled() || jobPassageService != null) {
				response = jobPassageService.cancel(true);
			}
		} catch (Exception e) {
			System.err.println("response " + e.getMessage());

		}
		System.err.println("response " + response);
		return response;
	}

	public ResponseEntity<Void> pushTimeseries(String poin, String aspectName, String token) {
		final String uri = "https://gateway.cn1-pre.mindsphere-in.cn/api/iottimeseries/v3/timeseries/74535565b6ef42c4a62727a4e2d9c124/"
				+ aspectName;

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Void> response = null;
		try {

			HttpHeaders headers = mindsphereUtils.createHttpHeaders(token);
			HttpEntity<Object> entity = new HttpEntity<Object>(poin, headers);
			response = restTemplate.exchange(uri, HttpMethod.PUT, entity, Void.class);
			System.out.println("Result - status (" + response.getStatusCode() + ") has body: " + response.hasBody());
		} catch (Exception eek) {
			refreshCronSchedule();
			System.out.println("** Exception: " + eek.getMessage());
		}
		return response;
	}

}
