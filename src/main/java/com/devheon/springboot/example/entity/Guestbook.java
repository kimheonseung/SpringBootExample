package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.*;

/**
 * Entity 클래스는 가능하면 setter 기능을 만들지 않는 것이 권장사항이지만,
 * 필요에 따라 수정기능을 만들기도 한다.
 * Entity가 내부에서 병경되면 JPA를 관리하는 쪽이 복잡해질 수 있기 때문이다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Guestbook extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gno;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1500, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    /**
     * 수정 기능을 하는 메소드
     */
    public void changeTitle(String title) {
        this.title = title;
    }
    public void changeContent(String content) {
        this.content = content;
    }
}