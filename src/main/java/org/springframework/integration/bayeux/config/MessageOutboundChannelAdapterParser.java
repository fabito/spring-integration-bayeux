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

		//IntegrationNamespaceUtils.configureHeaderMapper(element, builder, parserContext, DefaultXmppHeaderMapper.class, null);

		String connectionName = element.getAttribute("xmpp-connection");
		if (StringUtils.hasText(connectionName)){
			builder.addConstructorArgReference(connectionName);
		}
		else if (parserContext.getRegistry().containsBeanDefinition(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME)) {
			builder.addConstructorArgReference(BayeuxNamespaceHandler.BAYEUX_CLIENT_BEAN_NAME);
		}
		else {
			throw new BeanCreationException("You must either explicitly define which XMPP connection to use via " +
					"'xmpp-connection' attribute or have default XMPP connection bean registered under the name 'xmppConnection'" +
					"(e.g., <int-xmpp:xmpp-connection .../>). If 'id' is not provided the default will be 'xmppConnection'.");
		}

		return builder.getBeanDefinition();
	}
}
