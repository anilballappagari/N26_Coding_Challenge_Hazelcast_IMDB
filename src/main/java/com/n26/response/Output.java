/**
 * 
 */
package com.n26.response;

import java.io.Serializable;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IFunction;
import com.n26.store.TransactionStore;

/**
 * @author Anil Ballappagari
 * @version 2.0
 *
 */
public class Output implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double sum;
	private double avg;
	private double max;
	private double min;
	private int count;

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getAvg() {
		return avg;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private double getMaxAmount() {
		Map<Long, Double> inputMap = TransactionStore.getInstance().getInMemoryStore().getMap("amount");
		if (inputMap.size() > 0) {
			return inputMap.values().parallelStream().max((d1, d2) -> Double.compare(d1, d2)).get();
		}
		return 0;
	}

	private double getMinAmount() {
		Map<Long, Double> inputMap = TransactionStore.getInstance().getInMemoryStore().getMap("amount");
		if (inputMap.size() > 0) {
			return inputMap.values().parallelStream().min((d1, d2) -> Double.compare(d1, d2)).get();
		}
		return 0;
	}

	public void update(double amount, boolean isExpired) {
		HazelcastInstance inMemoryStore = TransactionStore.getInstance().getInMemoryStore();
		double max = getMaxAmount();
		double min = getMinAmount();
		int count = inMemoryStore.getMap("input").size();
		IAtomicReference<Output> out = inMemoryStore.getAtomicReference("output");
		out.alter(new IFunction<Output, Output>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Output apply(Output out) {
				if (!isExpired) {
					out.setSum((double) Math.round((out.getSum() + amount) * 100) / 100);
				} else {
					out.setSum((double) Math.round((out.getSum() - amount) * 100) / 100);
				}
				out.setCount(count);
				if (count > 0) {
					out.setAvg((double) Math.round((out.getSum() / out.getCount() * 100)) / 100);
				} else {
					out.setAvg(0);
				}
				out.setMax(max);
				out.setMin(min);
				return out;
			}
		});
	}

	@Override
	public String toString() {
		return "Output [sum=" + sum + ", avg=" + avg + ", max=" + max + ", min=" + min + ", count=" + count + "]";
	}
}
