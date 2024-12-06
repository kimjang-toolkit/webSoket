package kimjang.toolkit.solsol.domain.room;

import kimjang.toolkit.solsol.domain.room.dto.ChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.CreateChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.InviteChatRoomDto;
import kimjang.toolkit.solsol.domain.room.dto.LeaveRoomDto;
import kimjang.toolkit.solsol.domain.message.dto.SendChatMessageDto;
import kimjang.toolkit.solsol.domain.room.service.ChatRoomService;
import kimjang.toolkit.solsol.domain.room.service.ChatRoomStompService;
import kimjang.toolkit.solsol.domain.message.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatRoomController {

   private final ChatRoomService chatRoomService;
   private final ChatRoomStompService chatRoomStompService;
   private final ChatService chatService;

   @MessageMapping("/chat/{roomId}") // /pub/chat 로 SendMessageDto를 전송
   @SendTo("/sub/chat/{roomId}") // /sub/chat 을 구독하면 SendMessageDto를 받음
   public SendChatMessageDto sendChatMessage(@DestinationVariable String roomId, @Payload SendChatMessageDto message){
      log.info("방 번호 : "+roomId);
      // 채팅 방에 채팅 저장하기
      chatService.saveChat(message);
      // 저장한 채팅 분배하기
       return message;
   }

   /**
    * Create Chat room
    * and notify to Users that they are invited to chat room
    * @param dto
    * @return
    */
   @PostMapping("/chat-room")
   public ResponseEntity<InviteChatRoomDto> createChatRoom(@RequestBody CreateChatRoomDto dto){
      try{
         InviteChatRoomDto inviteChatRoomDto = chatRoomService.createChatRoom(dto);
         // /notification/room/{userid}를 구독하면 SendMessageDto를 받음
         chatRoomStompService.inviteParticipates(inviteChatRoomDto);
         return ResponseEntity.ok(inviteChatRoomDto);
      }
      catch(RuntimeException e){
         log.error(e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   @GetMapping("/chat-room")
   public ResponseEntity<List<ChatRoomDto>> getChatRoomList(@RequestParam("userId") Long userId){
      try{
         List<ChatRoomDto> rooms = chatRoomService.getChatRooms(userId);
         return ResponseEntity.ok(rooms);
      } catch(RuntimeException e){
         log.error("Error creating chat room", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   @PutMapping("/chat-room/leave")
   public ResponseEntity<LeaveRoomDto> leaveRoom(@RequestBody LeaveRoomDto leaveRoomDto){
      try{
         LeaveRoomDto result = chatRoomService.leaveRoom(leaveRoomDto);
         return ResponseEntity.ok(result);
      } catch (IllegalArgumentException e){
         log.error("Error ");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      } catch(RuntimeException e){
         log.error("Error creating chat room", e);
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }
}
