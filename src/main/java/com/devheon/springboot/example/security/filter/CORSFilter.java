package com.devheon.springboot.example.security.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
/**
 * 모든 필터 중 가장 먼저 동작하도록 지정
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {
    /**
     * 외부 Ajax에서도 API를 사용하여 Authorization을 하려면 Cross Origin Resource Sharing 문제를 해결해야 한다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

        if("OPTIONS".equalsIgnoreCase(request.getMethod()))
            response.setStatus(HttpServletResponse.SC_OK);
        else
            filterChain.doFilter(request, response);

        /**
         * $(".btn").click(function() {
         *     $.ajax({
         *         beforeSend: function(request) {
         *             request.setRequestHeader("Authorization", "Bearer " + jwtValue);
         *         },
         *         dataType: 'json',
         *         url: 'http://localhost:8080/notes/all',
         *         data: {email: 'user10@zerock.com'},
         *         success: function(arr) {
         *             console.log(arr);
         *         }
         *     });
         * });
         */
    }
}
