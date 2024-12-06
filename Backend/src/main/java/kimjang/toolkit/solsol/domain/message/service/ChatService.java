package kimjang.toolkit.solsol.domain.message.service;

import kimjang.toolkit.solsol.domain.message.dto.ReqPastChatsDto;
import kimjang.toolkit.solsol.domain.message.repository.ChatRepository;
import kimjang.toolkit.solsol.domain.room.entity.ChatRoom;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomRepository;
import kimjang.toolkit.solsol.domain.user.entities.User;
import kimjang.toolkit.solsol.domain.user.reposiotry.UserRepository;
import kimjang.toolkit.solsol.domain.message.ChatMessage;
import kimjang.toolkit.solsol.domain.message.dto.PastChatsDto;
import kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatMessage saveChat(SendChatMessageDto message) {
        User user = userRepository.findById(message.getSender().getId())
                .orElseThrow();
        ChatRoom room = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow();
        ChatMessage chatMessage = ChatMessage.toEntity(message, room, user);
        return chatRepository.save(chatMessage);
    }

    @Transactional(readOnly = true)
    public PastChatsDto getPastChats(ReqPastChatsDto reqPastChatsDto, String timeLine) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        log.info(reqPastChatsDto.toString()+" email : "+email);
        if(timeLine.equals("recent")){
            Slice<SendChatMessageDto> pastChats =
                    chatRepository.findRecentChats(reqPastChatsDto.getRoomId(),
                            email,
                            reqPastChatsDto.getPage());
            return PastChatsDto.of(reqPastChatsDto, pastChats);
        } else{
            Slice<SendChatMessageDto> pastChats =
                    chatRepository.findPastChats(reqPastChatsDto.getRoomId(),
                            email,
                            reqPastChatsDto.getPage());
            return PastChatsDto.of(reqPastChatsDto, pastChats);
        }
    }
}
