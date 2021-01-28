package com.devheon.springboot.example.security.filter;

import com.devheon.springboot.example.security.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

    private AntPathMatcher antPathMatcher;
    private String pattern;
    private JWTUtil jwtUtil;

    public ApiCheckFilter(String pattern, JWTUtil jwtUtil) {
        this.antPathMatcher = new AntPathMatcher();
        this.pattern = pattern;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("REQUESTURI : " + request.getRequestURI());
        log.info(antPathMatcher.match(pattern, request.getRequestURI()));

        if(antPathMatcher.match(pattern, request.getRequestURI())) {
            log.info("ApiChekFilter...");
            boolean checkHeader = checkAuthHeader(request);

            /**
             * ApiCheckFilter는 스프링 시큐리티가 사용하는 쿠키나 세션을 이요하지 않으므로
             * Athorization 헤더가 없어도 200 신호는 주고받는다.
             * 이를 해결하기 위해 AuthenticationManager를 이용하거나, ApiCheckFilter에서 간단히 JSON 포맷의 에러메세지를 전송하는 방법을 사용한다.
             */

            if(checkHeader) {
                filterChain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                /**
                 * json 리턴 및 한글 깨짐 수정.
                 */
                response.setContentType("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                String message = "FAIL CHECK API TOKEN";
                json.put("code", "403");
                json.put("message", message);

                PrintWriter out = response.getWriter();
                out.println(json);
                return;
            }
        }

        /**
         * 다음 필터의 단계로 넘어가는 코드
         */
        filterChain.doFilter(request, response);
    }

    private boolean checkAuthHeader(HttpServletRequest request) {
        /**
         * 특정 API를 호출하는 클라이언트에서는 다른 서버나 Application으로 실행되기 때문에 쿠키나 세션을 활용할 수 없다.
         * 따라서 Request를 전송할 때, http 헤더 메시지에 특별한 값을 지정해서 전송한다.\
         * Authorization 헤더는 이러한 용도로 사용한다.
         *
         * 다음은 Authorization 헤더값을 확인하고 12345678 이란 값이면 true를 반환하는 로직
         */
        boolean checkResult = false;

        String authHeader = request.getHeader("Authorization");

        /**
         * Authorization 헤더 메시지의 경우 일반적으론 Basic을 사용하고, JWT는 Bearer를 사용한다.
         */
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            log.info("Authorization exists : " + authHeader);

            try {
                String email = jwtUtil.validateAndExtract(authHeader.substring(7));
                log.info("validate result : " + email);
                checkResult = email.length() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return checkResult;
    }
}