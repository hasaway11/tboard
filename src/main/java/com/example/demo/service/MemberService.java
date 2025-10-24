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
import org.springframework.stereotype.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.*;

import java.io.*;
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
    defaultProfile = TBoardUtil.getDefaultBase64Profile();
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
    // 1. Guard Clause: 아이디 중복 확인 및 즉시 종료
    if(memberDao.existsByUsername(dto.getUsername()))
      throw new JobFailException("가입처리가 불가능한 아이디입니다");

    // 2. 비밀번호 인코딩
    String encodedPassword = encoder.encode(dto.getPassword());

    // 3. 프로필 이미지 Base64 처리
    MultipartFile profile = dto.getProfile();
    // 프로필 파일이 유효한지 확인
    boolean hasProfileImage = profile != null && !profile.isEmpty();
    String base64EncodedImage = defaultProfile;
    if(hasProfileImage)
      base64EncodedImage = TBoardUtil.convertToBase64Profile(profile).orElse(defaultProfile);

    // 4. 엔티티 생성 및 저장
    Member member = dto.toEntity(encodedPassword, base64EncodedImage);
    memberDao.insert(member);
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
    return memberDao.findByUsername(loginId).orElseThrow(MemberNotFoundException::new).toRead();
  }

  public void updateProfile(MultipartFile profile, String loginId) {
    String base64EncodedImage = TBoardUtil.convertToBase64Profile(profile).orElseThrow(()->new JobFailException("프로필 이미지 처리 중 오류 발생"));
    memberDao.updateProfileByUsername(base64EncodedImage, loginId);
  }

  public void activate(String code) {
    boolean isActive = memberDao.activateAccountByCode(code)==1;
    if(!isActive)
      throw new JobFailException("이미 활성화되었거나 잘못된 확인 코드입니다");
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
