package com.devheon.springboot.example.security.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Log4j2
@Getter
@Setter
@ToString
public class ClubAuthMemberDTO extends User implements OAuth2User {
    /**
     * DTO 역할을 수행하는 클래스인 동시에 스프링 시큐리티에 인증/인가 작업에 사용할 수 있는 클래스
     * ClubMember가 ClubAuthMemberDTO 타입으로 처리된 가장 큰 이유는,
     * 사용자의 정보를 가져오는 핵심 역할을 하는 UserDetailsService라는 인터페이스 때문이다.
     */

    private String email;
    private String password;
    private String name;
    private boolean fromSocial;

    /**
     * OAuth2User 타입 관련 필드
     */
    private Map<String, Object> attr;

    /**
     * 1. User 클래스를 상속하고, 부모 클래스인 User 클래스의 생성자를 호출하는 코드를 만든다.
     */
    public ClubAuthMemberDTO(
            String username,
            String password,
            boolean fromSocial,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, authorities);
        this.email = username;
        this.password = password;
        this.fromSocial = fromSocial;
    }

    public ClubAuthMemberDTO(
            String username,
            String password,
            boolean fromSocial,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attr
    ) {
        this(username, password, fromSocial, authorities);
        this.attr = attr;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attr;
    }
}
