package kimjang.toolkit.solsol.chat;

import kimjang.toolkit.solsol.domain.message.dto.PastChatsDto;
import kimjang.toolkit.solsol.domain.message.dto.ReqPastChatsDto;
import kimjang.toolkit.solsol.domain.message.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class LoadPastChatsTest {
    private static final String userEmail = "hyo@naver.com";
    private static final String authorities = "ROLE_ADMIN,ROLE_USER";

    private final ChatService chatService;

    @Autowired
    public LoadPastChatsTest(ChatService chatService) {
        this.chatService = chatService;
    }

    @BeforeAll
    public static void setAuthentication(){
        Authentication auth = new UsernamePasswordAuthenticationToken(userEmail, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void loadRecentChats_test(){
        ReqPastChatsDto reqPastChatsDto = new ReqPastChatsDto(1L, 0, 100);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        log.info("email : "+email);
        PastChatsDto pastChatsDto = chatService.getPastChats(reqPastChatsDto, "recent");

        pastChatsDto.getPastChats().stream().forEach(chat -> {
            log.info(chat.getContent());
        });
    }

    @Test
    public void loadPastChats_test(){
        ReqPastChatsDto reqPastChatsDto = new ReqPastChatsDto(1L, 0, 100);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        log.info("email : "+email);
        PastChatsDto pastChatsDto = chatService.getPastChats(reqPastChatsDto, "past");

        pastChatsDto.getPastChats().stream().forEach(chat -> {
            log.info(chat.getContent());
        });
    }

}
