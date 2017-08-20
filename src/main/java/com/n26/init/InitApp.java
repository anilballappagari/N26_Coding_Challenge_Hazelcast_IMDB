package com.n26.init;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.n26.request.Input;
import com.n26.store.TransactionStore;
import com.n26.util.N26Utility;

@RestController
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.n26")
public class InitApp {

	private final static Logger LOGGER = Logger.getLogger(InitApp.class.getName());

	/**
	 * @return json output
	 */
	@RequestMapping(method = { RequestMethod.GET }, produces = { "application/json" }, path = "/statistics")
	String statistics() {
		LOGGER.info("Fetching transactions........");
		return new Gson().toJson(TransactionStore.getInstance().getOutputFromStore());
	}

	/**
	 * @param input
	 *            which needs to be calculated for statistics.
	 * @param response
	 * @return 201 if valid transaction else 204
	 */
	@RequestMapping(method = { RequestMethod.POST }, produces = { "application/json" }, consumes = {
			"application/json" }, path = "/transactions")
	ResponseEntity<String> transactions(@RequestBody Input input, HttpServletResponse response) {
		LOGGER.info("Adding the transaction.....");
		if (N26Utility.validateTransaction(input.getTimestamp())) {
			TransactionStore.getInstance().addToStore(input);
			LOGGER.info("Successfully added the transaction....");
			return new ResponseEntity<String>(HttpStatus.CREATED);
		}
		LOGGER.info("Failed to add transaction. The transaction is expired...");
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(InitApp.class, args);
	}
}
