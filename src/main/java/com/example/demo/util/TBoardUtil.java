package com.example.demo.util;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import org.springframework.http.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

public class TBoardUtil {
  public static PostDto.Page getPage(long pageno, long pagesize, long blocksize, long totalcount, List<Post> posts) {
    // 전체 페이지의 개수
    // 글개수:123, pagesize:10
    // 글개수/pagesize는 123/10 -> 12가 나온다 -> Math.ceil(12) -> 결국 12
    // 우리가 원하는 값은 Math.ceil(12.3) -> 13이 나와야한다
    //   123/10이 12.3이려면 (double) 필요

    // Math.ceil(12.3)의 결과는 13.0이다. 그래서 (int) 필요
    long numberOfPages = (long) (Math.ceil((double)totalcount/pagesize));

    // pageno    prev   ---> pageno/BLOCK_SIZE
    //  1~5        0         (1~4)/5->0, 5/5->1    그래서 (pageno-1)/5로 나눠주면 0*5 -> 0
    //  6~10       5                               (5~9)/5                결과는 1*5-> 5
    long prev = (pageno-1)/blocksize * blocksize;
    long start = prev + 1;
    long end = prev + blocksize;
    long next = end + 1;

    // 위처럼 계산하면 pageno가 11일때 end는 15, next는 16이다
    // 하지만 실제 페이지의 개수는 13이므로 end는 13, next는 0이 되도록 수정
    if(end>=numberOfPages) {
      end = numberOfPages;
      next = 0;
    }
    return new PostDto.Page(prev, start, end, next, pageno, posts);
  }

  private static final String PROFLILE_FOLDER = System.getProperty("user.dir") + File.separator + "upload"
      + File.separator + "profile" + File.separator;
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
