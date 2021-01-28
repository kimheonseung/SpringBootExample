package com.devheon.springboot.example.security.service;

import com.devheon.springboot.example.constant.ClubMemberRole;
import com.devheon.springboot.example.security.dto.ClubAuthMemberDTO;
import com.devheon.springboot.example.entity.ClubMember;
import com.devheon.springboot.example.repository.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubOAuth2UserDetailsService extends DefaultOAuth2UserService {
    /**
     * OAuth 연동 관련 가장 먼저 할 작업은 로그인 처리 후 결과를 가져오는 작업 환경을 구성하는 것이다.
     * OAuth2UserService
     * - org.springframework.security.oauth2.client.userinfo.OAuth2UserService 인터페이스는
     *   UserDetailsService의 OAuth 버전
     * - OAuth2UserService는 여러 구현 클래스를 가지고 있는데, DefaultOAuth2UserService를 사용할 것이다.
     */

    private final ClubMemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("----------------------------------------");
        log.info("userRequest : " + userRequest);

        String clientName = userRequest.getClientRegistration().getClientName();
        // google
        log.info("clientName : " + clientName);
        // id_token
        log.info(userRequest.getAdditionalParameters());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("=============================================");
        oAuth2User.getAttributes().forEach((k, v) -> {
            // sub, picture, email, email_verified
            log.info(k + " : " + v);
        });

        String email = null;

        if(clientName.equals("Google"))
            email = oAuth2User.getAttribute("email");

        log.info("EMAIL: : " + email);

        ClubMember member = saveSocialMember(email);

        ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
                member.getEmail(),
                member.getPassword(),
                true,
                member.getRoleSet().stream().map(
                        role -> new SimpleGrantedAuthority("ROLE_"+role.name())
                ).collect(Collectors.toList()),
                oAuth2User.getAttributes()
        );
        clubAuthMember.setName(member.getName());

        return clubAuthMember;
    }

    /**
     * 소셜 로그인한 이메일 처리
     */
    private ClubMember saveSocialMember(String email) {
        /**
         * 기존 동일한 메일로 가입한 회원이 있는 경우는 그대로 조회만
         */
        Optional<ClubMember> result = repository.findByEmail(email, true);
        if(result.isPresent())
            return result.get();

        /**
         * 없다면 회원 추가 패스워드는 1111, 이름은 그냥 메일 주소로
         * 패스워드는 추후에 바꿀 수 있게 하든지,
         * fromSocial인 회원은 username, password로 로그인이 불가능하게 하든지의 처리가 필요
         */
        ClubMember clubMember = ClubMember.builder()
                .email(email)
                .name(email)
                .password(passwordEncoder.encode("1111"))
                .fromSocial(true)
                .build();
        clubMember.addMemberRole(ClubMemberRole.USER);
        repository.save(clubMember);
        return clubMember;
    }
}
