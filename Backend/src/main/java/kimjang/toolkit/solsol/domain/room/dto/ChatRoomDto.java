package kimjang.toolkit.solsol.domain.room.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long roomId;
    private String roomName;
    private Integer memberCnt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, // JSON <-> String 파싱
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private LocalDateTime lastChatTime;
    private String lastContent;
    private Long unreadCnt;
}
