package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * querydsl 사용시 QuerydslPredicateExecutor 인터페이스를 추가 상속
 */
public interface GuestbookRepository extends JpaRepository<Guestbook, Long>, QuerydslPredicateExecutor<Guestbook> {
}