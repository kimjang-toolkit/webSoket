package kimjang.toolkit.solsol.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import kimjang.toolkit.solsol.domain.user.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendChatMessageDto implements Comparable<SendChatMessageDto>{
    private Long roomId; // 어디서 보냈는지
    private String content; // 뭘 보냈는지
    @JsonFormat(shape = JsonFormat.Shape.STRING, // JSON <-> String 파싱
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDateTime createDate = LocalDateTime.now(); // 언제 보냈는지
    private UserDto sender; // 누가 보냈는지
    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCreateDate = createDate.format(formatter);
        StringBuilder sb = new StringBuilder();
        sb.append("누가 : ").append(sender.getName()).append("\n")
                .append("언제 : ").append(formattedCreateDate).append("\n")
                .append("어디서 : ").append(roomId).append("\n")
                .append("내용 : ").append(content).append("\n");
        return sb.toString();
    }

    @Override
    public int compareTo(SendChatMessageDto o) {
        // 최근순
        if (createDate.isAfter(o.getCreateDate())) {
            return 1;
        }
        return -1;
    }

    public SendChatMessageDto (Long roomId, String content, LocalDateTime createDate, Long senderId, String senderEmail, String senderName){
        this.roomId = roomId;
        this.createDate = createDate;
        this.content = content;
        this.sender = new UserDto(senderId, senderEmail, senderName);
    }
}
