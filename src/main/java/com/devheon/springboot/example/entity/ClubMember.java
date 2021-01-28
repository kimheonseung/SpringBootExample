package com.devheon.springboot.example.entity;

import com.devheon.springboot.example.constant.ClubMemberRole;
import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ClubMember extends BaseEntity {
    @Id
    private String email;
    private String password;
    private String name;
    private boolean fromSocial;

    /**
     * ClubMember는 여러 권한을 가질 수 있어야 한다.
     * ClubMember와 ClubMemberRole의 관계는 1:N 관계지만,
     * 사실상 ClubMemberRole 자체가 핵심적인 역할을 하지 못하므로 별도 엔티티 보다는 @ElementCollection을 이용하여 별도 PK 없이 구성한다.
     * 다만, 이 권한은 ClubMember 객체의 일부로만 사용되므로 JPA의 @ElementCollection을 이용한다.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ClubMemberRole> roleSet = new HashSet<>();

    public void addMemberRole(ClubMemberRole clubMemberRole) {
        roleSet.add(clubMemberRole);
    }
}
