package com.n26.init;

import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.hazelcast.config.Config;

/**
 * Initialize the default config for HazelCast
 * 
 * @author Anil Ballappagari
 * @version 1.0
 *
 */
@Configuration
@Component
public class APPConfig {

	private final static Logger LOGGER = Logger.getLogger(APPConfig.class.getName());

	@Bean
	public Config config() {
		LOGGER.info("Initiliazing the default hazel config...");
		return new Config();
	}

}
