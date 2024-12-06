package kimjang.toolkit.solsol.chat;


import kimjang.toolkit.solsol.domain.message.dto.PastChatsDto;
import kimjang.toolkit.solsol.domain.message.dto.ReqPastChatsDto;
import kimjang.toolkit.solsol.domain.message.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class GetPastChatsTest {
    @Autowired
    private ChatService chatService;

    @Test
    @DisplayName(value = "요청에 따라 채팅 메세지 불러오기")
    public void GET_Pase_Chat(){
        ReqPastChatsDto req = ReqPastChatsDto.builder()
                .roomId(1L)
                .page(PageRequest.of(0, 100))
                .build();
        String timeLine = "recent";
        PastChatsDto responseDto = chatService.getPastChats(req, timeLine);
        System.out.println("메세지 개수 : "+responseDto);
        for(int i=0; i<responseDto.getPastChats().size(); i++){
            System.out.println("메세지 : "+responseDto.getPastChats().get(i));
        }
    }
}
