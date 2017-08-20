package com.n26.integration;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.n26.init.InitApp;
import com.n26.request.Input;
import com.n26.response.Output;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = InitApp.class)
public class SpringBootAppTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void testSuccessTransaction() {
		Input input = new Input();
		input.setAmount(1);
		input.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()-1000).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	input, String.class);
		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}
	
	@Test
	public void testFailureTransaction() {
		Input input = new Input();
		input.setAmount(1);
		long delay = TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - 61);
		input.setTimestamp(Instant.ofEpochMilli(delay).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	input, String.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
	
	@Test
	public void testOutput() throws InterruptedException {
		Input input = new Input();
		input.setAmount(2);
		long delay = TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - 30);
		input.setTimestamp(Instant.ofEpochMilli(delay).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	input, String.class);
		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Thread.sleep(500);
		ResponseEntity<String> output = restTemplate.getForEntity("/statistics", String.class);
		Output out = new Gson().fromJson(output.getBody(), Output.class);
		Assert.assertEquals(out.getSum(), 3, 0);
		Assert.assertEquals(out.getCount(), 2);
		Assert.assertEquals(out.getMax(), 2, 0);
		Assert.assertEquals(out.getMin(), 1, 0);
		Assert.assertEquals(out.getAvg(), 1.5, 0);
	}
	
	
}
