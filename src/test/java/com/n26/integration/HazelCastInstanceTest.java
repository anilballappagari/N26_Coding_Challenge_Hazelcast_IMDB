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
import com.n26.request.Input;

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
	    public void testInputInMemory() {
	    	IMap<String, Input> inputMap = inMemoryStore.getMap("input");
	    	Input input = new Input();
	    	input.setAmount(200.45);
	    	input.setTimestamp(1503214913875l);
	    	inputMap.put("Test", input);
	    	Map<String, Input> inputMap1 = inMemoryStore.getMap("input");
	    	Assert.assertNotNull(inputMap1.get("Test"));
	    	Assert.assertEquals(200.45, inputMap1.get("Test").getAmount(), 0);
	    	Assert.assertEquals(true, inputMap1.get("Test").getTimestamp() == input.getTimestamp());
	    }
	    
	    @Test
	    public void testTimeLivedForInput() throws Exception {
	    	IMap<String, Input> inputMap = inMemoryStore.getMap("input");
	    	Input input = new Input();
	    	input.setAmount(200.45);
	    	input.setTimestamp(1503214913875l);
	    	inputMap.put("Test1", input, 2, TimeUnit.SECONDS);
	    	Thread.sleep(10000);
	    	Map<String, Input> inputMap1 = inMemoryStore.getMap("input");
	    	Assert.assertNull(inputMap1.get("Test1"));
	    }
}
