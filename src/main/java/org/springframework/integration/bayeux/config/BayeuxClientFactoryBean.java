package org.springframework.integration.bayeux.config;

import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.SmartLifecycle;


/**
 * This class configures an {@link org.cometd.client.BayeuxClient} object. 
 * This object is used for all scenarios to talk to 
 *
 * @author Fábio Franco Uechi
 * @see org.cometd.client.BayeuxClient
 * @since 2.2.2
 */
public class BayeuxClientFactoryBean extends AbstractFactoryBean<BayeuxClient> implements
		SmartLifecycle {

	private String url;
	
	private ClientTransport defaultClientTransport;
	
	private ClientTransport[] transports;
	
	private BayeuxClient bayeuxClient;
	
	private volatile boolean autoStartup = true;

	private volatile int handshakeTimeout = 60 * 1000;
	
	private volatile int phase = Integer.MIN_VALUE;

	private final Object lifecycleMonitor = new Object();

	private volatile boolean running;

	public BayeuxClientFactoryBean(String url, ClientTransport clientTransport) {
		super();
		this.url = url;
		this.defaultClientTransport = clientTransport;
	}

	public BayeuxClientFactoryBean(String url, ClientTransport defaultTransport, ClientTransport... transports) {
		super();
		this.url = url;
		this.defaultClientTransport = defaultTransport;
		this.transports = transports;
	}

	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	public void setHandshakeTimeout(int handshakeTimeout) {
		this.handshakeTimeout = handshakeTimeout;
	}

	public boolean isRunning() {
		return this.running;
	}

	public void start() {
		synchronized (this.lifecycleMonitor) {
			if (this.running) {
				return;
			}
			try {
				this.bayeuxClient.getChannel(Channel.META_HANDSHAKE).addListener(new ClientSessionChannel.MessageListener()
				{
				    public void onMessage(ClientSessionChannel channel, Message message)
				    {
				        if (message.isSuccessful())
				        {
				        	logger.debug("handshake was successful");
				        }
				    }
				});
				logger.debug("Starting handshake");
				this.bayeuxClient.handshake();
		        logger.debug("Waiting for handshake completion");
		        boolean handshaken = this.bayeuxClient.waitFor(this.handshakeTimeout, BayeuxClient.State.CONNECTED);
		        if (handshaken)
		        {
		        	this.running = true;
		        } else {
		        	this.running = false;
		        	throw new BeanInitializationException("Client did not handshake with server: " + this.url);
		        }
			}
			catch (Exception e) {
				throw new BeanInitializationException("failed to connect to " + this.url, e);
			}
		}

	}

	public void stop() {
		synchronized (this.lifecycleMonitor) {
			if (this.isRunning()) {
				this.bayeuxClient.disconnect();
				this.bayeuxClient.waitFor(this.handshakeTimeout, BayeuxClient.State.DISCONNECTED);
				this.running = false;
			}
		}
	}

	public int getPhase() {
		return this.phase;
	}

	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public void stop(Runnable callback) {
		this.stop();
		callback.run();
	}

	@Override
	protected BayeuxClient createInstance() throws Exception {
		if (this.transports != null) {
			this.bayeuxClient = new BayeuxClient(this.url, this.defaultClientTransport, this.transports);
		} else {
			this.bayeuxClient = new BayeuxClient(this.url, this.defaultClientTransport);
		}
		return this.bayeuxClient;
	}

	@Override
	public Class<? extends BayeuxClient> getObjectType() {
		return BayeuxClient.class;
	}
	
	
}
