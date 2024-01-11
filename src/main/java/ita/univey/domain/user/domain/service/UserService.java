package ita.univey.domain.user.domain.service;

import io.jsonwebtoken.Claims;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.UserRole;
import ita.univey.domain.user.domain.dto.ImageDto;
import ita.univey.domain.user.domain.dto.UserInfoDto;
import ita.univey.domain.user.domain.dto.UserJoinDto;
import ita.univey.domain.user.domain.dto.UserLoginDto;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Long join(UserJoinDto userJoinDto) {

        if (userRepository.findUserByEmail(userJoinDto.getEmail()).orElse(null) != null) {
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

        User saveUser = userRepository.save(user);
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

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("email로 user 찾기 실패"));
    }

    @Transactional
    public Integer updatePointByUser(User user, Integer point) {
        return user.updatePoint(point);
    }

    @Transactional
    public Long updateUserInfoByEmail(String email, UserInfoDto userInfoDto) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("회원 조회 실패"));
        user.updateUserInfo(userInfoDto);
        return user.getId();
    }

   @Transactional
    public Long updateUserInfoByEmail(String email, UserInfoDto userInfoDto, ImageDto imageDto) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("회원 조회 실패"));
        user.updateUserInfo(userInfoDto);
        log.info(userInfoDto.getImageDto().getOriginName());
        user.getUserImage().setOriginImageName(userInfoDto.getImageDto().getOriginName());
        user.getUserImage().setImageName(userInfoDto.getImageDto().getImageName());
        user.getUserImage().setImagePath(userInfoDto.getImageDto().getPathName());

        return user.getId();
    }

    public UserInfoDto getUserDetailWithImage(User user, ImageDto imageDto) {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNumber(user.getPhoneNumber())
                .imageDto(imageDto)
                .build();
        return userInfoDto;
    }
    public UserInfoDto getUserDetail(User user) {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNumber(user.getPhoneNumber())
                .build();
        return userInfoDto;
    }
    public boolean checkNicknameDuplicate(String nickname, String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("회원 조회 실패"));
        return userRepository.existsByNickNameAndEmailNotContains(nickname, user.getEmail());
    }

}
