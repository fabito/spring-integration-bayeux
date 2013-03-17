package org.springframework.integration.bayeux.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for 'bayeux:outbound-channel-adapter' element
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public class MessageOutboundChannelAdapterParser extends
		AbstractOutboundChannelAdapterParser {

	@Override
	protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.integration.bayeux.outbound.MessageSendingMessageHandler");

		//IntegrationNamespaceUtils.configureHeaderMapper(element, builder, parserContext, DefaultBayeuxHeaderMapper.class, null);

		String bayeuxClient = element.getAttribute("bayeux-client");

		if (StringUtils.hasText(bayeuxClient)){
			builder.addConstructorArgReference(bayeuxClient);
		}
		else if (parserContext.getRegistry().containsBeanDefinition(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME)) {
			builder.addConstructorArgReference(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME);
		}
		else {
			throw new BeanCreationException("You must either explicitly define which Bayeux Cliente to use via " +
					"'bayeux-client' attribute or have default Bayeux Client bean registered under the name 'bayeuxClient'" +
					"(e.g., <int-bayeux:bayeux-client .../>). If 'id' is not provided the default will be 'bayeuxClient'.");
		}
		
		String bayeuxChannelName = element.getAttribute("bayeux-channel");
		if (StringUtils.hasText(bayeuxChannelName)) {
			builder.addConstructorArgValue(bayeuxChannelName);
		} else {
			throw new BeanCreationException(
					"You must explicitly define which channel the BayeuxClient will be listening to via the "
							+ "'bayeux-channel' attribute.");
		}

		return builder.getBeanDefinition();
	}
}
