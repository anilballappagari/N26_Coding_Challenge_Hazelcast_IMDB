/**
 * 
 */
package com.n26.response;

import static com.n26.constants.N26Constants.HAZEL_AMOUNT;
import static com.n26.constants.N26Constants.HAZEL_TRANSACTION;
import static com.n26.constants.N26Constants.HAZEL_STATISTICS;

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
public class Statistics implements Serializable {
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
		Map<Long, Double> transactionMap = TransactionStore.getInstance().getInMemoryStore().getMap(HAZEL_AMOUNT);
		if (transactionMap.size() > 0) {
			return transactionMap.values().parallelStream().max((d1, d2) -> Double.compare(d1, d2)).get();
		}
		return 0;
	}

	private double getMinAmount() {
		Map<Long, Double> transactionMap = TransactionStore.getInstance().getInMemoryStore().getMap(HAZEL_AMOUNT);
		if (transactionMap.size() > 0) {
			return transactionMap.values().parallelStream().min((d1, d2) -> Double.compare(d1, d2)).get();
		}
		return 0;
	}

	public void update(double amount, boolean isExpired) {
		HazelcastInstance inMemoryStore = TransactionStore.getInstance().getInMemoryStore();
		double max = getMaxAmount();
		double min = getMinAmount();
		int count = inMemoryStore.getMap(HAZEL_TRANSACTION).size();
		IAtomicReference<Statistics> stats = inMemoryStore.getAtomicReference(HAZEL_STATISTICS);
		stats.alter(new IFunction<Statistics, Statistics>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Statistics apply(Statistics stat) {
				if (!isExpired) {
					stat.setSum((double) Math.round((stat.getSum() + amount) * 100) / 100);
				} else {
					stat.setSum((double) Math.round((stat.getSum() - amount) * 100) / 100);
				}
				stat.setCount(count);
				if (count > 0) {
					stat.setAvg((double) Math.round((stat.getSum() / stat.getCount() * 100)) / 100);
				} else {
					stat.setAvg(0);
				}
				stat.setMax(max);
				stat.setMin(min);
				return stat;
			}
		});
	}

	@Override
	public String toString() {
		return "Statistics [sum=" + sum + ", avg=" + avg + ", max=" + max + ", min=" + min + ", count=" + count + "]";
	}
}
