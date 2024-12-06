package kimjang.toolkit.solsol.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kimjang.toolkit.solsol.config.cache.TokenHasher;
import kimjang.toolkit.solsol.config.jwt.SecurityConstants;
import kimjang.toolkit.solsol.domain.cache.RefreshTokenCacheService;
import kimjang.toolkit.solsol.domain.user.dto.AuthorityInfo;
import kimjang.toolkit.solsol.domain.user.dto.IssuedTokens;
import kimjang.toolkit.solsol.domain.user.dto.RefreshDto;
import kimjang.toolkit.solsol.domain.user.service.JwtIssuer;
import kimjang.toolkit.solsol.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootTest
@Transactional
public class ReIssueTokenTest {
    private final SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
    @Autowired
    private TokenHasher tokenHasher;

    @Autowired
    private RefreshTokenCacheService refreshTokenCacheService;

    @Autowired
    private JwtIssuer jwtIssuer;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    private IssuedTokens issuedTokens;
    private final String email = "solsol@gmail.com";
    private final AuthorityInfo authorityInfo1 = AuthorityInfo.builder()
            .email(email).authorities("ROLE_USER").build();
    private final AuthorityInfo authorityInfo2 = AuthorityInfo.builder()
            .email("email").authorities("ROLE_USER").build();

    @Test
    public void when_reissue_then_not_equal_made_time(){
        // 토큰 생성
        issuedTokens = jwtIssuer.getAccessTokenByAuthorityInfo(authorityInfo1);
        // 토큰 저장
        jwtIssuer.saveIssuedToken(email, issuedTokens.getRefreshToken());
        String time1 = getCreatedTime(issuedTokens.getRefreshToken());
        log.info("초기 refresh token 생성 시간 : {}",time1);
        // 처음 생성
        IssuedTokens newIssuedTokens = jwtIssuer.getAccessTokenByRefreshToken(new RefreshDto(issuedTokens.getRefreshToken()));
        String time2 = getCreatedTime(newIssuedTokens.getRefreshToken());
        log.info("다음 refresh token 생성 시간 : {}",time2);
        Assertions.assertNotEquals(time1, time2);
    }

    @Test
    public void when_reissue_then_not_match(){
        // 토큰 생성
        issuedTokens = jwtIssuer.getAccessTokenByAuthorityInfo(authorityInfo1);
        // 토큰 저장
        jwtIssuer.saveIssuedToken(email, issuedTokens.getRefreshToken());
        log.info("초기 토큰 : {}", issuedTokens.getRefreshToken());
        // 해싱된 refresh token 불러오기
        String savedRefreshToken = refreshTokenCacheService.searchToken(authorityInfo1.getEmail());

        String time1 = getCreatedTime(issuedTokens.getRefreshToken());
        log.info("초기 refresh token 생성 시간 : {}",time1);
        // 처음 생성
        IssuedTokens newIssuedTokens = jwtIssuer.getAccessTokenByRefreshToken(new RefreshDto(issuedTokens.getRefreshToken()));
        String time2 = getCreatedTime(newIssuedTokens.getRefreshToken());
        log.info("다음 토큰 : {}", newIssuedTokens.getRefreshToken());
        log.info("다음 refresh token 생성 시간 : {}",time2);

        Assertions.assertNotEquals(time1, time2);
        // 새로운 refresh token 가져오고, 해싱된 토큰 비교. 다른 값을 해싱했기 때문에 match하지 말아야한다.
        Assertions.assertTrue(!tokenHasher.matches(newIssuedTokens.getRefreshToken(), savedRefreshToken));
    }

    @Test
    public void when_string_then_not_match_sha_256(){
        // 토큰 생성
        String test1 = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTb2wtS2ltamFuZyIsInN1YiI6IkpXVCBUb2tlbiIsImVtYWlsIjoic29sc29sQGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiY3JlYXRlX2RhdGUiOiIyMDI0LTA4LTAyVDE3OjMxOjQxLjMzMzkwNCIsImlhdCI6MTcyMjU4NzUwMSwiZXhwIjoxNzIyNTg3NjIxfQ.RYVguGifzq4jQ4fCHfNGTGfFlMLn-VnmMy_BAKPLsEE";
        String test2 = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTb2wtS2ltamFuZyIsInN1YiI6IkpXVCBUb2tlbiIsImVtYWlsIjoic29sc29sQGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjoiUk9MRV9VU0VSIiwiY3JlYXRlX2RhdGUiOiIyMDI0LTA4LTAyVDE3OjMxOjQxLjcwMDE3MyIsImlhdCI6MTcyMjU4NzUwMSwiZXhwIjoxNzIyNTg3NjIxfQ.J_HxlIgceLJTjbU58_KIgfeb8gRiOfmHPBaWnQyXc7w";

        String hashed = tokenHasher.encode(test1);
        // 새로운 refresh token 가져오고, 해싱된 토큰 비교. 다른 값을 해싱했기 때문에 match하지 말아야한다.
        Assertions.assertFalse(tokenHasher.matches(test2, hashed));
    }

//    @Test
//    public void when_string_then_not_match_bCrypt(){
//        // 토큰 생성
//        String test1 = jwtIssuer.getRefreshToken(authorityInfo1);
//        log.info("test1 : {}",test1);
//        String test2 = jwtIssuer.getRefreshToken(authorityInfo2);;
//        log.info("test2 : {}",test2);
//
//        String hashed = bCryptPasswordEncoder.encode(test1);
//        // 새로운 refresh token 가져오고, 해싱된 토큰 비교. 다른 값을 해싱했기 때문에 match하지 말아야한다.
//        Assertions.assertFalse(bCryptPasswordEncoder.matches(test2, hashed));
//    }


    @Test
    public void when_reissue_then_invoke_exception(){
        // 토큰 생성
        issuedTokens = jwtIssuer.getAccessTokenByAuthorityInfo(authorityInfo1);
        // 토큰 저장
        jwtIssuer.saveIssuedToken(email, issuedTokens.getRefreshToken());
        String time1 = getCreatedTime(issuedTokens.getRefreshToken());
        log.info("초기 refresh token 생성 시간 : {}",time1);
        // 처음 생성
        IssuedTokens newIssuedTokens = jwtIssuer.getAccessTokenByRefreshToken(new RefreshDto(issuedTokens.getRefreshToken()));
        String time2 = getCreatedTime(newIssuedTokens.getRefreshToken());
        log.info("다음 refresh token 생성 시간 : {}",time2);

        Assertions.assertThrows(UnauthorizedException.class,
                () -> jwtIssuer.getAccessTokenByRefreshToken(new RefreshDto(issuedTokens.getRefreshToken())));
    }

    private String getCreatedTime(String refreshToken) {
        Claims claims = Jwts.parser()
                .verifyWith(key) // 서버가 저장하고 있는 키로 payload 암호 풀기
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();
        return (String) claims.get("create_date");
    }
}
