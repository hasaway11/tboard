package com.example.demo.dto;

import com.example.demo.entity.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

// PostDto는 Dto들을 담는 클래스다 -> Dto 클래스 개수 줄여 PostDto.Pages, PostDto.Create....
public class PostDto {
  @Data
  public static class PostList {
    private long pno;
    private String title;
    private String writer;
    private String writeTime;
    private long readCnt;
  }

  // 페이징 출력 DTO
  @Data
  @AllArgsConstructor
  public static class PageResponse {
    private long pageno;
    private long prev;
    private long next;
    private List<Long> pages;
    private List<PostList> posts;
  }

  // 글을 작성하는 DTO
  @Data
  public static class CreateRequest {
    @NotEmpty(message="제목을 입력하세요")
    private String title;
    @NotEmpty(message="내용을 입력하세요")
    private String content;

    public Post toEntity(String loginId) {
      return Post.builder().title(title).content(content).writer(loginId).build();
    }
  }

  // 글 변경 DTO
  @Data
  public static class UpdateRequest {
    @NotNull(message="글번호가 없습니다")
    private Long pno;
    @NotEmpty(message="제목을 입력하세요")
    private String title;
    @NotEmpty(message="내용을 입력하세요")
    private String content;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PostResponse {
    private long pno;
    private String title;
    private String content;
    private String writer;
    private String writeTime;
    private long readCnt;
    private long goodCnt;
    private long badCnt;
    private List<CommentDto.CommentResponse> comments;
  }
}
