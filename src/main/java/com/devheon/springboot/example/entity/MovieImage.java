package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = "movie")
public class MovieImage {
    /**
     * 단방향 참조로 처리할 것이고, @Query로 left join 등을 사용하게 된다.
     * @ElementCollection, @Embeddable과 같은 엔티티가 아닌 '값 객체(Value Object)'를 이용하는 방법도 있다.
     * 다만, 예제에서는 페이지 처리나 조인 처리가 많으므로 엔티티 타입으로 사용하도록 한다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inum;
    private String uuid;
    private String imgName;
    private String path;
    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;
}