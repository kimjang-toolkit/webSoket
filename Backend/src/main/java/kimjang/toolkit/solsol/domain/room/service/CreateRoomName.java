package kimjang.toolkit.solsol.domain.room.service;

import kimjang.toolkit.solsol.domain.room.dto.CreateChatRoomDto;

public class CreateRoomName {

    public static String withParticipationsName(CreateChatRoomDto createChatRoomDto, String creator, Long customerId){
//        List<Long> participants = createChatRoomDto.getParticipants();
//        // 3명 이상일 경우 본인을 제외한 나머지 사람들의 이름 나열, 단 10글자 이후론 ...으로 처리
//        if(participants.size() >= 3 && createChatRoomDto.getRoomName() != null && !createChatRoomDto.getRoomName().isEmpty()){
//            // 채팅방 만든 사람이 방 이름을 설정할 수 있다.
//            return createChatRoomDto.getRoomName();
//        } else{ // 상대 이름
//            if(participants.size() >= 2){
//                StringBuilder sb = new StringBuilder();
//                for(Long c : participants){
//                    if(!c.getId().equals(customerId)){
//                        sb.append(c.getName()).append(" ");
//                    }
//                    if(sb.length() > 10){
//                        sb = new StringBuilder(sb.substring(0, 10));
//                        sb.append("...");
//                        return sb.toString();
//                    }
//                }
//                sb = new StringBuilder(sb.substring(0, sb.length()-1));
//                return sb.toString();
//            }
//            else{ // 나만 있는 방일 경우
//                return creator;
//            }
//        }
        return createChatRoomDto.getRoomName();

    }
}
