package com.n26.init;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;

/**
 * Initialize the {@link HazelcastInstance} which connects to default port.
 * 
 * Spring boot take's care of Initializing the HazelCast Server
 * 
 * @author Anil Ballappagari
 * @version 2.0
 *
 */
public class HazelCastFactory {
	private static HazelcastInstance hazelCastCluster;
	private static boolean isHazelShutDown = true;

	/**
	 * @return {@link HazelcastInstance} connected to hazelcast server
	 */
	public static HazelcastInstance getInstance() {
		if (isHazelShutDown) {
			ClientConfig clientConfig = new ClientConfig();
			ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
			clientNetworkConfig.addAddress("127.0.0.1:5701");
			clientConfig.setNetworkConfig(clientNetworkConfig);
			hazelCastCluster = HazelcastClient.newHazelcastClient(clientConfig);
			isHazelShutDown = false;
		}
		return hazelCastCluster;
	}

	/**
	 * Shutdown the {@link HazelcastInstance} 
	 */
	public static void shutDown() {
		hazelCastCluster.shutdown();
		isHazelShutDown = true;
	}
}