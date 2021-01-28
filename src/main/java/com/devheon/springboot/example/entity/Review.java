package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 매핑 테이블
 * - 매핑 테이블은 주로 동사나 히스토리를 의미하는 테이블이다.
 * - '회원이 영화에 대해서 평점을 준다'는 행위가 매핑 테이블이 필요한 부분이다.
 * - ManyToMany와의 차이점. 두 엔티티 간의 추가적인 데이터를 기록할 수 없다.
 * - 매핑 테이블은 두 테이블 사이에서 양쪽은 PK를 참조하는 형태로 구성된다.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"movie", "member"})
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewnum;
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    private MovieMember movieMember;

    private int grade;
    private String text;

    public void changeGrade(int grade) {
        this.grade = grade;
    }
    public void changeText(String text) {
        this.text = text;
    }
}