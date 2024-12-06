package kimjang.toolkit.solsol.domain.room.service;

import kimjang.toolkit.solsol.domain.room.dto.ChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.InviteChatRoomDto;
import kimjang.toolkit.solsol.domain.room.entity.ChatRoomCustomerRelationship;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomRepository;
import kimjang.toolkit.solsol.domain.user.entities.User;
import kimjang.toolkit.solsol.domain.room.dto.LeaveRoomDto;
import kimjang.toolkit.solsol.domain.user.reposiotry.UserRepository;
import kimjang.toolkit.solsol.domain.user.dto.UserDto;
import kimjang.toolkit.solsol.domain.message.ChatMessage;
import kimjang.toolkit.solsol.domain.message.repository.ChatRepository;
import kimjang.toolkit.solsol.domain.room.entity.ChatRoom;
import kimjang.toolkit.solsol.domain.room.dto.CreateChatRoomDto;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomCustormerRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {


    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomCustormerRelationshipRepository relationshipRepository;



    @Transactional
    public InviteChatRoomDto createChatRoom(CreateChatRoomDto createChatRoomDto) {
        String creator = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = fetchCustomers(createChatRoomDto.getParticipants(), creator); // 채팅방 참여자들 불러오기
        List<UserDto> userDtos = toDtoParticipants(users);

        ChatRoom createdRoom = createChatRoom(users.size());
        saveRelationships(createdRoom, users, createChatRoomDto, creator);
        return InviteChatRoomDto.builder()
                .roomId(createdRoom.getId())
                .creator(creator)
                .participants(userDtos)
                .roomName(createChatRoomDto.getRoomName())
                .build();
    }

    @Transactional
    public void saveFirstChat(String content, User maker, ChatRoom createdRoom) {

        chatRepository.save(ChatMessage.builder()
                        .createDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .chatRoom(createdRoom)
                        .user(maker)
                        .content(content)
                .build());
    }

    @Transactional
    public ChatRoom createChatRoom(int memberCnt) {
        try {
            return chatRoomRepository.save(ChatRoom.of(memberCnt));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("채팅방 생성하지 못했습니다.");
        }
    }

    @Transactional
    public void saveRelationships(ChatRoom chatRoom, List<User> users, CreateChatRoomDto createChatRoomDto, String creator) {
        List<ChatRoomCustomerRelationship> relationships = users.stream()
                .map(customer -> ChatRoomCustomerRelationship.builder()
                        .chatRoom(chatRoom)
                        .user(customer)
                        .roomName(createChatRoomDto.getRoomName())
                        .build())
                .toList();
        try{
            relationshipRepository.saveAll(relationships);
        } catch (RuntimeException e){
            log.error(e.getMessage());
            throw new RuntimeException("사용자와 채팅방 사이 관계를 생성하지 못했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<User> fetchCustomers(List<Long> participants, String creatorEmail) {
//        List<Long> customerIds = participants.stream()
//                .map(UserDto::getId)
//                .toList();
        User creator = userRepository.findByEmail(creatorEmail).get();
        List<User> users = userRepository.findByIdIn(participants);
        if (users.size() != participants.size()) {
            throw new RuntimeException("존재하지 않는 유저에게 채팅방을 초대했습니다.");
        }
        users.add(creator);
        return users;
    }
    private List<UserDto> toDtoParticipants(List<User> users) {
        return users.stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    public List<ChatRoomDto> getChatRooms(Long userId) {
        return chatRoomRepository.findChatRoomsByUserId(userId);
//        List<LastChatDto> lastChatDtos = chatRepository.findLastChatsByUserId(userId);
    }

    /**
     * 자신의 JWT 인증 토큰으로 다른 사람이 채팅방을 나가게 하지 못하도록 막기
     * @param leaveRoomDto
     * @return
     */
    @Transactional
    @PreAuthorize("# leaveRoomDto.leaveUser.email == authentication.principal.username")
    public LeaveRoomDto leaveRoom(LeaveRoomDto leaveRoomDto) {
        System.out.println(leaveRoomDto.getLeaveUser().getEmail()+"은 인증된 사람이다!!");
        LeaveRoomDto dto = null;
        ChatRoom chatRoom = chatRoomRepository.findById(leaveRoomDto.getRoomId()).orElseThrow(() -> {
            throw new IllegalStateException("나가고자 하는 방이 없어요. 고객님이 들어간 방을 찾아주세요.");
        });
        // room에 memberCnt 1개 줄이기
        chatRoom.leaveUser();
        // user에 맞는 ChatRoomCustomerRelationship delete 하기
        relationshipRepository.deleteByChatRoom_IdAndUser_Email(leaveRoomDto.getRoomId(), leaveRoomDto.getLeaveUser().getEmail());
        return dto;
    }
}
