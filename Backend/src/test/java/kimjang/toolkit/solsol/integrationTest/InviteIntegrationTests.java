package kimjang.toolkit.solsol.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kimjang.toolkit.solsol.domain.room.ChatRoomController;
import kimjang.toolkit.solsol.domain.room.dto.CreateChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.CreateRoomReqDto;
import kimjang.toolkit.solsol.domain.room.dto.InviteChatRoomDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InviteIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ChatRoomController chatRoomController;

	private WebSocketStompClient stompClient;

	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

	@BeforeEach
	public void setup() {
		WebSocketClient webSocketClient = new StandardWebSocketClient();
		this.stompClient = new WebSocketStompClient(webSocketClient);

		// LocalDateTime을 필드로 가지는 Dto 같은 경우 테스트 시 아래처럼 객체 맵퍼를 설정해줘야한다.
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(objectMapper);

		this.stompClient.setMessageConverter(converter);
	}

	@Test
	public void getGreeting() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		CreateChatRoomDto createChatRoomDto = new CreateChatRoomDto(
				Arrays.asList(2L), "통합 테스트");

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				System.out.println("연결 시도!");
				session.subscribe("/notification/room/1", new StompFrameHandler() { // 2번 유저의 알림 구독
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return CreateRoomReqDto.class; // 응답 객체의 클래스
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						System.out.println("연결 완료! 응답 대기!"+headers);
						CreateRoomReqDto messageDto = (CreateRoomReqDto) payload;
						try {
							System.out.println("응답 : "+messageDto.getRoomName());
							assertEquals(createChatRoomDto.getRoomName(), messageDto.getRoomName());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					// 보낸 메세지 저장
					// 인증 객체 저장
					System.out.println("인증 객체 저장");
					Authentication auth = new UsernamePasswordAuthenticationToken("solsol@naver.com", null,
							List.of(new SimpleGrantedAuthority("USER")));
					SecurityContextHolder.getContext().setAuthentication(auth);
					System.out.println("채팅방 생성 시작!");
					ResponseEntity<InviteChatRoomDto> chatMessage = chatRoomController.createChatRoom(createChatRoomDto);
					System.out.println("채팅방 생성 완료! "+chatMessage.getBody().getRoomName());
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		this.stompClient.connectAsync("ws://localhost:{port}/gs", this.headers, handler, this.port);

		if (latch.await(10, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("Greeting not received");
		}

	}

	private static class TestSessionHandler extends StompSessionHandlerAdapter {

		private final AtomicReference<Throwable> failure;

		public TestSessionHandler(AtomicReference<Throwable> failure) {
			this.failure = failure;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			this.failure.set(new Exception(headers.toString()));
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			this.failure.set(ex);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			this.failure.set(ex);
		}
	}
}