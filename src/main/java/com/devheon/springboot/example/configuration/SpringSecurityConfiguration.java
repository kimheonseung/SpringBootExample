package com.devheon.springboot.example.configuration;

import com.devheon.springboot.example.security.filter.ApiCheckFilter;
import com.devheon.springboot.example.security.filter.ApiLoginFilter;
import com.devheon.springboot.example.security.handler.ApiLoginFailHandler;
import com.devheon.springboot.example.security.handler.ClubLoginSuccessHandler;
import com.devheon.springboot.example.security.service.ClubUserDetailsService;
import com.devheon.springboot.example.security.util.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 시큐리티 관련 기능을 쉽게 설정하기 위해 WebSecurityConfigurerAdapter 상속
 */
@Configuration
@Log4j2
/**
 * 어노테이션 기반의 접근 제한을 설정할 수 있도록 설정
 * securedEnabled는 예전 버전의 @Secure 어노테이션이 사용 가능한지 지정
 * - @PreAuthorize를 사용하기 위함.
 * - 지정된 URL에 접근제한을 거는 방식과 달리 어노테이션만으로 접근제한이 필요한 컨트롤러의 메서드에 @PreAuthorize 적용
 */
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private ClubUserDetailsService userDetailsService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClubLoginSuccessHandler successHandler() {
        return new ClubLoginSuccessHandler(passwordEncoder());
    }

    @Bean
    public ApiCheckFilter apiCheckFilter() {
        /**
         * 특정 url만 WebToken을 검증한다.
         */
        return new ApiCheckFilter("/notes/**/*", jwtUtil());
    }

    @Bean
    public ApiLoginFilter apiLoginFilter() throws Exception {
        ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/api/login", jwtUtil());
        /**
         * AbstractAuthenticationProcessingFilter는 반드시 AuthenticationManager가 필요하다.
         */
        apiLoginFilter.setAuthenticationManager(authenticationManager());
        /* 인증 실패 처리 핸들러 */
        apiLoginFilter.setAuthenticationFailureHandler(new ApiLoginFailHandler());
        return apiLoginFilter;
    }

    @Bean
    public JWTUtil jwtUtil() {
        return new JWTUtil();
    }

    /**
     * ClubUserDetailsService가 빈으로 등록되면 이를 자동으로 스프링 시큐리티에서 UserDetailsService로 인식하므로
     * 기존에 임시로 설정한 configure 부분을 사용하지 않도록 한다.
     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                .password("$2a$10$31LySA/0AiX7BXY97GQVEO36GeIGWUU5e4wKlvkm16/qJqyWhTEAK")
//                .roles("USER");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * authorizeRequests : 인증이 필요한 자원을 설정할 수 있다.
         * antMatchers : 앤트 스타일의 패턴으로 원하는 자원을 선택 가능
         * permitAll : 로그인하지 않은 사용자도 접근 가능
         */
//        http.authorizeRequests()
//                .antMatchers("/sample/all").permitAll()
//                .antMatchers("/sample/member").hasRole("USER");

        /**
         * 인증/인가 문제시 로그인 화면
         * formLogin()을 이용하는 경우 별도의 디자인을 적용하지 위해 추가적 설정이 필요하다.
         * loginPage(), loginProcessUrl(), defaultSuccessUrl(), failureUrl() 등을 이용한다.
         * 대부분의 애플리케이션은 고유 디자인을 적용하기 위해 loginPage()를 사용하여 별도 로그인 페이지를 이용한다.
         */
        http.formLogin();

        /**
         * CSRF 설정
         * 스프링 시큐리티는 기본적으로 Cross Site Request Forgery(사이트간 요청 위조) 공격을 방어하기 위해
         * 임의의 값을 만들어 GET 방식을 제외한 모든 요청 방식에 포함시켜야만 정상적인 동작이 가능하다.
         * 외부에서 REST방식으로 이용할 수 있는 보안 설정을 다루기 위해 CSRF 토큰을 발행하지 않도록 설정한다.
         */
        http.csrf().disable();

        /**
         * logout() 메서드를 이용하면, 로그아웃 처리가 가능하다.
         * formLogout() 역시 별도의 설정이 없는 경우 스프링 시큐리티가 제공하는 웹 페이지를 보게 된다.
         * /logout URL 호출
         * - CSRF 토큰을 이용한 경우, /logout URL은 <form>태그와 버튼으로 구성된 화면이 나오고
         *   이용하지 않은 경우 GET 방식으로 로그아웃이 처리된다.
         * - formLogin()과 마찬가지로 logoutUrl(), logoutSuccessUrl() 등을 지정할 수 있다.
         * - 스프링 시큐리티는 기본적으로 HttpSession을 이용하는데, invalidatedHttpSession()과 deleteCookies()를 이용하여
         *   쿠키나 세션을 무효화 시킬 수 있도록 설정 가능하다.
         */
        http.logout();

        /**
         * oauth 로그인 가능하도록
         */
        http.oauth2Login().successHandler(successHandler());

        /**
         * Remember Me 설정
         * 최근 모바일과 함께 많이 사용됨.
         * 웹의 인증 방식 중 쿠키(HttpCooke) 사용
         * 한번 로그인한 사용자가 브라우저를 닫은 후 다시 접속해도 별도의 로그인 절차 없이 바로 로그인 처리가 진행된다.
         * 단, 소셜로그인에서는 사용할 수 없음.
         */
        http.rememberMe().tokenValiditySeconds(60*60*7).userDetailsService(userDetailsService);

        /**
         * ApiCheckFilter의 동작 순서를 조절.
         * UsernamePasswordAuthenticationFilter는 사용자 아이디와 패스워드를 기반으로 동작하는 필터이다.
         * ApiCheckFilter를 이 필어 이전에 동작하도록 지정
         */
        http.addFilterBefore(apiCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
