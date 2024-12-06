package kimjang.toolkit.solsol.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomDto {
    private List<Long> participants;
    private String roomName;
//    private String firstChat;
//    private String creator;
}
