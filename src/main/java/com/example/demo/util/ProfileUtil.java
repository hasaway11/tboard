package com.example.demo.util;

import org.springframework.http.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

public class ProfileUtil {
  private static final String PROFLILE_FOLDER = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "profile" + File.separator;
  private static final String PROFILE_NAME = "default.jpg";

  public static Optional<String> convertToBase64Profile(MultipartFile file) {
    try {
      // contentType는 파일의 형식. 예) image/jpg, image/png
      // 웹브라우저가 데이터의 종류를 모르면 저장 메뉴를 띄운다
      // - 데이터 앞에 파일 형식을 지정하면, 웹브라우저가 처리
      byte[] fileBytes =  file.getBytes();
      String base64EncodedImage = "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(fileBytes);
      return Optional.ofNullable(base64EncodedImage);
    } catch (IOException e) {
      System.err.println("WARN: 프로필 이미지 처리 중 오류 발생. 기본 이미지를 사용합니다: " + e.getMessage());
    }
    return Optional.empty();
  }

  public static String getDefaultBase64Profile()   {
    try {
      // 1. 폴더와 파일명으로 파일 객체를 생성
      File file = new File(PROFLILE_FOLDER, PROFILE_NAME);
      // 2. FileInputStream을 이용해 open한 파일을 byte로 읽어온다
      FileInputStream fis = new FileInputStream(file);
      byte[] fileBytes = fis.readAllBytes();
      // 3. base64로 리턴
      return "data:" + MediaType.IMAGE_JPEG_VALUE + ";base64," + Base64.getEncoder().encodeToString(fileBytes);
    } catch(IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
