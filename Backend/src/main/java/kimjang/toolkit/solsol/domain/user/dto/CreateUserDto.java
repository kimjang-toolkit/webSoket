package kimjang.toolkit.solsol.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    private String name;
    private String email;
    private String imgUrl;
    private String mobileNumber;

    // json -> 객체로 시리얼라이제이션은 가능하지만 그 반대로는 불가능하게 해서 외부로 DB의 비밀번호가 유출되지 않도록 함.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;
}
