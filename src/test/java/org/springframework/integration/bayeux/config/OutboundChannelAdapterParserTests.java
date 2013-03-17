package org.springframework.integration.bayeux.config;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.message.GenericMessage;
import org.springframework.integration.test.util.TestUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Fábio Franco Uechi
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class OutboundChannelAdapterParserTests {

	@Autowired
	private ApplicationContext context;

	private static volatile int adviceCalled;
	
	@Test
	public void testEventConsumerWithNoChannel() {
		Object eventConsumer = context.getBean("outboundNoChannelAdapter");
		assertTrue(eventConsumer instanceof SubscribableChannel);
	}
	
	@Test
	public void advised() {
		MessageHandler handler = TestUtils.getPropertyValue(context.getBean("advised"),
				"handler", MessageHandler.class);
		handler.handleMessage(new GenericMessage<String>("foo"));
		assertEquals(1, adviceCalled);
	}

	@Test
	public void testEventConsumer() {
		Object eventConsumer = context.getBean("outboundEventAdapter");
		assertTrue(eventConsumer instanceof EventDrivenConsumer);
	}	
	
	public static class FooAdvice extends AbstractRequestHandlerAdvice {
		@Override
		protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) throws Exception {
			adviceCalled++;
			return null;
		}

	}
	
}