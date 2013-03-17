package org.springframework.integration.bayeux.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for 'bayeux:inbound-channel-adapter' element
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public class MessageInboundChannelAdapterParser extends
		AbstractChannelAdapterParser {

	@Override
	protected boolean shouldGenerateId() {
		return false;
	}

	@Override
	protected boolean shouldGenerateIdAsFallback() {
		return true;
	}

	protected AbstractBeanDefinition doParse(Element element, ParserContext parserContext, String channelName) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.integration.bayeux.inbound.MessageListeningEndpoint");

		//TODO IntegrationNamespaceUtils.configureHeaderMapper(element, builder, parserContext, DefaultXmppHeaderMapper.class, null);

		String connectionName = element.getAttribute("bayeux-client");

		if (StringUtils.hasText(connectionName)){
			builder.addConstructorArgReference(connectionName);
		}
		else if (parserContext.getRegistry().containsBeanDefinition(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME)) {
			builder.addConstructorArgReference(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME);
		}
		else {
			throw new BeanCreationException("You must either explicitly define which Bayeux Cliente to use via " +
					"'bayeux-client' attribute or have default Bayeux Client bean registered under the name 'bayeuxClient'" +
					"(e.g., <int-bayeux:bayeux-client .../>). If 'id' is not provided the default will be 'bayeuxClient'.");
		}
		builder.addPropertyReference("outputChannel", channelName);
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, "auto-startup");
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "error-channel");
		this.postProcess(element, parserContext, builder);
		return builder.getBeanDefinition();
	}

	protected void postProcess(Element element, ParserContext parserContext, BeanDefinitionBuilder builder){
		// no op
	}

}
