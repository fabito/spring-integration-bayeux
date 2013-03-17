package org.springframework.integration.bayeux.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * This class parses the schema for Bayeux support.
 *
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public class BayeuxNamespaceHandler extends NamespaceHandlerSupport {

	public final static String BAYEUX_CLIENT_BEAN_NAME = "bayeuxClient";

	public void init() {
		// connection
		registerBeanDefinitionParser("bayeux-client", new BayeuxClientParser());

		// send/receive messages
		registerBeanDefinitionParser("inbound-channel-adapter", new MessageInboundChannelAdapterParser());
		registerBeanDefinitionParser("outbound-channel-adapter", new MessageOutboundChannelAdapterParser());

		registerBeanDefinitionParser("header-enricher", new BayeuxHeaderEnricherParser());

	}

}
