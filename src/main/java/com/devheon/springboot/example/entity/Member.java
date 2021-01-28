package com.devheon.springboot.example.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Member extends BaseEntity {

    /**
     * email : PK
     * 별도로 FK를 사용하지 않음
     */

    @Id
    private String email;

    private String password;
    private String name;
}
