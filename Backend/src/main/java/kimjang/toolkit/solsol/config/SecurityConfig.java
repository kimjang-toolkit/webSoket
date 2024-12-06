package kimjang.toolkit.solsol.config;

import kimjang.toolkit.solsol.config.filter.*;
import kimjang.toolkit.solsol.config.provider.JwtAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationEntryPoint authEntryPoint;
    public SecurityConfig(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            JwtAuthenticationProvider jwtAuthenticationProvider,
            @Qualifier("delegatedAuthenticationEntryPoint")
            AuthenticationEntryPoint authEntryPoint
    ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);
        this.authEntryPoint = authEntryPoint;
    }

//    @Bean
//    public JdbcUserDetailsManager userDetailService(DataSource dataSource){
//        return new JdbcUserDetailsManager(dataSource);
//    }
    /**
     * 커스텀 security filter chain
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        // CSRF 토큰을 이용해서 원하는 브라우저에서만 요청할 수 있도록, CSRF 공격 방지
        // CSRF 토큰 값을 사용하는 헤더는 왜 CORS 설정을 하지 않았냐면, 프레임워크에서 csrf 헤더를 조작하기 때문이다.
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        // 모든 uri에 인증이 필요하지 않도록 설정
        // 한번 자격증명을 보내면 JSSESSIONID 값을 이용해서 보안 API 호출할 때 자격증명을 제공하지 않아도 괜찮은 상태
        // 세션 정보를 서버에 저장하지 않고 세션 ID를 제공하지 않도록함
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .cors(cors -> {
//                    cors.disable();
//                })
                .cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:5173", "http://localhost:5174","http://localhost:8080"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        // 비밀번호 같은 자격증명을 헤더에 담아서 보낼 수 있도록 설정
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        // Authorization 헤더를 노출하는 것을 허용, 여기에 JWT 토큰 값을 보낼 것
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    });
                })
//                // CookieCsrfTokenRepository 는 csrf 토큰을 쿠키로 유지하고 헤더 "XSRF-TOKEN"이란 이름으로 csrf 토큰을 저장한다.
//                // withHttpOnlyFalse은 App UI의 javascript가 쿠키를 읽을 수 있도록 하는 설정 // postman을 동작시키기 위함
//                // csrf가 세션 스토리지에 저장하게됨
//                .csrf(AbstractHttpConfigurer::disable)
                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/chat-room/**","/register","/user/*" ,"/login","/gs", "/refresh-token")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAt(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthorityLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManagerBuilder.getOrBuild()), BasicAuthenticationFilter.class)
//                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class) // JWT 토큰 생성은 정상적인 인증 후 진행함/
//                .addFilterBefore(new JWTValidatorFilter(), BasicAuthenticationFilter.class) // JWT 토큰 유효성 검사는 인증 전에 진행한다.
                .authorizeHttpRequests((requests) -> requests
                        // uri를 접근하기 위해 유저에게 권한이 있는지 체크 = 인가
                        // 역할에 ROLE_ 접두사를 붙일 필요 없음. security가 자동으로 붙여서 검색함.
                        .requestMatchers( HttpMethod.POST,"/chat-room**" ).hasRole("USER")
                        .requestMatchers( HttpMethod.GET,"/chat-room**" ).hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/login**","/refresh-token**").permitAll()
                        .requestMatchers( "/api-docs/**", "/swagger-ui/**","/register/**", "/**", "/gs").permitAll()
                        .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()) // 나머지 요청 모두 인증된 회원만 접근 가능
                .httpBasic(Customizer.withDefaults())
                // 예외가 발생하면 예외를 담아 controller로 전달하기
                .exceptionHandling(handler -> handler.authenticationEntryPoint(authEntryPoint));

        return http.build();
    }



    /**
     * DaoAuthenticationManager가 InMamoryUserManager 내에 있는 메서드로 사용자를 인증하려고
     *                                                      loadUserByUsername 메서드 반환 유형은 UserDetails입니다.
     *                                                      이는 유저의 이름으로 저장소에 있던 세부사항을 가져와서 User 객체를 만드니다.
     *
     * 유저 정보를 메모리 상이 아닌 JDBC로 DB에 저장하기 때문에 InMemoryUserDetailsManager는 주석 처리
     * @return
     */
    @Bean
    public InMemoryUserDetailsManager userDetailService(){
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("user")
                .password("1234")
                .authorities("admin")
                .roles("User")
                .build();

        return new InMemoryUserDetailsManager(admin); // 메모리 상에 유저의 자격증명 정보를 저장
    }

    /**
     * spring security 프레임워크 전체에서 사용할 PasswordEncoder를 정의한다.
     *
     * 인증을 keycloak에 전담했으니 비밀번호를 다루지 않을 것이다.
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 비밀번호를 일반 텍스트로 관리되기 때문에 프로덕션 환경에서는 위험하다.
        // BCrypt 알고리즘을 이용해서 비밀번호를 해싱하자
        return new BCryptPasswordEncoder();
    }
}
