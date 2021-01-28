package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDTO {
    private Long num;
    private String title;
    private String content;
    private String writerEmail;    /* 연관관계 없이 */
    private LocalDateTime regDate, modDate;
}
