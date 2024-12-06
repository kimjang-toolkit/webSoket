package kimjang.toolkit.solsol.jwt;

import kimjang.toolkit.solsol.SolsolApplication;
import kimjang.toolkit.solsol.domain.cache.RefreshToken;
import kimjang.toolkit.solsol.domain.cache.RefreshTokenCacheService;
import kimjang.toolkit.solsol.domain.cache.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest(classes = SolsolApplication.class)
@ActiveProfiles(value = "dev")
public class RefreshTokenCacheServiceTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenCacheService refreshTokenCacheService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void checkCacheableToken(){
        String email = "solsol@gmail.com";
        String refreshToken = "1234";
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);

        // email에 맞는 refresh token 캐시에 저장
        refreshTokenCacheService.saveToken(email, hashedRefreshToken);
        // 캐시에 있는 refresh token 얻어오기
        String savedRefreshToken = refreshTokenCacheService.searchToken(email);
        Assertions.assertTrue(passwordEncoder.matches(refreshToken, savedRefreshToken));
    }



    private final int testCount = 10000;
    @Test
    public void countTimeDBAndCache(){
        // set up
        String email = "solsol@gmail.com";
        String refreshToken = "1234";
        String hashedRefreshToken = passwordEncoder.encode(refreshToken);
        RefreshToken tokenEntity = RefreshToken.builder()
                .email(email)
                .refreshToken(hashedRefreshToken)
                .build();
        refreshTokenRepository.save(tokenEntity);
        // email에 맞는 refresh token 캐시에 저장
        refreshTokenCacheService.saveToken(email, hashedRefreshToken);

        LocalDateTime start = LocalDateTime.now();
        for(int i=0; i<testCount; i++){
            String savedToken = refreshTokenRepository.findByEmail(email)
                    .get().getRefreshToken();
        }
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        log.info("DB 쿼리 {}회 걸린 시간 : {}",testCount, duration.toMillis());


        LocalDateTime start2 = LocalDateTime.now();
        for(int i=0; i<testCount; i++){
            String savedRefreshToken = refreshTokenCacheService.searchToken(email);
        }
        LocalDateTime end2 = LocalDateTime.now();
        Duration duration2 = Duration.between(start2, end2);
        log.info("캐싱 쿼리 {}회 걸린 시간 : {}",testCount, duration2.toMillis());

    }

    @Test
    public void countTimeDBAndCacheIn10000tokens(){
        // set up
        String prefixEmail = "solsol@gmail.com";
        String prefixRefreshToken = "1234";
        for(int i=0; i<10000; i++){
            String hashedRefreshToken = passwordEncoder.encode(prefixRefreshToken+i);
            RefreshToken tokenEntity = RefreshToken.builder()
                    .email(prefixEmail+i)
                    .refreshToken(hashedRefreshToken)
                    .build();
            refreshTokenRepository.save(tokenEntity);
            // email에 맞는 refresh token 캐시에 저장
            refreshTokenCacheService.saveToken(prefixEmail+i, hashedRefreshToken);
        }

        String email = "solsol@gmail.com500";
        String refreshToken = "1234500";
        LocalDateTime start = LocalDateTime.now();
        for(int i=0; i<testCount; i++){
            String savedToken = refreshTokenRepository.findByEmail(email)
                    .get().getRefreshToken();
        }
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        log.info("DB 쿼리 {}회 걸린 시간 : {}",testCount, duration.toMillis());


        LocalDateTime start2 = LocalDateTime.now();
        for(int i=0; i<testCount; i++){
            String savedRefreshToken = refreshTokenCacheService.searchToken(email);
        }
        LocalDateTime end2 = LocalDateTime.now();
        Duration duration2 = Duration.between(start2, end2);
        log.info("캐싱 쿼리 {}회 걸린 시간 : {}",testCount, duration2.toMillis());

    }
}
