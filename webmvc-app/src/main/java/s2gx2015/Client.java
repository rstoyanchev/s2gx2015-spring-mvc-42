package s2gx2015;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;


public class Client {

	private static Logger logger = LoggerFactory.getLogger(Client.class);


	public static void main(String[] args) throws IOException {

		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.afterPropertiesSet();

		WebSocketClient webSocket = new StandardWebSocketClient();
		SockJsClient sockJs = new SockJsClient(createTransports(webSocket));
		WebSocketStompClient client = new WebSocketStompClient(sockJs);
		client.setMessageConverter(new MappingJackson2MessageConverter());
		client.setTaskScheduler(taskScheduler); // for heartbeat

		client.connect("http://localhost:8080/messaging", new SessionHandler());

		System.in.read();
	}

	private static List<Transport> createTransports(WebSocketClient webSocketClient) {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(webSocketClient));
		transports.add(new RestTemplateXhrTransport());
		return transports;
	}


	private static class SessionHandler extends StompSessionHandlerAdapter {

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			session.subscribe("/topic/interval", new SubscriptionHandler());
		}
	}

	private static class SubscriptionHandler implements StompFrameHandler {

		@Override
		public Type getPayloadType(StompHeaders headers) {
			return Map.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			logger.debug("Got: " + payload);
		}
	}

}
