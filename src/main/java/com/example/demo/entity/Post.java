package com.example.demo.entity;

import lombok.*;

import java.time.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Post {
  private long pno;
  private String title;
  private String content;
  private String writer;
  @Builder.Default
  private LocalDateTime writeTime = LocalDateTime.now();
  @Builder.Default
  private long readCnt = 0;
  @Builder.Default
  private long goodCnt = 0;
  @Builder.Default
  private long badCnt = 0;
}
