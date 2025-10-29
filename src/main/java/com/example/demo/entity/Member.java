package com.example.demo.entity;

import com.example.demo.dto.*;
import lombok.*;

import java.time.*;
import java.time.temporal.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Member {
  private String username;
  private String password;
  private String email;
    // 프로필사진 이름
  private String profile;

  @Builder.Default
  private LocalDate joinDay = LocalDate.now();
  @Builder.Default
  private Role role = Role.USER;
  @Builder.Default
  private Level level = Level.NORMAL;

  // 로그인 관련된 데이터 : 로그인 실패 횟수, 계정 블록 여부
  @Builder.Default
  private int failedAttempts = 0;
  @Builder.Default
  private boolean isLock = false;

  public MemberDto.MemberResponse toDto() {
    long days = ChronoUnit.DAYS.between(joinDay, LocalDate.now());
    return new MemberDto.MemberResponse(username, email, "/profile/" + profile, profile, joinDay, days, level);
  }
}







