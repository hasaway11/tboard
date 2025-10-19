package com.example.demo.entity;

import lombok.*;

import java.time.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  private long cno;
  private String content;
  private String writer;
  private LocalDateTime writeTime;
  private long pno;
}
