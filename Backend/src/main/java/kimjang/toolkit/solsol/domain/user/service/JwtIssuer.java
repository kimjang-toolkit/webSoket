package kimjang.toolkit.solsol.domain.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kimjang.toolkit.solsol.config.cache.TokenHasher;
import kimjang.toolkit.solsol.config.jwt.JwtInvalidException;
import kimjang.toolkit.solsol.config.jwt.SecurityConstants;
import kimjang.toolkit.solsol.domain.cache.RefreshToken;
import kimjang.toolkit.solsol.domain.cache.RefreshTokenCacheService;
import kimjang.toolkit.solsol.domain.user.dto.AuthorityInfo;
import kimjang.toolkit.solsol.domain.user.dto.IssuedTokens;
import kimjang.toolkit.solsol.domain.user.dto.RefreshDto;
import kimjang.toolkit.solsol.domain.user.entities.Authority;
import kimjang.toolkit.solsol.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class JwtIssuer {
    private final SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
    private final Long ONE_SECONDS = 1000L;
    private final Long ONE_MINUTE = 60 * ONE_SECONDS;
    private final long accessExpireMin;
    private final long refreshExpireMin;
    private final RefreshTokenCacheService refreshTokenCacheService;
    private final TokenHasher tokenHasher;

    public JwtIssuer(@Value("${jwt.access-expire-min:10}") long accessExpireMin,
            @Value("${jwt.refresh-expire-min:60}") long refreshExpireMin,
                     RefreshTokenCacheService refreshTokenCacheService,
                     TokenHasher tokenHasher){
        this.accessExpireMin = accessExpireMin;
        this.refreshExpireMin = refreshExpireMin;
        this.refreshTokenCacheService = refreshTokenCacheService;
        this.tokenHasher = tokenHasher;
    }

    /**
     * refresh token 재발급 시 호출되는 메서드!
     * 요청된 refresh token을 바탕으로 email을 찾고
     * 새로운 tokens를 발급하고 캐시와 DB에 저장!
     * @param refreshDto
     * @return
     */
    public IssuedTokens getAccessTokenByRefreshToken(RefreshDto refreshDto) {
        try {
            AuthorityInfo authorityInfo = getAuthorityInfo(refreshDto);
            // cache hit 저장된 토큰 그대로 가져옴
            String savedRefreshToken = refreshTokenCacheService.searchToken(authorityInfo.getEmail());
            // 저장된 refresh token과 같은지 확인 같을 때 재발급 가능!
            hashMatchingTokens(refreshDto.getRefreshToken(), savedRefreshToken);

            // 기존 토큰 제거하기
            refreshTokenCacheService.deleteCache(authorityInfo.getEmail());
            // 새로운 토큰 재발급
            IssuedTokens issuedTokens = getAccessTokenByAuthorityInfo(authorityInfo);
            // 새로운 토큰 저장하기
            saveIssuedToken(authorityInfo.getEmail(), issuedTokens.getRefreshToken());
            return issuedTokens;

        } catch (SignatureException signatureException) {
            throw new JwtInvalidException("signature key is different", signatureException);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new JwtInvalidException("expired token", expiredJwtException);
        } catch (MalformedJwtException malformedJwtException) {
            throw new JwtInvalidException("malformed token", malformedJwtException);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new JwtInvalidException("using illegal argument like null", illegalArgumentException);
        }
    }

    private void hashMatchingTokens(String refreshToken, String savedRefreshToken){
        // hash match check
        if(!tokenHasher.matches(refreshToken, savedRefreshToken)){
            throw new UnauthorizedException("refresh token is already used");
        }
        log.info("존재하는 token과 같은 token을 가지고 있습니다.");
    }

    private AuthorityInfo getAuthorityInfo(RefreshDto refreshDto) {
        Claims claims = Jwts.parser()
                .verifyWith(key) // 서버가 저장하고 있는 키로 payload 암호 풀기
                .build()
                .parseSignedClaims(refreshDto.getRefreshToken())
                .getPayload();
        String email = (String) claims.get("email");
        String authorities = (String) claims.get("authorities");

        return AuthorityInfo.builder()
                .email(email)
                .authorities(authorities).build();
    }

    public IssuedTokens getAccessTokenByAuthorityInfo(AuthorityInfo authorityInfo) {
        String accessToken = getAccessToken(authorityInfo);
        String refreshToken = getRefreshToken(authorityInfo);
        return IssuedTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String getAccessToken(AuthorityInfo authorityInfo) {
        log.info("유통기한 : {}",accessExpireMin*ONE_MINUTE);
        return getToken(authorityInfo.getEmail(), authorityInfo.getAuthorities(), accessExpireMin*ONE_MINUTE);
    }
    public String getRefreshToken(AuthorityInfo authorityInfo) {
        return getToken(authorityInfo.getEmail(), authorityInfo.getAuthorities(), refreshExpireMin*ONE_MINUTE);
    }

    private String getToken(String email, String authorities, Long expireTime) {
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return Jwts.builder().issuer("Sol-Kimjang").subject("JWT Token")
                // 비밀번호는 절대 안된다.
                .claim("email", email)
                .claim("authorities", authorities)
                .claim("create_date", LocalDateTime.now().toString()) // 생성 시간을 둬서 여러번 생성되는 것을 방지하기
                .issuedAt(new Date(now))
                // 만료시간, ms 단위, 만료 시간을 넘어선 토큰이 들어오면 만료됐다고 예외가 발생한다.
                .expiration(new Date(now + expireTime))
                // 디지털 서명 작성
                .signWith(key).compact();
    }

    public void saveIssuedToken(String email, String refreshToken) {
        String hashedRefreshToken = tokenHasher.encode(refreshToken);
        refreshTokenCacheService.saveToken(email, hashedRefreshToken);
    }
}
