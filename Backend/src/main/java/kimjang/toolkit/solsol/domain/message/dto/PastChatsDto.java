package kimjang.toolkit.solsol.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PastChatsDto {
    private Long roomId;
    private List<SendChatMessageDto> pastChats;
    private int page; // 현재 페이지 번호
    private int size; // 페이지에 채팅 개수
    private boolean hasNext;

    public static PastChatsDto of(ReqPastChatsDto reqPastChatsDto, Slice<SendChatMessageDto> pastChats){
        return PastChatsDto.builder()
                .roomId(reqPastChatsDto.getRoomId())
                .pastChats(pastChats.getContent())
                .page(reqPastChatsDto.getPage().getPageNumber())
                .size(reqPastChatsDto.getPage().getPageSize())
                .hasNext(pastChats.hasNext())
                .build();
    }
}
