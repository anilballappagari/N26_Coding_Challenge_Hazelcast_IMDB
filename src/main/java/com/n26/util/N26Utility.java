package com.n26.util;

import java.time.Instant;

public final class N26Utility {

	private N26Utility() {}
	
	public static final boolean validateTransaction(long epochMillis) {
		long diff = (Instant.ofEpochMilli(System.currentTimeMillis()).toEpochMilli() - epochMillis)/1000;
		return  (diff <= 60 && diff > 0) ? true: false;
	}
}
