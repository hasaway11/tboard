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
    File uploadFolder = new File(TBoardConstant.UPLOAD_FOLDER);
    File tempFolder = new File(TBoardConstant.TEMP_FOLDER);
    File profileFolder = new File(TBoardConstant.PROFILE_FOLDER);
    if(uploadFolder.exists()==false)
      uploadFolder.mkdir();
    if(tempFolder.exists()==false)
      tempFolder.mkdir();
    if(profileFolder.exists()==false)
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
      File file = new File(folderName, fileName);
      byte[] imageBytes = Files.readAllBytes(file.toPath());
      MediaType mediaType = ProfileUtil.getMediaType(fileName);
      return ResponseEntity.ok().contentType(mediaType).body(imageBytes);
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
