package kimjang.toolkit.solsol.user;

import kimjang.toolkit.solsol.domain.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UniqueEmailTest {
    @Autowired
    private UserService userService;

    @Test
    @DisplayName(value = "유저 이메일이 유니크한지 체크")
    public void Check_Unique_Email(){
        String email = "solsol@naver.com";
        boolean check = userService.checkUniqueEmail(email);

        Assertions.assertTrue(check);
    }
}
