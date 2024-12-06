package kimjang.toolkit.solsol.domain.room.service;

import kimjang.toolkit.solsol.domain.room.dto.InviteChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.CreateRoomReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomStompService {

    private final SimpMessagingTemplate messagingTemplate;

    public void inviteParticipates(InviteChatRoomDto inviteChatRoomDto) {
        log.info(inviteChatRoomDto.toString());
        List<CompletableFuture<Void>> futures = inviteChatRoomDto.getParticipants()
                .stream()
                .map(customerDto -> CompletableFuture.runAsync(() -> {
                    CreateRoomReqDto createRoomReqDto = CreateRoomReqDto.builder()
                            .roomId(inviteChatRoomDto.getRoomId())
                            .roomName(inviteChatRoomDto.getRoomName())
                            .user(customerDto)
                            .creator(inviteChatRoomDto.getCreator())
                            .build();
                    messagingTemplate.convertAndSend("/notification/room/" + customerDto.getId(), createRoomReqDto);
                }))
                .toList();

        // 모든 병렬 작업이 완료될 때까지 기다림
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
