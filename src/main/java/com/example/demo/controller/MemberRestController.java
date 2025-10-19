package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.security.*;
import java.util.*;

@Validated
@RestController
public class MemberRestController {
  @Autowired
  private MemberService memberService;

  @PreAuthorize("isAnonymous()")
  @GetMapping("/api/member/check-username")
  public ResponseEntity<String> checkUsername(@ModelAttribute @Valid MemberDto.UsernameCheck dto) {
    boolean result = memberService.checkUsername(dto);
    if(result)
      return ResponseEntity.ok("사용가능합니다");
    return ResponseEntity.status(HttpStatus.CONFLICT).body("사용중인 아이디입니다");
  }

  @PreAuthorize("isAnonymous()")
  @PostMapping("/api/member/new")
  public ResponseEntity<Void> signup(@ModelAttribute @Valid MemberDto.Create dto) {
    memberService.join(dto);
    return ResponseEntity.ok(null);
  }

  @PreAuthorize("isAnonymous()")
  @GetMapping("/api/member/username")
  public ResponseEntity<String> searchUsername(@RequestParam @NotEmpty(message="이메일은 필수입력입니다") @Email(message="이메일을 입력하세요") String email) {
    Optional<String> result = memberService.searchUsername(email);
    return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).body("사용자를 찾을 수 없습니다"));
  }

  @PreAuthorize("isAnonymous()")
  @PostMapping("/api/member/password")
  public ResponseEntity<String> resetPassword(@ModelAttribute @Valid MemberDto.ResetPassword dto) {
    memberService.resetPassword(dto);
    return ResponseEntity.ok("임시비밀번호를 가입 이메일로 보냈습니다");
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/api/member/profile")
  public ResponseEntity<String> changeProfile(@RequestParam(required=false) @NotNull(message="프로필 사진은 필수입력입니다") MultipartFile profile, Principal principal) {
    memberService.updateProfile(profile, principal.getName());
    return ResponseEntity.ok("프로필 사진을 변경했습니다");
  }
}
