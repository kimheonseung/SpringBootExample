package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GuestbookDTO {
    /**
     * DTO는 엔티티 객체와 달리 각 계층끼리 주고받는 객체이다.
     * 실제 프로젝트 작성에선 영속계층 바깥쪽에서 DTO를 이용하는 것을 권장한다.
     * 엔티티 객체와 유사하지만 목적 자체가 데이터 전달이므로, 읽고 쓰는 것이 모두 허용되며 일회성 성격이 강하다.
     * DTO를 사용하면 엔티티 객체의 범위를 한정지을 수 있으므로 좀 더 안전한 코드를 작성할 수 있다.
     * 가장 큰 단점은 Entity와 유사한 코드 중복으로 개발한다는 점, DTO -> Entity, Entity -> DTO 과정이 필요하다는 점이다.
     *
     * 반면, 엔티티 객체는 실제 데이터베이스와 관련이 있고, 내부 엔티티 매니저가 관리하는 객체이다.
     */
    private Long gno;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate, modDate;
}