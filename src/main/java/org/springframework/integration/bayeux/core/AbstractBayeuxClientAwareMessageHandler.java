package org.springframework.integration.bayeux.core;

import org.cometd.client.BayeuxClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.util.Assert;

/**
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public abstract class AbstractBayeuxClientAwareMessageHandler extends
		AbstractMessageHandler {

	protected volatile BayeuxClient bayeuxClient;

	protected volatile boolean initialized;

	public AbstractBayeuxClientAwareMessageHandler() {
	}

	public AbstractBayeuxClientAwareMessageHandler(BayeuxClient bayeuxClient) {
		Assert.notNull(bayeuxClient, "'bayeuxClient' must no be null");
		this.bayeuxClient = bayeuxClient;
	}

	protected void onInit() throws Exception {
		BeanFactory beanFactory = this.getBeanFactory();
		if (this.bayeuxClient == null && beanFactory != null) {
			this.bayeuxClient = beanFactory.getBean("bayeuxClient",
					BayeuxClient.class);
		}
		Assert.notNull(
				this.bayeuxClient,
				"Failed to resolve BayeuxClient. BayeuxClient must either be set expicitly "
						+ "via the 'bayeux-client' attribute or implicitly by registering a bean with the name 'bayeuxClient' and of type "
						+ "'org.cometd.client.BayeuxClient' in the Application Context.");
		this.initialized = true;
	}

}
