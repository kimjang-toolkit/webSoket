package kimjang.toolkit.solsol.domain.room.dto;

import kimjang.toolkit.solsol.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRoomDto {
    private Long roomId;
    private UserDto leaveUser;
}
