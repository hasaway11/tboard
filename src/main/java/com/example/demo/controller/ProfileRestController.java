package com.example.demo.controller;

import com.example.demo.service.*;
import com.example.demo.util.*;
import jakarta.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

@RestController
public class ProfileRestController {
  @Autowired
  private MemberService memberService;

  @PostConstruct
  public void makeFolder() {
    File tempFolder = new File(TBoardConstant.TEMP_FOLDER);
    File profileFolder = new File(TBoardConstant.PROFILE_FOLDER);
    if(!tempFolder.exists())
      tempFolder.mkdir();
    if(!profileFolder.exists())
      profileFolder.mkdir();
  }

  @PostMapping("/profile/new")
  public ResponseEntity<Map<String, String>> uploadProfile(MultipartFile profile) {
    try {
      if (profile != null && !profile.isEmpty()) {
        File dest = new File(TBoardConstant.TEMP_FOLDER, profile.getOriginalFilename());
        profile.transferTo(dest);
      }
    } catch(IOException e) {
      return ResponseEntity.status(409).body(null);
    }
    String profileName = profile.getOriginalFilename();
    Map<String,String> map = Map.of("profileUrl", "/temp/" + profileName, "profileName", profileName);
    return ResponseEntity.ok(map);
  }

  private ResponseEntity<byte[]> readProfile(String fileName, String folderName) {
    try {
      // 1. 파일을 byte 배열로 읽기
      File file = new File(folderName, fileName);
      byte[] imageBytes = Files.readAllBytes(file.toPath());
      // 2. 이미지 타입 확인
      String contentType = "image/jpeg"; // 기본값
      if (fileName.endsWith(".png")) {
        contentType = "image/png";
      } else if (fileName.endsWith(".gif")) {
        contentType = "image/gif";
      }
      // 3. ResponseEntity로 반환
      return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(imageBytes);
    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/temp/{fileName}")
  public ResponseEntity<byte[]> getTempProfile(@PathVariable String fileName) {
    return readProfile(fileName, TBoardConstant.TEMP_FOLDER);
  }

  @GetMapping("/profile/{fileName}")
  public ResponseEntity<byte[]> getProfile(@PathVariable String fileName) {
    return readProfile(fileName, TBoardConstant.PROFILE_FOLDER);
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/api/member/profile")
  public ResponseEntity<String> changeProfile(@RequestParam String profile, Principal principal) {
    memberService.updateProfile(profile, principal.getName());
    return ResponseEntity.ok("프로필 사진을 변경했습니다");
  }
}
