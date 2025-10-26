package com.example.demo.service;

import com.example.demo.dao.*;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.exception.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PostService {
  @Autowired
  private PostDao postDao;
  @Autowired
  private CommentDao commentDao;
  @Autowired
  private PostMemberGoodDao postMemberGoodDao;
  private static final long POSTS_PER_PAGE = 10;
  private static final long PAGES_PER_BLOCK = 5;

  private PostDto.PageResponse toPageResponse(long pageno, long pagesize, long blocksize, long totalcount, List<PostDto.PostList> posts) {
    long numberOfPages = (totalcount-1)/pagesize;

    long prev = (pageno-1)/blocksize * blocksize;
    long start = prev + 1;
    long end = prev + blocksize;
    long next = end + 1;

    if(end>=numberOfPages) {
      end = numberOfPages;
      next = 0;
    }

    List<Long> pages = new ArrayList<>();
    for(long i=start; i<=end; i++)
      pages.add(i);
    return new PostDto.PageResponse(pageno, prev, next, pages, posts);
  }

  public PostDto.PageResponse list(long pageno, long pagesize) {
    long totalcount = postDao.count();
    List<PostDto.PostList> posts = postDao.findAll(pageno, pagesize);
    return toPageResponse(pageno, POSTS_PER_PAGE, PAGES_PER_BLOCK, totalcount, posts);
  }

  public PostDto.PostResponse read(long pno, String loginId) {
    PostDto.PostResponse post = postDao.findByPnoWithComments(pno).orElseThrow(PostNotFoundException::new);
    if(loginId!=null && !post.getWriter().equals(loginId)) {
      postDao.increaseReadCnt(pno);
      post.setReadCnt(post.getReadCnt()+1);
    }
    return post;
  }

  public long write(PostDto.CreateRequest dto, String loginId) {
    Post post = dto.toEntity(loginId);
    postDao.insert(post);
    return post.getPno();
  }

  public void update(PostDto.UpdateRequest dto, String loginId) {
    // 1. 글이 없으면 예외
    Post post = postDao.findByPno(dto.getPno()).orElseThrow(PostNotFoundException::new);
    // 2. 작업자가 글쓴이가 아니면 예외
    if(!post.getWriter().equals(loginId))
      throw new JobFailException("잘못된 작업입니다");
    postDao.updateByPno(dto);
  }

  @Transactional
  public void delete(long pno, String loginId) {
    Post post = postDao.findByPno(pno).orElseThrow(PostNotFoundException::new);
    if(!post.getWriter().equals(loginId))
      throw new JobFailException("잘못된 작업있니다");
    commentDao.deleteByPno(pno);
    postDao.deleteByPno(pno);
  }

  @Transactional
  public long good(long pno, String loginId) {
    // 비로그인이면 추천할 수 없다 -> @PreAuthrize()로 필터링되서 여기까지 안온다(X)
    // 1. 글이 없으면 예외처리
    // 2. 자기가 작성한 글이면 예외처리
    // 3. 이미 추천한 글이면 예외 처리
    // 4. 추천하지 않은 글이면 추천 후 새로운 추천수를 리턴
    Post post = postDao.findByPno(pno).orElseThrow(PostNotFoundException::new);
    if(post.getWriter().equals(loginId))
      throw new JobFailException("자신의 글은 추천할 수 없습니다");
    boolean isRecommended = postMemberGoodDao.existsByPnoAndUsername(pno, loginId);
    if(isRecommended)
      throw new JobFailException("이미 추천했습니다");
    postMemberGoodDao.save(pno, loginId);
    postDao.increaseGoodCntByPno(pno);
    return postDao.findGoodCntByPno(pno).get();
  }
}
