package com.devheon.springboot.example.security.handler;

import com.devheon.springboot.example.security.dto.ClubAuthMemberDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
public class ClubLoginSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 시큐리티 로그인 관련 처리에는 AuthenticationSuccessHandler, AuthenticationFailureHandler 인터페이스를 제공
     * HttpSecurity의 formLogin()이나 oauth2Login() 후에 이러한 핸들러를 설정할 수 있다.
     * 이 예제에서는 oauth2Login() 이후에 이를 적용한다고 하자.
     *
     * 로그인 성공 이후의 처리를 담당하는 용도로 AuthenticationSuccessHandler 구현
     */

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private PasswordEncoder passwordEncoder;
    public ClubLoginSuccessHandler(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("------------------------------------------");
        log.info("onAUthenticationSuccess");

        ClubAuthMemberDTO authMember = (ClubAuthMemberDTO) authentication.getPrincipal();
        boolean fromSocial = authMember.isFromSocial();

        log.info("Need Modify Member ? " + fromSocial);
        boolean isPasswordChangeNeed = passwordEncoder.matches("1111", authMember.getPassword());

        if(fromSocial && isPasswordChangeNeed)
            redirectStrategy.sendRedirect(request, response, "/member/modify?from=social");

    }
}
