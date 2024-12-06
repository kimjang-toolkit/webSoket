package kimjang.toolkit.solsol.domain.room.dto;

import kimjang.toolkit.solsol.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteChatRoomDto {
    private List<UserDto> participants;
    private String roomName;
    private Long roomId;
    private String creator;
    @Override
    public String toString() {
        return "InviteChatRoomDto{" +
                "participants=" + participants +
                ", roomName='" + roomName + '\'' +
                ", roomId=" + roomId +
                ", creator='" + creator + '\'' +
                '}';
    }
}
