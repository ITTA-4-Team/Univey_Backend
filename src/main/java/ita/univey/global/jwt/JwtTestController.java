package ita.univey.global.jwt;

import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.dto.UserJoinDto;
import ita.univey.domain.user.domain.dto.UserLoginDto;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JwtTestController {
    /**
     * react와 연결 전 postman을 통해 jwt 인증, 인가 테스트 하는 컨트롤러 입니다.
     */
    private final UserService userService;
    private final UserRepository userRepository;
    @Value("${jwt.header}")
    private String accessHeader;

    @PostMapping("/sign-up")
    public ResponseEntity<User> join(@Valid @RequestBody UserJoinDto userJoinDto) {
        Long joinUserId = userService.join(userJoinDto);
        User joinUser = userRepository.findUserById(joinUserId).get();
        log.info("name : {}, email : {} 회원 가입 완료", joinUser.getName(), joinUser.getEmail());
        return ResponseEntity.ok().body(joinUser);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> loin(@Valid @RequestBody UserLoginDto userLoginDto) {
        String jwt = userService.login(userLoginDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(accessHeader, "Bearer " + jwt);
        return new ResponseEntity<>(jwt, httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/hello")
    public String test2() {
        return "SUCESSSSSS~~~~~~~";
    }

    @GetMapping("/admintest")
    public ResponseEntity<String> roleTest() {
        return ResponseEntity.ok().body("SUCCESS~~");
    }
}
