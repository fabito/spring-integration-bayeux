package org.springframework.integration.bayeux.config;


import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
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

	@Test
	public void testEventConsumerWithNoChannel() {
		Object eventConsumer = context.getBean("outboundNoChannelAdapter");
		assertTrue(eventConsumer instanceof SubscribableChannel);
	}

	public static class FooAdvice extends AbstractRequestHandlerAdvice {
		@Override
		protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) throws Exception {
			//adviceCalled++;
			return null;
		}

	}
	
}
