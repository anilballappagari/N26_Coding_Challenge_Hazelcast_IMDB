package com.n26.store;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.n26.init.HazelCastFactory;
import com.n26.listener.InputListener;
import com.n26.request.Input;
import com.n26.response.Output;

/**
 * TransactionStore takes care of storing the data to Hazelcast IMDB's.
 * 
 * @author Anil Ballappagari
 * @version 2.0
 *
 */
public final class TransactionStore {

	private final HazelcastInstance inMemoryStore;

	public HazelcastInstance getInMemoryStore() {
		return inMemoryStore;
	}

	private static final TransactionStore STORE;

	static {
		STORE = new TransactionStore();
	}

	/**
	 * initialize {@code inMemoryStore} and add {@link InputListener} to input
	 * map.
	 */
	private TransactionStore() {
		inMemoryStore = HazelCastFactory.getInstance();
		IMap<Long, Input> inputMap = inMemoryStore.getMap("input");
		inputMap.addEntryListener(new InputListener(), true);
	}

	/**
	 * @return instance of {@link TransactionStore}
	 */
	public static final TransactionStore getInstance() {
		return STORE;
	}

	/**
	 * @return {@link Output} which contains the statistics
	 */
	public final Output getOutputFromStore() {
		IAtomicReference<Output> output = inMemoryStore.getAtomicReference("output");
		return output.get();
	}

	/**
	 * add {@link Input} to hazelcast input map
	 * 
	 * @param input
	 *            which contains the timestamp and amount
	 */
	public void addToStore(Input input) {
		IMap<Long, Input> inputMap = inMemoryStore.getMap("input");
		IAtomicLong inputCounter = inMemoryStore.getAtomicLong("inputCounter");
		inputMap.put(inputCounter.incrementAndGet(), input, 60, TimeUnit.SECONDS);
		IMap<Long, Double> amountMap = inMemoryStore.getMap("amount");
		amountMap.put(inputCounter.get(), input.getAmount());
	}
}
