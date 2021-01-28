package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.MovieMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieMemberRepository extends JpaRepository<MovieMember, Long> {
}
