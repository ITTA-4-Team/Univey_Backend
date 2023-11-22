package ita.univey.global.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final String AUTHORIZE_HEADER = "Authorization";


    public JwtAuthorizationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        String requestURI = request.getRequestURI();
        log.info("요청 url in filter => {}", request.getRequestURI());

        //request header에서 토큰 꺼내오기.
        String bearerToken = extractAccessToken(request).orElse(null);

        if (bearerToken != null) {
            String token = bearerToken.substring("Bearer ".length());
            log.info("REQUEST에서 보낸 JWT => {}", token);
            // 1차 체크(정보가 변조되지 않았는지 체크)
            if (jwtProvider.verify(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                log.info("로그인하고 권한 가져오기 =>{}", authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Security Context에 '{}'의 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            } else {
                log.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);

            }
        }
        filterChain.doFilter(request, response);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZE_HEADER));
    }
}
