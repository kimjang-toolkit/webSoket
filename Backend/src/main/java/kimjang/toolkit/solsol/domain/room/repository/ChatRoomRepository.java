package kimjang.toolkit.solsol.domain.room.repository;

import kimjang.toolkit.solsol.domain.room.dto.ChatRoomDto;
import kimjang.toolkit.solsol.domain.room.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {


    @Query("SELECT new kimjang.toolkit.solsol.domain.room.dto.ChatRoomDto( " +
            " cr.id, ccr.roomName, cr.memberCnt, ccr.roomExitTime, " +
            " (SELECT cm.content FROM ChatMessage cm WHERE cm.chatRoom.id = cr.id AND " + // 채팅 내용 가져오기
            "   cm.createDate =(SELECT MAX(ccm.createDate) FROM ChatMessage ccm WHERE ccm.chatRoom.id = cm.chatRoom.id)) " + // 채팅방에서 가장 최근 채팅 날짜
            ", " +
            " (SELECT COUNT(cm2.id) FROM ChatMessage cm2 " + // 읽지 않은 매세지 개수
                        // 채팅방 id가 같은 메세지만 가져옴, roomExitTime 이후에 생성된 메세지만 가져옴
            "   WHERE cm2.chatRoom.id = cr.id AND cm2.createDate > ccr.roomExitTime)" +
            ") " +
            "FROM ChatRoom cr " + // 채팅방
            "JOIN ChatRoomCustomerRelationship ccr " + // 채팅방-유저 연관 테이블
            // 유저가 속한 채팅방을이 기본 조건
            "   ON cr.id = ccr.chatRoom.id AND ccr.user.id = :userId ")
            // 최근 채팅을 가져오기 위해 join
    List<ChatRoomDto> findChatRoomsByUserId(@Param("userId") Long userId);
}
