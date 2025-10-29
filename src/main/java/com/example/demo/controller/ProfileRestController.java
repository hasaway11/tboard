package com.example.demo.controller;

import com.example.demo.util.TBoardConstant;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
public class ProfileRestController {
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

  @GetMapping("/temp/{filename}")
  public ResponseEntity<byte[]> getTempProfile(@PathVariable String filename) {
    try {
      // 1. 파일을 byte 배열로 읽기
      File file = new File(TBoardConstant.TEMP_FOLDER, filename);
      byte[] imageBytes = Files.readAllBytes(file.toPath());

      // 2. 이미지 타입 확인
      String contentType = "image/jpeg"; // 기본값
      if (filename.endsWith(".png")) {
        contentType = "image/png";
      } else if (filename.endsWith(".gif")) {
        contentType = "image/gif";
      }

      // 3. ResponseEntity로 반환
      return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(imageBytes);

    } catch (IOException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
