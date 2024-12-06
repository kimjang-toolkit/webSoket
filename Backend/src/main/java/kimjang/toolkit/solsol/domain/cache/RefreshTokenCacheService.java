package kimjang.toolkit.solsol.domain.cache;

import kimjang.toolkit.solsol.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenCacheService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * CachePut은 캐시가 있더라도 메서드를 실행한 결과를 Cache에 저장
     * 따라서 무조건 새로운 token을 저장하고 싶은 경우 적절
     * 한 메일에 한 refresh token만 가능!
     *
     * @param email
     * @param hashedRefreshToken : hashing된 refresh token
     * @return
     */
    @CachePut(value= "refresh", key="#email")
    public String saveToken(String email, String hashedRefreshToken){
        Optional<RefreshToken> token = refreshTokenRepository.findByEmail(email);
        if(token.isPresent()){ // 예전에 로그인해서 refresh token이 남아 있는 경우 업데이트
            token.get().setRefreshToken(hashedRefreshToken);
            return refreshTokenRepository.save(token.get()).getRefreshToken();
        } else{ // 처음 refresh token 만들어서 저장하는 경우
            RefreshToken newToken = RefreshToken.builder()
                    .email(email)
                    .refreshToken(hashedRefreshToken)
                    .build();
            return refreshTokenRepository.save(newToken).getRefreshToken();
        }
    }

    @CacheEvict(value = "refresh", key = "#email")
    public void deleteCache(String email){}

    /**
     * 재발급 용도로 저장된 token 검색
     * @param email
     * @return
     */
    @Cacheable(value= "refresh")
    public String searchToken(String email){
        return refreshTokenRepository.findByEmail(email)
                .orElseThrow(()-> new UnauthorizedException("인증되지 않았습니다. 다시 시도해주세요.")).getRefreshToken();
    }
}
