package com.n26.listener;

import static com.n26.constants.N26Constants.HAZEL_AMOUNT;
import static com.n26.constants.N26Constants.HAZEL_STATISTICS;

import java.util.Map;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.n26.request.Transaction;
import com.n26.response.Statistics;
import com.n26.store.TransactionStore;

/**
 * TransactionListener listens to transaction map of hazelcast.
 * 
 * <p>
 * when entry is added to transaction map of hazelcast, statistics are calculated.
 * </p>
 * 
 * <p>
 * when entry is removed from transaction map of hazelcast, statistics are
 * re-calculated.
 * </p>
 * 
 * @author Anil Ballappagari
 * @version 2.0
 */
public class TransactionListener implements EntryAddedListener<Long, Transaction>, EntryExpiredListener<Long, Transaction> {

	@Override
	public void entryAdded(EntryEvent<Long, Transaction> event) {
		IAtomicReference<Statistics> stat = TransactionStore.getInstance().getInMemoryStore().getAtomicReference(HAZEL_STATISTICS);
		if (stat.get() == null) {
			stat.set(new Statistics());
		}
		stat.get().update(event.getValue().getAmount(), false);
	}

	@Override
	public void entryExpired(EntryEvent<Long, Transaction> event) {
		HazelcastInstance inMemoryStore = TransactionStore.getInstance().getInMemoryStore();
		Map<Long, Double> amountMap = inMemoryStore.getMap(HAZEL_AMOUNT);
		amountMap.remove(event.getKey());
		IAtomicReference<Statistics> stat = inMemoryStore.getAtomicReference(HAZEL_STATISTICS);
		if (stat.get() == null) {
			stat.set(new Statistics());
		}
		stat.get().update(event.getOldValue().getAmount(), true);
	}
}
