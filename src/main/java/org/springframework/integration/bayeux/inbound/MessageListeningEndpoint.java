package org.springframework.integration.bayeux.inbound;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.springframework.integration.bayeux.core.AbstractBayeuxClientAwareEndpoint;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

/**
 * This component logs in as a user and forwards any messages <em>to</em> that
 * user on to downstream components.
 * 
 * @author Fábio Franco Uechi
 * @since 2.2.2
 */
public class MessageListeningEndpoint extends AbstractBayeuxClientAwareEndpoint {

	private MessagePublishingMessageListener messageListener = new MessagePublishingMessageListener();
	private String channelName;

	public MessageListeningEndpoint() {
		super();
	}

	public MessageListeningEndpoint(BayeuxClient bayeuxClient,
			String channelName) {
		super(bayeuxClient);
		this.channelName = channelName;
	}

	@Override
	public String getComponentType() {
		return "bayeux:inbound-channel-adapter";
	}

	@Override
	protected void doStart() {
		Assert.isTrue(this.initialized,
				this.getComponentName() + " [" + this.getComponentType()
						+ "] must be initialized");
		this.bayeuxClient.getChannel(this.channelName).subscribe(messageListener);
	}

	@Override
	protected void doStop() {
		if (this.bayeuxClient != null) {
			this.bayeuxClient.getChannel(this.channelName).unsubscribe(
					messageListener);
		}
	}

	private class MessagePublishingMessageListener implements MessageListener {
		public void onMessage(ClientSessionChannel channel, Message message) {
			logger.debug(message.toString());
			MessageBuilder<?> messageBuilder = MessageBuilder
					.withPayload(message.toString());// .copyHeaders(mappedHeaders);
			sendMessage(messageBuilder.build());
		}
	}

}
