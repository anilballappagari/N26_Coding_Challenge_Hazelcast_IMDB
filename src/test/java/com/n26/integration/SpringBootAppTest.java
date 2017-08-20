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
import com.n26.request.Transaction;
import com.n26.response.Statistics;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT, classes = InitApp.class)
public class SpringBootAppTest {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void testSuccessTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(1);
		transaction.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()-1000).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	transaction, String.class);
		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}
	
	@Test
	public void testFailureTransaction() {
		Transaction transaction = new Transaction();
		transaction.setAmount(1);
		long delay = TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - 61);
		transaction.setTimestamp(Instant.ofEpochMilli(delay).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	transaction, String.class);
		Assert.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
	}
	
	@Test
	public void testOutput() throws InterruptedException {
		Transaction transaction = new Transaction();
		transaction.setAmount(2);
		long delay = TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - 30);
		transaction.setTimestamp(Instant.ofEpochMilli(delay).toEpochMilli());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/transactions",	transaction, String.class);
		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Thread.sleep(500);
		ResponseEntity<String> stats = restTemplate.getForEntity("/statistics", String.class);
		Statistics stat = new Gson().fromJson(stats.getBody(), Statistics.class);
		Assert.assertEquals(stat.getSum(), 3, 0);
		Assert.assertEquals(stat.getCount(), 2);
		Assert.assertEquals(stat.getMax(), 2, 0);
		Assert.assertEquals(stat.getMin(), 1, 0);
		Assert.assertEquals(stat.getAvg(), 1.5, 0);
	}
	
	
}
