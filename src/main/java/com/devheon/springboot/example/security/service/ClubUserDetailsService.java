package com.devheon.springboot.example.security.service;

import com.devheon.springboot.example.security.dto.ClubAuthMemberDTO;
import com.devheon.springboot.example.entity.ClubMember;
import com.devheon.springboot.example.repository.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {
    private final ClubMemberRepository clubMemberRepository;

    /**
     * 스프링 시큐리티의 구조에서 인증을 담당하는 AuthenticationManager는 내부적으로
     * UserDetailsService를 호출하여 사용자의 정보를 가져온다.
     * 현재와 같이 JPA로 사용자의 정보를 가져오고 싶다면, 이 부분을 UserDetailsService가 이용하는 구조로 작성한다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("ClubUserDetailsService loadUserByUsername " + username);

        /**
         * ClubMemberRepository 연동
         */
        Optional<ClubMember> result = clubMemberRepository.findByEmail(username, false);
        if(result.isEmpty())
            throw new UsernameNotFoundException("Check Email or Social");

        ClubMember clubMember = result.get();
        log.info("-------------------------------------------");
        log.info(clubMember);

        ClubAuthMemberDTO clubAuthMember = new ClubAuthMemberDTO(
                clubMember.getEmail(),
                clubMember.getPassword(),
                clubMember.isFromSocial(),
                clubMember.getRoleSet().stream().map(role -> new SimpleGrantedAuthority("ROLE_"+role.name())).collect(Collectors.toSet())
        );
        clubAuthMember.setName(clubMember.getName());
        clubAuthMember.setFromSocial(clubMember.isFromSocial());

        return clubAuthMember;
    }
}
