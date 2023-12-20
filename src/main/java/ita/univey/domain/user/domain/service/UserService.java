package ita.univey.domain.user.domain.service;

import io.jsonwebtoken.Claims;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.UserRole;
import ita.univey.domain.user.domain.dto.UserJoinDto;
import ita.univey.domain.user.domain.dto.UserLoginDto;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository UserRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Long join(UserJoinDto userJoinDto) {

        if (UserRepository.findUserByEmail(userJoinDto.getEmail()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
        // 가입되어 있지 않은 회원이면,
        // 권한 정보 만들고
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);

        String providerId = null;
        // provider Id 는 카카오에서 받아온다. 테스트할 or 로컬 로그인을 개발한 경우 uuid로 임의로 부여한다.
        // uuid는 10자리에서 자른다.
        if (userJoinDto.getProviderId() == null) {
            providerId = UUID.randomUUID().toString().substring(0, 10);
            ;
        } else {
            providerId = userJoinDto.getProviderId();
        }
        User user = User.builder()
                .name(userJoinDto.getName())
                .email(userJoinDto.getEmail())
                .password(passwordEncoder.encode(userJoinDto.getPassword()))
                .roleSet(roles)
                .providerId(providerId) // 임의의 providerId 생성 , 카카오로그인 작성 시 카카오에서 받아온 값으로 변경.
                .build();

        User saveUser = UserRepository.save(user);
        return saveUser.getId();

    }

    public String login(UserLoginDto userLoginDto) {

        //name을 email로 대체한다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword());

        // authenticate 메소드가 실행이 될 때 CustomUserDetailsService class의 loadUserByUsername 메소드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("로그인 시도한 {}의 AUTH =>{}", userLoginDto.getEmail(), authentication.getAuthorities());
        // 해당 객체를 SecurityContextHolder에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // authentication 객체를 createToken 메소드를 통해서 JWT Token을 생성
        String jwt = jwtProvider.generateAccessToken(authentication);
        //claim 열어보기 => user의 emial
        Claims claims = jwtProvider.getClaims(jwt);
        log.info("{}의 claim의 subject(email) => {}", userLoginDto.getEmail(), claims.getSubject());

        return jwt;

    }
}
