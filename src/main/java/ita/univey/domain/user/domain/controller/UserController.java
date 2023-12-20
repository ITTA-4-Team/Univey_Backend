package ita.univey.domain.user.domain.controller;

import ita.univey.domain.user.domain.dto.UserJoinDto;
import ita.univey.domain.user.domain.dto.UserLoginDto;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.oauth2.kakao.KakaoProfile;
import ita.univey.global.oauth2.kakao.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final KakaoService kakaoService;
    private final UserService userService;


    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam String code) {
        /**
         * post 방식으로 key=value 데이터를 카카오로 요청
         * Retrofit2, OkHttp, RestTemplate 사용 가능
         */
        log.info("kakao controller 들어오냐?");
        KakaoProfile kakaoProfile = kakaoService.kakaoRequest(code);
        log.info("kakaoProfile의 사용자 이름 => {} ", kakaoProfile.getProperties().getNickname());

        String kakaoUserEmail = kakaoProfile.getKakao_account().getEmail();
        String kakaoUserName = kakaoProfile.getProperties().getNickname();
        String kakaoUserProviderId = kakaoProfile.getId().toString();
        String kakaoUserPw = kakaoUserEmail + '_' + kakaoUserProviderId;
        if (kakaoService.isNew(kakaoUserEmail)) {
            log.info("{}는 처음 들어온 회원이여서 자동 회원 가입 진행", kakaoUserName);
            UserJoinDto kakaoMemberJoin = UserJoinDto.builder()
                    .name(kakaoUserName)
                    .email(kakaoUserEmail)
                    .password(kakaoUserPw)
                    .providerId(kakaoUserProviderId)
                    .build();
            userService.join(kakaoMemberJoin);
        }


        log.info("{} 카카오로 로그인 프로세스 진행", kakaoUserName);
        UserLoginDto kakaoMemberLogin = UserLoginDto.builder()
                .password(kakaoUserPw)
                .email(kakaoUserEmail)
                .build();
        String jwt = userService.login(kakaoMemberLogin);
        return jwt;
    }
}
