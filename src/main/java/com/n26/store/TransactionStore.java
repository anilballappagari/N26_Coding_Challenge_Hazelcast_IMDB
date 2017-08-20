package com.n26.store;

import static com.n26.constants.N26Constants.HAZEL_COUNTER;
import static com.n26.constants.N26Constants.HAZEL_TRANSACTION;
import static com.n26.constants.N26Constants.HAZEL_STATISTICS;

import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.n26.init.HazelCastFactory;
import com.n26.listener.TransactionListener;
import com.n26.request.Transaction;
import com.n26.response.Statistics;

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
	 * initialize {@code inMemoryStore} and add {@link TransactionListener} to input
	 * map.
	 */
	private TransactionStore() {
		inMemoryStore = HazelCastFactory.getInstance();
		IMap<Long, Transaction> transactionMap = inMemoryStore.getMap(HAZEL_TRANSACTION);
		transactionMap.addEntryListener(new TransactionListener(), true);
	}

	/**
	 * @return instance of {@link TransactionStore}
	 */
	public static final TransactionStore getInstance() {
		return STORE;
	}

	/**
	 * @return {@link Statistics} which contains the statistics
	 */
	public final Statistics getStatisticsFromStore() {
		IAtomicReference<Statistics> statistics = inMemoryStore.getAtomicReference(HAZEL_STATISTICS);
		return statistics.get();
	}

	/**
	 * add {@link Transaction} to hazelcast input map
	 * 
	 * @param input
	 *            which contains the timestamp and amount
	 */
	public void addToStore(Transaction input) {
		IMap<Long, Transaction> transactiontMap = inMemoryStore.getMap(HAZEL_TRANSACTION);
		IAtomicLong transactionCounter = inMemoryStore.getAtomicLong(HAZEL_COUNTER);
		transactiontMap.put(transactionCounter.incrementAndGet(), input, 60, TimeUnit.SECONDS);
		IMap<Long, Double> amountMap = inMemoryStore.getMap("amount");
		amountMap.put(transactionCounter.get(), input.getAmount());
	}
}
