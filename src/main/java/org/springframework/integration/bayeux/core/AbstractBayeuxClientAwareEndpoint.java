package org.springframework.integration.bayeux.core;

import org.cometd.client.BayeuxClient;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.util.Assert;

/**
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public abstract class AbstractBayeuxClientAwareEndpoint extends
		MessageProducerSupport {

	protected volatile BayeuxClient bayeuxClient;

	protected volatile boolean initialized;

	public AbstractBayeuxClientAwareEndpoint() {
	}

	public AbstractBayeuxClientAwareEndpoint(BayeuxClient bayeuxClient) {
		Assert.notNull(bayeuxClient, "'bayeuxClient' must no be null");
		this.bayeuxClient = bayeuxClient;
	}
	
	@Override
	protected void onInit() {
		super.onInit();
		BeanFactory beanFactory = this.getBeanFactory();
		if (this.bayeuxClient == null && beanFactory != null) {
			this.bayeuxClient = beanFactory.getBean("bayeuxClient", BayeuxClient.class);
		}
		Assert.notNull(this.bayeuxClient, "Failed to resolve BayeuxClient. BayeuxClient must either be set expicitly " +
				"via the 'bayeux-client' attribute or implicitly by registering a bean with the name 'bayeuxClient' and of type " +
				"'org.cometd.client.BayeuxClient' in the Application Context.");
		this.initialized = true;
	}

}
