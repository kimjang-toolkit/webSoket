package kimjang.toolkit.solsol.domain.user.controller;

import kimjang.toolkit.solsol.domain.user.dto.*;
import kimjang.toolkit.solsol.domain.user.service.FriendService;
import kimjang.toolkit.solsol.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody CreateUserDto createUserDto) {
        UserDto userDto = userService.registerUser(createUserDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/register/unique")
    public ResponseEntity<Boolean> checkUniqueEmail(@RequestParam("email") String email){
        try{
            boolean check = userService.checkUniqueEmail(email);
            return ResponseEntity.ok(check);
        } catch (RuntimeException e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/friends")
    public List<UserProfileDto> getUserFriends(Authentication authentication){
        return friendService.getFriends(authentication.getName());
    }

    @PostMapping("/user/friends")
    public String addFriend(Authentication authentication, @RequestBody AddFriendsDto addFriendsDto){
        friendService.addFriend(authentication.getName(), addFriendsDto);
        return "성공적으로 친구추가 했습니다.";
    }

    @DeleteMapping("/user/friends")
    public String deleteFriends(Authentication authentication, @RequestBody AddFriendsDto addFriendsDto){
        friendService.removeFriend(authentication.getName(), addFriendsDto);
        return "성공적으로 친구를 제거했습니다.";
    }

    @PostMapping("/login")
    public LoginSuccessDto getUserDetailsAfterLogin(@RequestBody LoginDto loginDto) {
        return userService.userLoginAndSaveRefreshToken(loginDto);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<UserProfileDto> getUserDetail(){
        UserProfileDto dto = userService.getUserDetail();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refresh-token")
    public IssuedTokens getAccessTokenByRefreshToken(@RequestBody RefreshDto refreshDto){
        return userService.getAccessToken(refreshDto);
    }
}
