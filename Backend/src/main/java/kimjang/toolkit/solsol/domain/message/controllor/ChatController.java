package kimjang.toolkit.solsol.domain.message.controllor;

import kimjang.toolkit.solsol.domain.message.dto.ReqPastChatsDto;
import kimjang.toolkit.solsol.domain.message.service.ChatService;
import kimjang.toolkit.solsol.domain.message.dto.PastChatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * 채팅방 나간 시간 이전, 이후에 생성된 채팅을 불러오는 API
     * timeLine이 past라면 채팅방 나간 시간 이전에 생성된 채팅
     * timeLine이 recent라면 채팅방 나간 시간 이후에 생성된 채팅
     *
     * 이미 유저와 채팅방 연관 관계에 최근 나간 시간을 저장하기 때문에 프론트에게 받을 필요가 없다.
     *
     * @param roomId
     * @param page
     * @param size
     * @param timeLine
     * @return
     */
    @GetMapping("/chat-room/chat/{roomId}")
    public ResponseEntity<PastChatsDto> getChatLogs(@PathVariable Long roomId,
//                                                    @RequestParam(defaultValue = "0001-01-01 00:00:00") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime roomExitTime,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "100") Integer size,
                                                    @RequestParam(defaultValue = "recent") String timeLine){
        ReqPastChatsDto reqPastChatsDto = new ReqPastChatsDto(roomId, page, size);
        PastChatsDto pastChatsDto = chatService.getPastChats(reqPastChatsDto, timeLine);
        return ResponseEntity.ok(pastChatsDto);
    }

}
