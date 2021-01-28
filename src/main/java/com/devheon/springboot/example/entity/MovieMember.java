package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "m_member")    /* 메세지 처리 -> Settings -> Editor - Inspections -> JPA -> Unresolved database references in annotations 해제 */
public class MovieMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;

    private String email;
    private String pw;
    private String nickname;
}
