package kimjang.toolkit.solsol.room;

import kimjang.toolkit.solsol.domain.room.dto.ChatRoomDto;
import kimjang.toolkit.solsol.domain.room.service.ChatRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ChatRoomListTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Test
    @DisplayName("특정 유저의 채팅방 리스트 불러오기")
    public void Get_User_Chat_Room_list(){
        List<ChatRoomDto> chatRoomDtoList = chatRoomService.getChatRooms(1L);
        chatRoomDtoList.stream().forEach(
                chatRoomDto -> {
                    System.out.println(chatRoomDto.getRoomName()+" "+chatRoomDto.getLastContent()+" "+chatRoomDto.getLastChatTime());
                }
        );
    }
}
