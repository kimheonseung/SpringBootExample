package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {
}