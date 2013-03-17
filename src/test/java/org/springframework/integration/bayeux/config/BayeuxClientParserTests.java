package org.springframework.integration.bayeux.config;

import static junit.framework.Assert.assertTrue;

import org.cometd.client.BayeuxClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BayeuxClientParserTests {
	
	@Test
	public void testSimpleConfiguration() throws Exception {
		ApplicationContext ac = new ClassPathXmlApplicationContext("BayeuxClientParserTests-simple.xml", this.getClass());
		BayeuxClient client  = ac.getBean("bayeuxClient", BayeuxClient.class);
		assertTrue(client.isConnected());
	}
	
}
