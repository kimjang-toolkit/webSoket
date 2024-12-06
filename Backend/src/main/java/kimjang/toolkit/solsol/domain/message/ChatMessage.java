package kimjang.toolkit.solsol.domain.message;

import jakarta.persistence.*;
import kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto;
import kimjang.toolkit.solsol.domain.room.entity.ChatRoom;
import kimjang.toolkit.solsol.domain.user.entities.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 엔티티는 불변해야하니까 final로 설정
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id") // 컬럼의 이름을 바꿀 수 있다.
    private Long id;
    @JoinColumn(name = "chat_room")
    @ManyToOne
    private ChatRoom chatRoom; // 어디서 보냈는지
    private String content; // 뭘 보냈는지
    @Column(name = "create_date")
    private LocalDateTime createDate; // 언제 보냈는지
    @ManyToOne
    private User user; // 누가 보냈는지

    public static ChatMessage toEntity(SendChatMessageDto dto, ChatRoom chatRoom, User user){
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(dto.getContent())
                .createDate(dto.getCreateDate())
                .build();
    }

    public static ChatMessage toEntity(String content, ChatRoom chatRoom, User user){
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .user(user)
                .content(content)
                .createDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
