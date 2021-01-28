package com.devheon.springboot.example.security.filter;

import com.devheon.springboot.example.security.dto.ClubAuthMemberDTO;
import com.devheon.springboot.example.security.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {
    private JWTUtil jwtUtil;

    /**
     * 특정 URL로 외부에서 로그인이 가능하도록 하고,
     * 로그인이 성공하면 클라이언트가 Authorization 헤더의 값으로 이용할 데이터를 전송한다.
     * '/api/login'이라는 URL로 외부의 클라이언트가 자신의 아이디와 패스워드로 로그인한다고 가정.
     * -> 일반 로그인과 동일한 계정으로 로그인하면 일정 기간동안 API를 호출할 수 있도록 구성한다.
     *
     * AbstractAuthenticationProcessingFilter
     * attemptAuthentication이라는 메서드와 패턴을 생성자로 받는 생성자가 필요함
     */

    public ApiLoginFilter(String defaultFilterProcessUrl, JWTUtil jwtUtil) {
        super(defaultFilterProcessUrl);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("-------------ApiLoginFilter-----------------");
        log.info("attemptAuthentication");

        String email = request.getParameter("email");
        String pw = request.getParameter("pw");

        /**
         * AuthenticationManager의 authenticate를 이용하기 위해 파라미터로 Authentication 객체를 사용한다.
         * Authentication 타입 객체로는 'XXXToken'을 사용한다.
         * ex) UsernamePasswordAuthenticationFilter 클래스는 UsernamePasswordAUthenticationToken 객체를 사용
         */
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, pw);

        /**
         * 로그인 성공 -> 인증 토큰 전송
         * 로그인 실패 -> JSON 결과 전송
         */

        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        /**
         * 인증 성공 처리
         */
        log.info("-----------ApiLoginFilter-------------");
        log.info("successfulAUthentication : " + authResult);
        log.info(authResult.getPrincipal());

        /* email */
        String email = ((ClubAuthMemberDTO) authResult.getPrincipal()).getUsername();

        String token = null;

        try {
            token = jwtUtil.generateToken(email);

            response.setContentType("text/plain");
            response.getOutputStream().write(token.getBytes());

            log.info(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
