package kimjang.toolkit.solsol.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqPastChatsDto {
    private Long roomId;
    private Pageable page;
    public ReqPastChatsDto (Long roomId, int page, int size){
        this.roomId = roomId;
        this.page = PageRequest.of(page, size);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("방 번호 : ").append(roomId)
                .append("\n 페이지 : ").append(page.getPageNumber()).append(" 페이지 사이즈 : ").append(page.getPageSize());
        return sb.toString();
    }
}
