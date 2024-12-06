package kimjang.toolkit.solsol.domain.message;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {
	@MessageMapping("/hello") // "/hello"로 메세지가 오면 greeting() 메서드 실행
	@SendTo("/topic/greetings") // Greeting 응답 객체는 "/topic/greetings"의 모든 구독자에게 전달된다.
	public Greeting greeting(HelloMessage message) throws Exception { // HelloMessage 객체가 인자로 들어가게된다.
//		Thread.sleep(1000); // simulated delay
		// To Do : 챗 메세지 채팅 방에 저장하기
		//
		System.out.println("message : "+message.getChat());
//		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
		return new Greeting(message.getChat());
	}

//	@GetMapping("/")
//	public String
}