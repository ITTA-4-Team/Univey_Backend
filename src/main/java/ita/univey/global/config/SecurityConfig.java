package ita.univey.global.config;

import ita.univey.global.jwt.JwtAccessDeniedHandler;
import ita.univey.global.jwt.JwtAuthenticationEntryPoint;
import ita.univey.global.jwt.JwtProvider;
import ita.univey.global.jwt.JwtSecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    public SecurityConfig(JwtProvider jwtProvider, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) {
        this.jwtProvider = jwtProvider;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                //.requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico"));


        //h2-console 하위 요청, 파비콘 요청은 security 로직을 수행하지 않게 해줌.
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)

                )
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /**
                 *  테스트용 patter n 작성 상태. 연결 후 세부 사항 수정 예정.
                 */

                .authorizeHttpRequests((registry) ->
                                registry.requestMatchers(
                                                new AntPathRequestMatcher("/login"),
                                                new AntPathRequestMatcher("/sign-in"),
                                                new AntPathRequestMatcher("/sign-up"),
                                                new AntPathRequestMatcher("/users/kakao/callback"),
                                                new AntPathRequestMatcher("/hello"),
                                                new AntPathRequestMatcher("/health"),
                                                new AntPathRequestMatcher("/surveys/list"),
                                                new AntPathRequestMatcher("/trends"),
                                                new AntPathRequestMatcher("/ngrok-test"))
                                        .permitAll()
                                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/admintest")).hasRole("ADMIN")

                                        .anyRequest().authenticated()

                )

                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )
                .apply(new JwtSecurityConfig(jwtProvider));
        return httpSecurity.build();
    }


}

