package com.n26.integration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.QuickTest;
import com.n26.constants.N26Constants;
import com.n26.request.Transaction;

@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class HazelCastInstanceTest extends HazelcastTestSupport {

	    private TestHazelcastInstanceFactory factory;
	    private HazelcastInstance inMemoryStore;

	    @Before
	    public void init() {
	        factory = new TestHazelcastInstanceFactory(1);
	        inMemoryStore = factory.newHazelcastInstance();
	    }

	    @After
	    public void tear() {
	        factory.shutdownAll();
	    }
	    
	    @Test
	    public void testTransactionInMemory() {
	    	IMap<String, Transaction> transactionMap = inMemoryStore.getMap(N26Constants.HAZEL_TRANSACTION);
	    	Transaction transaction = new Transaction();
	    	transaction.setAmount(200.45);
	    	transaction.setTimestamp(1503214913875l);
	    	transactionMap.put("Test", transaction);
	    	Map<String, Transaction> transactionMap1 = inMemoryStore.getMap(N26Constants.HAZEL_TRANSACTION);
	    	Assert.assertNotNull(transactionMap1.get("Test"));
	    	Assert.assertEquals(200.45, transactionMap1.get("Test").getAmount(), 0);
	    	Assert.assertEquals(true, transactionMap1.get("Test").getTimestamp() == transaction.getTimestamp());
	    }
	    
	    @Test
	    public void testTimeLivedForTransaction() throws Exception {
	    	IMap<String, Transaction> transactionMap = inMemoryStore.getMap(N26Constants.HAZEL_TRANSACTION);
	    	Transaction transaction = new Transaction();
	    	transaction.setAmount(200.45);
	    	transaction.setTimestamp(1503214913875l);
	    	transactionMap.put("Test1", transaction, 2, TimeUnit.SECONDS);
	    	Thread.sleep(2000);
	    	Map<String, Transaction> transactionMap1 = inMemoryStore.getMap(N26Constants.HAZEL_TRANSACTION);
	    	Assert.assertNull(transactionMap1.get("Test1"));
	    }
}
