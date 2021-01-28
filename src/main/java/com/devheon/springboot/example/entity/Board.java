package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
/**
 * 변수로 선언된 writer가 toString()에 의해 출력될 때,
 * Member 엔티티를 가져오기 위해 데이터베이스 연결이 필요하게된다.
 * 이런 문제로 Layzy loading 시 연관관계까 있는 엔티티 클래스의 경우 exclude를 지정한다.
 */
@ToString(exclude = "writer")
public class Board extends BaseEntity {

    /**
     * Member의 email을 FK로 참조하는 구조
     * <p>
     * JPA에서 관계를 고려할 때 FK쪽을 먼저 해석하면 편하다.
     * Board와 Member는 N:1 관계이므로 Board에 @ManyToOne 사용
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;    /* 연관관계 지정정 */

    /**
     * 수정 관련 메소드
     */
    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}