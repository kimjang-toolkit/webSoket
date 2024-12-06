package kimjang.toolkit.solsol.domain.room.repository;

import kimjang.toolkit.solsol.domain.room.entity.ChatRoomCustomerRelationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomCustormerRelationshipRepository extends JpaRepository<ChatRoomCustomerRelationship, Long> {
    List<ChatRoomCustomerRelationship> findByChatRoom_Id(Long roomId);
    void deleteByChatRoom_IdAndUser_Email(Long roomId, String email);

    Optional<ChatRoomCustomerRelationship> findByChatRoom_IdAndUser_Email(Long roomId, String email);
}
