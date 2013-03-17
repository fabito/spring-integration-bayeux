package org.springframework.integration.bayeux.outbound;

import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.springframework.integration.Message;
import org.springframework.integration.bayeux.core.AbstractBayeuxClientAwareMessageHandler;
import org.springframework.util.Assert;

public class SendingMessageHandler extends
		AbstractBayeuxClientAwareMessageHandler {

	private String channelName;

	public SendingMessageHandler() {
		super();
	}

	public SendingMessageHandler(BayeuxClient bayeuxClient, String bayeuxChannel) {
		super(bayeuxClient);
		this.channelName = bayeuxChannel;
	}

	@Override
	protected void handleMessageInternal(Message<?> message) throws Exception {
		Assert.isTrue(this.initialized, this.getComponentName()
				+ " must be initialized");
		Object payload = message.getPayload();

		// if (this.bayeuxClient.isDisconnected()) {
		// logger.warn("'bayeuxClient' is disconnected. Trying new handshake before sending message.");
		// this.bayeuxClient.handshake();
		// }

		this.bayeuxClient.getChannel(this.channelName).publish(payload,
				new LoggingMessageListenerCallback());
	}

	private class LoggingMessageListenerCallback implements MessageListener {
		public void onMessage(ClientSessionChannel channel,
				org.cometd.bayeux.Message message) {
			if (message.isSuccessful()) {
				logger.debug("Message sent successfully.");
			} else {
				logger.warn("ERROR SENDING MESSAGE !!!!!");
			}
		}
	}
}