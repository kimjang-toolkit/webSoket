package kimjang.toolkit.solsol.domain.message.dto;

import kimjang.toolkit.solsol.domain.user.dto.UserDto;

import java.time.LocalDateTime;

public interface GetChatProj {
    String getContent();
    LocalDateTime getCreateDate();
    Long getChatRoom();
    UserDto getCustomer();
    interface ChatRoom{
        Long getId();
    }
    interface Customer{
        Long getId();
        String getName();
    }
}
