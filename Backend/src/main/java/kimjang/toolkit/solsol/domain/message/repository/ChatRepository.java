package kimjang.toolkit.solsol.domain.message.repository;

import kimjang.toolkit.solsol.domain.message.ChatMessage;
import kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {


//    Slice<GetChatProj> findByChatRoom_Id(Long id, Pageable pageable);

    /**
     * 요청한 유저의 채팅 방 퇴장 시간
     * 이후에 발생한 모든 채팅 불러오기
     *
     * @param roomId
     * @param email
     * @param pageable : size(100)개 씩 채팅 전달
     * @return
     * @Param roomExitTime : 사용자가 최근 방에서 나간 시간
     */
    @Query(value="SELECT " +
            " new kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto(" +
            " :roomId, cm.content, cm.createDate, u.id, u.email, u.name )" + // 채팅 내용
            " FROM ChatMessage cm " + // 채팅
            " join User u ON u.id = cm.user.id " + // 유저
            " WHERE cm.createDate > (" +
            " SELECT " +
            "   MAX(CASE WHEN ccr.roomExitTime IS NULL " +
            "         THEN '0001-01-01 00:00:00' " +
            "         ELSE ccr.roomExitTime " +
            "     END) " +
            "FROM ChatRoomCustomerRelationship ccr " +
            " WHERE ccr.user.email = :email AND cm.chatRoom.id = :roomId )" + // 사용자가 채팅 방을 나간 시간 이후에 생성된 채팅들
            " ORDER BY cm.createDate ASC ") // 오래된 순서로 채팅 정렬
    Slice<SendChatMessageDto> findRecentChats(@Param("roomId") Long roomId, @Param("email") String email, Pageable pageable);

    // 유저가 속한 채팅방에 가장 최근 채팅만 쿼리
    @Query(value="SELECT " +
            " new kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto(" +
            " :roomId, cm.content, cm.createDate, u.id, u.email, u.name )" + // 채팅 내용
            " FROM ChatMessage cm " + // 채팅
            " join User u ON u.id = cm.user.id " + // 유저
            " WHERE cm.createDate <= (" +
            " SELECT " +
            "   MAX(CASE WHEN ccr.roomExitTime IS NULL " +
            "         THEN '0001-01-01 00:00:00' " +
            "         ELSE ccr.roomExitTime " +
            "     END) " +
            " FROM ChatRoomCustomerRelationship ccr " +
            " WHERE ccr.user.email = :email AND cm.chatRoom.id = :roomId )" + // 사용자가 채팅 방을 나간 시간 이전에 생성된 채팅들
            " ORDER BY cm.createDate ASC ") // 최신 순서로 채팅 정렬
    Slice<SendChatMessageDto> findPastChats(@Param("roomId") Long roomId, @Param("email") String email, Pageable pageable);

    List<ChatMessage> findByChatRoom_Id(Long roomId);
}
