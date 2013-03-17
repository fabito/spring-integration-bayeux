package org.springframework.integration.bayeux.config;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InboundChannelAdapterParserTests {

	@Test
	public void testInboundChannelAdapterParser() {
		new ClassPathXmlApplicationContext("InboundChannelAdapterParserTests-context.xml", this.getClass());
		// no assertion needed. THe fact that no exception was thrown satisfies this test
	}
	
}
