package ita.univey.domain.user.domain.controller;

import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.dto.UserJoinDto;
import ita.univey.domain.user.domain.dto.UserLoginDto;
import ita.univey.domain.user.domain.dto.UserLoginResponseDto;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import ita.univey.global.oauth2.kakao.KakaoProfile;
import ita.univey.global.oauth2.kakao.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final UserService userService;
    @Value("${jwt.header}")
    private String accessHeader;

    @GetMapping("/kakao/callback")
    public ResponseEntity<BaseResponse<UserLoginResponseDto>> kakaoCallback(@RequestParam String code) {
        Long userId = null;
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
            userId = userService.join(kakaoMemberJoin);
        }


        log.info("{} 카카오로 로그인 프로세스 진행", kakaoUserName);
        if (userId == null) {
            User user = userRepository.findUserByEmail(kakaoUserEmail).orElseThrow(() -> new RuntimeException("로그인 오류! 없는 회원 입니다."));
            userId = user.getId();
        }

        UserLoginDto kakaoMemberLogin = UserLoginDto.builder()
                .password(kakaoUserPw)
                .email(kakaoUserEmail)
                .build();

        User loginUser = userRepository.findUserById(userId).orElseThrow(() -> new RuntimeException("로그인 오류! 없는 회원"));
        String jwt = userService.login(kakaoMemberLogin);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(accessHeader, "Bearer " + jwt);
        UserLoginResponseDto loginUserResponse = UserLoginResponseDto.builder()
                .userName(loginUser.getName())
                .point(loginUser.getPoint())
                .build();
        BaseResponse<UserLoginResponseDto> successResponse = BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, loginUserResponse);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(successResponse);
    }
}
