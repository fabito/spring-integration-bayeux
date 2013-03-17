package org.springframework.integration.bayeux.config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;


import org.cometd.client.BayeuxClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.bayeux.inbound.MessageListeningEndpoint;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Fábio Franco Uechi
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class InboundChannelAdapterParserTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private QueueChannel bayeuxInbound;

	@Autowired
	private MessageChannel autoChannel;

	@Autowired @Qualifier("autoChannel.adapter")
	private MessageListeningEndpoint autoChannelAdapter;

	@Test
	public void testInboundAdapter(){
		MessageListeningEndpoint adapter = context.getBean("bayeuxInboundAdapter", MessageListeningEndpoint.class);
		MessageChannel errorChannel = (MessageChannel) TestUtils.getPropertyValue(adapter, "errorChannel");
		assertEquals(context.getBean("errorChannel"), errorChannel);
		assertFalse(adapter.isAutoStartup());
		QueueChannel channel = (QueueChannel) TestUtils.getPropertyValue(adapter, "outputChannel");
		assertEquals("bayeuxInbound", channel.getComponentName());
		BayeuxClient bayeuxClient = (BayeuxClient) TestUtils.getPropertyValue(adapter, "bayeuxClient");
		assertEquals(bayeuxClient, context.getBean("testBayeuxClient"));
	}
	
	@Test
	public void testAutoChannel() {
		assertSame(autoChannel, TestUtils.getPropertyValue(autoChannelAdapter, "outputChannel"));
	}
	
}
