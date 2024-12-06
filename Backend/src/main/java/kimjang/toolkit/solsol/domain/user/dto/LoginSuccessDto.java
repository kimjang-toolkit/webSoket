package kimjang.toolkit.solsol.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessDto {

    private Long id;
    private String email;

    @Pattern(regexp = "^[/\\,'₩`{}|\"#\\$%^&*()]+$", message = "Name contains invalid characters.")
    @Size(max = 10, message = "Name must be less than 10 characters.")
    private String name;

    private String imgUrl;

    private String accessToken;
    private String refreshToken;
}
