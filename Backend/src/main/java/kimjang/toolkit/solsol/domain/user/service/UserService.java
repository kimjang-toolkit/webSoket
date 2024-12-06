package kimjang.toolkit.solsol.domain.user.service;

import kimjang.toolkit.solsol.domain.user.dto.*;
import kimjang.toolkit.solsol.domain.user.entities.Authority;
import kimjang.toolkit.solsol.exception.UnauthorizedException;
import kimjang.toolkit.solsol.domain.user.entities.User;
import kimjang.toolkit.solsol.domain.user.reposiotry.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtIssuer jwtIssuer;

    @Transactional
    public UserDto registerUser(CreateUserDto createUserDto){
        String hashPwd = passwordEncoder.encode(createUserDto.getPwd());
        Optional<User> userOptional = userRepository.findByEmail(createUserDto.getEmail());
        if(userOptional.isPresent()){
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        User user = User.of(createUserDto, hashPwd);
        User savedUser = userRepository.save(user);
        return UserDto.toDto(savedUser);
    }

    public boolean checkUniqueEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public UserDto findByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow();
        return UserDto.toDto(user);
    }

    /**
     * 유저가 처음으로 로그인할 때 생성되는 로그인 결과 값 LoginSuccessDto
     *
     * @param loginDto
     * @return
     */
    public LoginSuccessDto userLoginAndSaveRefreshToken(LoginDto loginDto){
        try{
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new AuthenticationException("존재하지 않는 아이디입니다."));
            if (!passwordEncoder.matches(loginDto.getPwd(), user.getPwd())) {
                log.error("bad credentials : {}",loginDto.getPwd());
                throw new BadCredentialsException("bad credential: using unmatched password");
            }
            log.info("email : "+loginDto.getEmail());
            String authorities = populateAuthorities(user.getAuthorities());
            AuthorityInfo authorityInfo = AuthorityInfo.builder().email(user.getEmail())
                    .authorities(authorities).build();
            // 새로운 tokens 재발급
            IssuedTokens tokens = jwtIssuer.getAccessTokenByAuthorityInfo(authorityInfo);
            // 재발급한 tokens Cache와 DB에 저장
            jwtIssuer.saveIssuedToken(user.getEmail(), tokens.getRefreshToken());
            return LoginSuccessDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .imgUrl(user.getImgUrl())
                    .accessToken(tokens.getAccessToken())
                    .refreshToken(tokens.getRefreshToken())
                    .build();
        } catch (AuthenticationException e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 기존 refresh token으로 새로운 로그인 결과 값 LoginSuccessDto을 생성
     * @param refreshDto
     * @return
     */
    public IssuedTokens getAccessToken(RefreshDto refreshDto) {
        /**
         * 캐시나 DB에 존재하는 refresh token인지 확인
         * - 없으면 401 에러
         * - 있으면 기존꺼 지우고 새로운 값으로 업데이트 (Cacheput)
         * - refresh token의 만료시간까지 사용되지 않으면 제거
         */

        return jwtIssuer.getAccessTokenByRefreshToken(refreshDto);
    }

    public UserProfileDto getUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();

        String userEmail = context.getAuthentication().getName();
        if(userEmail.equals("anonymousUser")){
            throw new UnauthorizedException("인가되지 않은 요청입니다.");
        }
        return userRepository.findProfileByEmail(userEmail);
    }

    private String populateAuthorities(Set<Authority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (Authority authority : collection) {
            authoritiesSet.add(authority.getName());
        }
        return String.join(",", authoritiesSet);
    }
}
