package kimjang.toolkit.solsol.domain.room.service;

import kimjang.toolkit.solsol.domain.room.entity.ChatRoomCustomerRelationship;
import kimjang.toolkit.solsol.domain.room.repository.ChatRoomCustormerRelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomSubscribeService {
    private final ChatRoomCustormerRelationshipRepository relationshipRepository;

    @Transactional
    public void updateUnsubscribeTime(String email, Long roomId){
        ChatRoomCustomerRelationship relationship = relationshipRepository.findByChatRoom_IdAndUser_Email(roomId, email)
                .orElseThrow(() -> new IllegalStateException("구독하고 있지 않는 채팅방입니다."));
        relationship.unsubscribeRoom();
    }
}
