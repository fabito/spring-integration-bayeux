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

	public MessageListeningEndpoint() {
		super();
	}

	public MessageListeningEndpoint(BayeuxClient bayeuxClient) {
		super(bayeuxClient);
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
		this.bayeuxClient.getChannel(getTopic()).subscribe(messageListener);
	}

	private String getTopic() {
		// TODO Auto-generated method stub
		return "/teste";
	}

	@Override
	protected void doStop() {
		if (this.bayeuxClient != null) {
			this.bayeuxClient.getChannel(getTopic()).unsubscribe(
					messageListener);
		}
	}

	private class MessagePublishingMessageListener implements MessageListener {

		public void onMessage(ClientSessionChannel channel, Message message) {
			// Object payload;
			// Map<String, ?> mappedHeaders;
			// try {
			// System.out.println("Received Message: "
			// + (new JSONObject(
			// new JSONTokener(message.getJSON())))
			// .toString(2));
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			// TODO decide what to put in the header and implement a
			// headermapper
			logger.debug(message.toString());
			MessageBuilder<?> messageBuilder = MessageBuilder
					.withPayload(message.toString());// .copyHeaders(mappedHeaders);
			sendMessage(messageBuilder.build());
		}
	}

}
