package kimjang.toolkit.solsol.chat;


import kimjang.toolkit.solsol.SolsolApplication;
import kimjang.toolkit.solsol.domain.user.dto.UserDto;
import kimjang.toolkit.solsol.domain.message.ChatMessage;
import kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto;
import kimjang.toolkit.solsol.domain.message.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@SpringBootTest(classes = SolsolApplication.class)
@ActiveProfiles(value = "dev")
@Transactional
public class CreateChatTest {
    @Autowired
    ChatService chatService;


    @Test
    @DisplayName(value = "채팅 생성 테스트!")
    public void Send_Message_CreateChat(){
        SendChatMessageDto testMessage = SendChatMessageDto.builder().roomId(102L)
                .content("호식이 두마리 치킨 크크크 치킨은 회애!")
                .createDate(LocalDateTime.of(2023,12,12,20,0))
                .sender(new UserDto(1L, "solsol@naver.com", "solsol"))
                .build();

        // 보낸 메세지 저장
        ChatMessage chatMessage = chatService.saveChat(testMessage);

        Assertions.assertEquals(testMessage.getContent(), chatMessage.getContent());
    }


}
