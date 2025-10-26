package com.example.demo.entity;

import com.fasterxml.jackson.annotation.*;
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
  @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
  private LocalDateTime writeTime;
  private long pno;
}
