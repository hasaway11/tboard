package com.example.demo.dto;

import com.example.demo.entity.*;
import com.example.demo.util.validation.Password;
import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.*;

import java.time.*;

public class MemberDto {
  @Data
  public static class UsernameCheckRequest {
    @NotEmpty(message="아이디는 필수입력입니다")
    @Pattern(regexp="^[a-z0-9]{6,10}$", message="아이디는 소문자와 숫자 6~10자입니다")
    private String username;
  }

  @Data
  public static class CreateRequest {
    @NotEmpty(message="아이디는 필수입력입니다")
    @Pattern(regexp="^[a-z0-9]{6,10}$", message="아이디는 소문자와 숫자 6~10자입니다")
    private String username;
    @Pattern(regexp="^[a-zA-Z0-9]{6,10}$", message="비밀번호는 영숫자 6~10자입니다")
    @Password
    private String password;
    @NotEmpty(message="이메일은 필수입력입니다")
    @Email
    private String email;
    private MultipartFile profile;

    public Member toEntity(String encodedPassword, String base64Image) {
      return Member.builder().username(username).password(encodedPassword).email(email).profile(base64Image).isLock(false).build();
    }
  }

  @Data
  public static class ResetPasswordRequest {
    @NotEmpty(message="아이디는 필수입력입니다")
    @Pattern(regexp="^[a-z0-9]{6,10}$", message="아이디는 소문자와 숫자 6~10자입니다")
    private String username;
  }

  @Data
  @AllArgsConstructor
  public static class MemberResponse {
    private String username;
    private String email;
    private String profile;
    @JsonFormat(pattern="yyyy년 MM월 dd일")
    private LocalDate joinDay;
    // 가입기간
    private long days;
    private Level level;
  }

  @Data
  public static class PasswordChangeRequest {
    @NotEmpty(message="기존 비밀번호는 필수입력입니다")
    @Pattern(regexp="^[a-zA-Z0-9]{6,10}$", message="현재 비밀번호는 영숫자 6~10자입니다")
    private String currentPassword;
    @NotEmpty(message="새 비밀번호는 필수입력입니다")
    @Pattern(regexp="^[a-zA-Z0-9]{6,10}$", message="새 비밀번호는 영숫자 6~10자입니다")
    private String newPassword;
  }

}
