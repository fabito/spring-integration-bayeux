package org.springframework.integration.bayeux.inbound;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.integration.MessagingException;
import org.springframework.integration.bayeux.core.BayeuxContextUtils;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.PollableChannel;
import org.springframework.integration.message.ErrorMessage;
import org.springframework.integration.test.util.TestUtils;

@RunWith(MockitoJUnitRunner.class)
public class MessageListeningEndpointTests {

	@Mock 
	private BayeuxClient bayeuxClient;
	
	@Mock 
	private ClientSessionChannel clientSessionChannel;
	
	/**
	 * Should add/remove MessageListener when endpoint started/stopped
	 */
	@Test
	public void testLifecycle(){
		
		final Set<MessageListener> packetListSet = new HashSet<MessageListener>();
		
		String channelName = "/testChannel";
		
		MessageListeningEndpoint endpoint = new MessageListeningEndpoint(bayeuxClient, channelName);

		when(bayeuxClient.getChannel(channelName)).thenReturn(clientSessionChannel);
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				packetListSet.add((MessageListener) invocation.getArguments()[0]);
				return null;
			}
		}).when(clientSessionChannel).subscribe(Mockito.any(MessageListener.class));

		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				packetListSet.remove(invocation.getArguments()[0]);
				return null;
			}
		}).when(clientSessionChannel).unsubscribe(Mockito.any(MessageListener.class));

		assertEquals(0, packetListSet.size());
		endpoint.setOutputChannel(new QueueChannel());
		endpoint.afterPropertiesSet();
		
		endpoint.start();
		assertEquals(1, packetListSet.size());
		endpoint.stop();
		
		assertEquals(0, packetListSet.size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonInitializationFailure(){
		MessageListeningEndpoint endpoint = new MessageListeningEndpoint(bayeuxClient, "");
		endpoint.start();
	}
	
	@Test
	public void testWithImplicitBayeuxClient(){
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		bf.registerSingleton(BayeuxContextUtils.BAYEUX_CLIENT_BEAN_NAME, bayeuxClient);
		MessageListeningEndpoint endpoint = new MessageListeningEndpoint();
		endpoint.setBeanFactory(bf);
		endpoint.setOutputChannel(new QueueChannel());
		endpoint.afterPropertiesSet();
		assertNotNull(TestUtils.getPropertyValue(endpoint,"bayeuxClient"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNoBayeuxClient(){
		MessageListeningEndpoint endpoint = new MessageListeningEndpoint();
		endpoint.afterPropertiesSet();
	}
	
	@Test
	public void testWithErrorChannel(){
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		bf.registerSingleton(BayeuxContextUtils.BAYEUX_CLIENT_BEAN_NAME, bayeuxClient);
		
		MessageListeningEndpoint endpoint = new MessageListeningEndpoint();

		DirectChannel outChannel = new DirectChannel();
		outChannel.subscribe(new MessageHandler() {		
			public void handleMessage(org.springframework.integration.Message<?> message)
					throws MessagingException {
				throw new RuntimeException("ooops");
			}
		});
		PollableChannel errorChannel = new QueueChannel();
		endpoint.setBeanFactory(bf);
		endpoint.setOutputChannel(outChannel);
		endpoint.setErrorChannel(errorChannel);
		endpoint.afterPropertiesSet();

		MessageListener listener = (MessageListener) TestUtils.getPropertyValue(endpoint, "messageListener");
		
		Message message = mock(Message.class);
		when(message.toString()).thenReturn("hello");
		listener.onMessage(clientSessionChannel, message);

		ErrorMessage msg =  
			(ErrorMessage) errorChannel.receive();
		assertEquals("hello", ((MessagingException)msg.getPayload()).getFailedMessage().getPayload());
	}
	
}