package kimjang.toolkit.solsol.config.provider;

import kimjang.toolkit.solsol.domain.user.entities.Authority;
import kimjang.toolkit.solsol.domain.user.entities.User;
import kimjang.toolkit.solsol.domain.user.reposiotry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomUserNamePwdAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * HTTP의 Authorization 헤더에서 인증 정보 찾기
     * {email}:{password}를 Base64로 인코딩한 값 => <encodedCredentials>
     *
     * 요청 포멧
     * Key : Authorization
     * Value : "Basic <encodedCredentials>"
     *
     * @param authentication the authentication request object.
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (passwordEncoder.matches(pwd, user.get().getPwd())) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, pwd, getGrantedAuthorities(user.get().getAuthorities()));
                System.out.println("인증이 되었는가 : "+token.isAuthenticated()+" 권한은 무엇인가 : "+token.getAuthorities());
                return token;
            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        }else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
