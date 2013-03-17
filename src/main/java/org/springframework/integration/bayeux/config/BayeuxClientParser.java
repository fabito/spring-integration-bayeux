package org.springframework.integration.bayeux.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class BayeuxClientParser extends AbstractSingleBeanDefinitionParser {

	@Override
	protected String getBeanClassName(Element element) {
		return "org.springframework.integration.bayeux.config.BayeuxClientFactoryBean";
	}

	@Override
	protected boolean shouldGenerateIdAsFallback() {
		return false;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext,
			BeanDefinitionBuilder builder) {

		String url = element.getAttribute("url");
		Assert.hasLength(url, "Url must be provided.");
		builder.addConstructorArgValue(url);

		tryWebsocketTransportIfRequired(element, builder);

		BeanDefinitionBuilder httpClientConfigurationBuilder = BeanDefinitionBuilder
				.genericBeanDefinition("org.eclipse.jetty.client.HttpClient");
		httpClientConfigurationBuilder.setInitMethodName("start");

		BeanDefinitionBuilder transportConfigurationBuilder = BeanDefinitionBuilder
				.genericBeanDefinition("org.cometd.client.transport.LongPollingTransport");
		transportConfigurationBuilder.addConstructorArgValue(null);
		transportConfigurationBuilder
				.addConstructorArgValue(httpClientConfigurationBuilder
						.getBeanDefinition());

		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
				"auto-startup");
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element,
				"handshake-timeout");

		builder.addConstructorArgValue(transportConfigurationBuilder
				.getBeanDefinition());

	}

	private void tryWebsocketTransportIfRequired(Element element,
			BeanDefinitionBuilder builder) {
		String tryWebsocket = element.getAttribute("try-websocket");
		if (StringUtils.hasText(tryWebsocket)
				&& "true".equalsIgnoreCase(tryWebsocket)) {

			BeanDefinitionBuilder wsFactoryConfigurationBuilder = BeanDefinitionBuilder
					.genericBeanDefinition("org.eclipse.jetty.websocket.WebSocketClientFactory");
			wsFactoryConfigurationBuilder.setInitMethodName("start");

			BeanDefinitionBuilder websocketConfigurationBuilder = BeanDefinitionBuilder
					.genericBeanDefinition("org.cometd.websocket.client.WebSocketTransport");
			websocketConfigurationBuilder.addConstructorArgValue(null);
			websocketConfigurationBuilder
					.addConstructorArgValue(wsFactoryConfigurationBuilder
							.getBeanDefinition());
			websocketConfigurationBuilder.addConstructorArgValue(null);

			builder.addConstructorArgValue(websocketConfigurationBuilder
					.getBeanDefinition());
		}
	}

}
