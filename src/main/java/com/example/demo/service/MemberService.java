package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.JobFailException;
import com.example.demo.exception.MemberNotFoundException;
import com.example.demo.util.*;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.mail.javamail.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.multipart.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class MemberService {
  @Autowired
  private MemberDao memberDao;
  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  private MailUtil mailUtil;

  public boolean checkUsername(MemberDto.UsernameCheckRequest dto) {
    return !memberDao.existsByUsername(dto.getUsername());
  }

  public void join(MemberDto.CreateRequest dto) {
    boolean 기본프사_사용 = false;
    String profile = dto.getProfile();
    File source = null;

    if(profile==null || profile.isEmpty()) {
      기본프사_사용 = true;
    } else {
      source = new File(TBoardConstant.TEMP_FOLDER, profile);
      if (source.exists() == false) {
        기본프사_사용 = true;
      }
    }

    if(기본프사_사용) {
      profile = "default.webp";
      source = new File(TBoardConstant.PROFILE_FOLDER, profile);
    }

    int 점의_위치 = profile.lastIndexOf(".");
    String 확장자 = profile.substring(점의_위치);
    String 저장_파일명 = dto.getUsername() + 확장자;
    File dest = new File(TBoardConstant.PROFILE_FOLDER, 저장_파일명);

    try {
      FileCopyUtils.copy(source, dest);
      if(기본프사_사용==false) {
        source.delete();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    String encodedPassword = encoder.encode(dto.getPassword());
    Member member = dto.toEntity(encodedPassword, 저장_파일명);
    memberDao.insert(member);
  }

  public void updateProfile(String profile, String loginId) {
    File origin = new File(TBoardConstant.TEMP_FOLDER, profile);
    String ext = profile.substring(profile.lastIndexOf("."));
    String profileName = loginId + ext;
    File dest = new File(TBoardConstant.PROFILE_FOLDER, profileName);
    String currentProfileName = memberDao.findProfileByUsername(loginId);

    try {
      Files.move(origin.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    memberDao.updateProfileByUsername(profileName, loginId);
  }


  public Optional<String> searchUsername(String email) {
    return memberDao.findUsernameByEmail(email);
  }

  public void resetPassword(MemberDto.ResetPasswordRequest dto) {
    Member member = memberDao.findByUsername(dto.getUsername()).orElseThrow(MemberNotFoundException::new);
    String newPassword = RandomStringUtils.secure().nextAlphanumeric(10);
    memberDao.updatePasswordByUsername(dto.getUsername(), encoder.encode(newPassword));
    String html = "<p>아래 임시비밀번호로 로그인하세요</p>";
    html+= "<p>" + newPassword  + "</p>";
    mailUtil.sendMail("admin@icia.com", member.getEmail(), "임시비밀번호", html);
  }

  public MemberDto.MemberResponse read(String loginId) {
    return memberDao.findByUsername(loginId).orElseThrow(MemberNotFoundException::new).toDto();
  }

  public boolean updatePassword(MemberDto.PasswordChangeRequest dto, String loginId) {
    String encodedPassword = memberDao.findPasswordByUsername(loginId);
    if(!encoder.matches(dto.getCurrentPassword(), encodedPassword))
      return false;
    return memberDao.updatePasswordByUsername(loginId, encoder.encode(dto.getNewPassword()))==1;
  }

  public void remove(String loginId) {
    boolean isRemove = memberDao.deleteByUsername(loginId)==1;
    if(!isRemove)
      throw new JobFailException("탈퇴할 수 없습니다");
  }

  public boolean checkPassword(String password, String loginId) {
    String encodedPassword = memberDao.findPasswordByUsername(loginId);
    return encoder.matches(password, encodedPassword);
  }
}
