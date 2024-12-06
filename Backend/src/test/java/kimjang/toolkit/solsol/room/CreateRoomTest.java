package kimjang.toolkit.solsol.room;

import kimjang.toolkit.solsol.domain.room.dto.InviteChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.CreateChatRoomDto;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomCustormerRelationshipRepository;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomRepository;
import kimjang.toolkit.solsol.domain.room.service.ChatRoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class CreateRoomTest {
    @Autowired
    ChatRoomService chatRoomService;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatRoomCustormerRelationshipRepository relationshipRepository;
    @Test
    @DisplayName("채팅방 생성 및 관계 생성 테스트")
    public void createRoomTest(){

        Authentication auth = new UsernamePasswordAuthenticationToken("solsol@naver.com", null,
                List.of(new SimpleGrantedAuthority("USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        CreateChatRoomDto createChatRoomDto = new CreateChatRoomDto(
                Arrays.asList(1L,2L,3L), "새로운 채팅방 생성 테스트");
        InviteChatRoomDto inviteChatRoomDto = chatRoomService.createChatRoom(createChatRoomDto);
        Assertions.assertEquals(inviteChatRoomDto.getRoomName(), createChatRoomDto.getRoomName());
    }

}
