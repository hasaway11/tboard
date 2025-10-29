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
  private JavaMailSender mailSender;
  private String defaultProfile;

  @PostConstruct
  public void init() {
    defaultProfile = ProfileUtil.getDefaultBase64Profile();
  }


  public boolean checkUsername(MemberDto.UsernameCheckRequest dto) {
    return !memberDao.existsByUsername(dto.getUsername());
  }

  public void sendMail(String from, String to, String title, String text) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(title);
      helper.setText(text, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  public void join(MemberDto.CreateRequest dto) {
    String uploadProfileName = dto.getProfile();
    String profileName = "";
    if(!dto.getProfile().equals("")) {
      File origin = new File(TBoardConstant.TEMP_FOLDER, uploadProfileName);
      String ext = uploadProfileName.substring(uploadProfileName.lastIndexOf("."));
      profileName = dto.getUsername() + ext;
      File dest = new File(TBoardConstant.PROFILE_FOLDER, profileName);
      try {
        Files.move(origin.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    String encodedPassword = encoder.encode(dto.getPassword());
    Member member = dto.toEntity(encodedPassword, profileName);
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
    sendMail("admin@icia.com", member.getEmail(), "임시비밀번호", html);
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
