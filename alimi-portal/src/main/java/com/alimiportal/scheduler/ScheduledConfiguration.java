package com.alimiportal.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.client.RestTemplate;

import com.alimiportal.common.MindsphereUtils;
import com.alimiportal.service.AuthService;
import com.alimiportal.service.DeviceStatusService;
import com.alimiportal.service.MotorService;
import com.alimiportal.service.PassageService;

@Configuration
@EnableScheduling
public class ScheduledConfiguration implements SchedulingConfigurer {

	TaskScheduler taskScheduler;
	private ScheduledFuture<?> jobDeviceStatusService;
	private ScheduledFuture<?> jobMotorService;
	private ScheduledFuture<?> jobPassageService;

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

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(10);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-thread");
		threadPoolTaskScheduler.initialize();

		jobDeviceStatusService(threadPoolTaskScheduler);
//		jobMotorService(threadPoolTaskScheduler);
//		jobPassageService(threadPoolTaskScheduler);
		this.taskScheduler = threadPoolTaskScheduler;
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

	}

	private void jobDeviceStatusService(TaskScheduler scheduler) {
		jobDeviceStatusService = scheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = deviceStatusService.getVariablesFromFactory(LocalTime.now().getSecond(),
						LocalTime.now().getMinute(), LocalTime.now().getHour(), dateFormat.format(new Date()));
				try {
					getToken();
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(1000);
					pushTimeseries(poin, "DeviceStatus", "");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
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

	private void jobMotorService(TaskScheduler scheduler) {
		jobMotorService = scheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = motorService.getVariablesFromFactory(LocalTime.now().getSecond(), LocalTime.now().getMinute(),
						LocalTime.now().getHour(), dateFormat.format(new Date()));

				try {
					Thread.sleep(10000);
					pushTimeseries(poin, "Motor", "");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
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

	private void jobPassageService(TaskScheduler scheduler) {
		jobPassageService = scheduler.schedule(new Runnable() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String poin = "";

			@Override
			public void run() {

				poin = passageService.getVariablesFromFactory(LocalTime.now().getSecond(), LocalTime.now().getMinute(),
						LocalTime.now().getHour(), dateFormat.format(new Date()));
				try {
					getToken();
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(10000);
					pushTimeseries(poin, "PassageStatus", "");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
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

	public ResponseEntity<Void> pushTimeseries(String poin, String aspectName, String token)
			throws ClientProtocolException, IOException {
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
			System.out.println("** Exception: " + eek.getMessage());
		}

		return response;
	}

	public String getToken() throws ClientProtocolException, IOException {
		final String uri = "https://fpt-sayhi-fpt.cn1-pre.mindsphere-in.cn/hello";
		String result = "";
		RestTemplate restTemplate = new RestTemplate();

		try {
			result = restTemplate.getForObject(uri, String.class);
			System.out.println("result " + result.toString());

			return result;
		} catch (Exception e) {
			System.out.println("** Exception: " + e.getMessage());
			return "";
		}

	}

	public HttpHeaders createHttpHeaders() {
		String token = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImtleS1pZC0xIiwidHlwIjoiSldUIn0.eyJqdGkiOiI3ZmYwOGRhYmRiMWM0YjBmYjBhYmRlNjNlOTNlMjY5OCIsInN1YiI6IjExY2E3ZmU5LTMxYjEtNGQyNy1hNGMxLWQ4MTgzMTQ3OTk3ZiIsInNjb3BlIjpbInVhYS5vZmZsaW5lX3Rva2VuIiwibWRzcDpjb3JlOmlvdC50aW1BZG1pbiIsInNheWhpLmFjY2VzcyIsIm1kc3A6Y29yZTphc3NldG1hbmFnZW1lbnQuc3RhbmRhcmR1c2VyIl0sImNsaWVudF9pZCI6InNheWhpLWZwdCIsImNpZCI6InNheWhpLWZwdCIsImF6cCI6InNheWhpLWZwdCIsImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJ1c2VyX2lkIjoiMTFjYTdmZTktMzFiMS00ZDI3LWE0YzEtZDgxODMxNDc5OTdmIiwib3JpZ2luIjoiZnB0IiwidXNlcl9uYW1lIjoicXVpY3R0QGZzb2Z0LmNvbS52biIsImVtYWlsIjoiUXVpQ1RUQGZzb2Z0LmNvbS52biIsImF1dGhfdGltZSI6MTU1MTUyMzkzNywicmV2X3NpZyI6ImIwZTNmMjNlIiwiaWF0IjoxNTUxNTIzOTM4LCJleHAiOjE1NTE1MjU3MzgsImlzcyI6Imh0dHBzOi8vZnB0LnBpYW0uY24xLXByZS5taW5kc3BoZXJlLWluLmNuL29hdXRoL3Rva2VuIiwiemlkIjoiZnB0IiwiYXVkIjpbIm1kc3A6Y29yZTppb3QiLCJ1YWEiLCJtZHNwOmNvcmU6YXNzZXRtYW5hZ2VtZW50Iiwic2F5aGktZnB0Iiwic2F5aGkiXSwidGVuIjoiZnB0Iiwic2NoZW1hcyI6WyJ1cm46c2llbWVuczptaW5kc3BoZXJlOmlhbTp2MSJdLCJjYXQiOiJ1c2VyLXRva2VuOnYxIn0.o7vrIXFZjris4s9r4bv0Xh4DOgvUSLvbyLe6GeWdKB99PSbv2V-ne-GoQ4OuvgUhcGoMPkUc3i90cybQivMl2FIJ7psSmP-IYvwMXxndAlu-Qtx3pwqG-JHlUPy1sQuNDxpuXGpUgDh9U1lUEMil2qEItGXPUfFH9pcWUtn9gWnjj7pu4iRNg78PPvngYQaN9QGuudMz72N7WPM078BCypd8G46r2-DIY_a1XDCr-db5GV7jjWnw9CBa_5xuf0c-Dc_C34qSGjsW-sa70Oky_CP8tLPAGdyw1hcreNvOIODtWNOoxLcKcu5y7cNPJML11pRARTXDBGACgqBtFSMS1g";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", token);
		return headers;
	}
}
