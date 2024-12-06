package kimjang.toolkit.solsol.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LastChatDto {
    private LocalDateTime lastChatTime;
    private String lastContent;
}
