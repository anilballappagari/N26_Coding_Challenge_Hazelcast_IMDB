package com.n26.listener;

import java.util.Map;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.n26.request.Input;
import com.n26.response.Output;
import com.n26.store.TransactionStore;

/**
 * InputListener listens to input map of hazelcast.
 * 
 * <p>
 * when entry is added to input map of hazelcast, statistics are calculated.
 * </p>
 * 
 * <p>
 * when entry is removed from input map of hazelcast, statistics are
 * re-calculated.
 * </p>
 * 
 * @author Anil Ballappagari
 * @version 2.0
 */
public class InputListener implements EntryAddedListener<Long, Input>, EntryExpiredListener<Long, Input> {

	@Override
	public void entryAdded(EntryEvent<Long, Input> event) {
		IAtomicReference<Output> out = TransactionStore.getInstance().getInMemoryStore().getAtomicReference("output");
		if (out.get() == null) {
			out.set(new Output());
		}
		out.get().update(event.getValue().getAmount(), false);
	}

	@Override
	public void entryExpired(EntryEvent<Long, Input> event) {
		HazelcastInstance inMemoryStore = TransactionStore.getInstance().getInMemoryStore();
		Map<Long, Double> amountMap = inMemoryStore.getMap("amount");
		amountMap.remove(event.getKey());
		IAtomicReference<Output> out = inMemoryStore.getAtomicReference("output");
		if (out.get() == null) {
			out.set(new Output());
		}
		out.get().update(event.getOldValue().getAmount(), true);
	}
}
