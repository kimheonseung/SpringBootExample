package com.devheon.springboot.example.controller;

import com.devheon.springboot.example.security.dto.ClubAuthMemberDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequestMapping("/sample/")
public class SampleController {
    /**
     * @PreAuthorize()의 value로는 문자열로 된 표현식을 넣는다.
     * value 표현식은 '#'과 같은 특별한 기호나 authentication 같은 내장 변수를 사용가능
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/all")
    public void exAll() {
        /**
         * 로그인하지 않은 사용자도 접근
         */
        log.info("exAll...");
    }

    /**
     * 로그인 사용자 중 user95@zerock.com인 사용자만 접근 가능하도록 설정
     */
    @PreAuthorize("#clubAuthMember != null && #clubAuthMember.username eq \"user95@zerock.com\"")
    @GetMapping("/exOnly")
    public String exMemberOnly(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMember) {
        log.info("exMemberOnly...");
        log.info(clubAuthMember);
        return"/sample/admin";
    }

    /**
     * 컨트롤러에서 로그인된 사용자 정보를 확인하는 방법
     * 1. SecurityContextHelder 객체를 사용
     * 2. 직접 파라미터와 어노테이션을 사용
     * - 2를 이용함
     *
     * org.springframework.security.core.annotation.AuthenticationPrincipal
     * - getPrincipal() 메서드를 통해 Object 타입을 반환
     *   위 코드에서 @AuthenticationPrincipal은 별도 캐스팅 작업 없이 실제 ClubAuthMemberDTO 타입을 사용할 수 있다.
     */
    @GetMapping("/member")
    public void exMember(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMember) {
        /**
         * 로그인한 사용자만 접근
         */
        log.info("exMember...");

        log.info("-------------------------");
        log.info(clubAuthMember);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public void exAdmin() {
        /**
         * 관리자 권한이 있는 사용자만 접근
         */
        log.info("exAdmin...");
    }
}
