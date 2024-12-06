package kimjang.toolkit.solsol.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IssuedTokens {
    private String accessToken;
    private String refreshToken;
}
