package org.springframework.integration.bayeux.config;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.mock;

import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author fabio
 *
 */
public class BayeuxClientFactoryBeanTests {

	@Test
	public void testBayeuxClientFactoryBean() throws Exception {
		BayeuxClientFactoryBean bayeuxClientFactoryBean = new BayeuxClientFactoryBean("http://localhost:8080/cometd", mock(ClientTransport.class));
		BayeuxClient bayeuxClient = bayeuxClientFactoryBean.createInstance();
		assertNotNull(bayeuxClient);
	}
	
	@Test
	public void testBayeuxClientFactoryBeanViaConfig() throws Exception {
		new ClassPathXmlApplicationContext("BayeuxClientFactoryBeanTests-context.xml", this.getClass());
		// the fact that no exception was thrown satisfies this test
	}
	
	@Test
	public void testBayeuxClientFactoryBeanViaConfig2() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("BayeuxClientFactoryBeanTests-context.xml", this.getClass());
		BayeuxClient bayeuxClient = ctx.getBean(BayeuxClient.class);
		bayeuxClient.waitFor(60 * 1000, BayeuxClient.State.DISCONNECTED);
		//assertTrue(bayeuxClient.isConnected());
	}

}
