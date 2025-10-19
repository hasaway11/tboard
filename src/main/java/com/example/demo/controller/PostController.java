package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import java.security.*;

@Validated
@Controller
public class PostController {
  @Autowired
  private PostService postService;

  @GetMapping("/")
  public ModelAndView list(@RequestParam(defaultValue="1") Long pageno, @RequestParam(defaultValue="10") Long pagesize, HttpSession session) {
    PostDto.Page page = postService.list(pageno, pagesize);
    ModelAndView mav = new ModelAndView("post/list").addObject("page", page);
    if(session.getAttribute("msg")!=null) {
      mav.addObject("msg", session.getAttribute("msg"));
      session.removeAttribute("msg");
    }
    return mav;
  }

  @Secured("ROLE_USER")
  @GetMapping("/post/write")
  public void write() { }

  @Secured("ROLE_USER")
  @PostMapping("/post/write")
  public ModelAndView write(@ModelAttribute @Valid PostDto.Write dto, Principal principal) {
    long bno = postService.write(dto, principal.getName());
    return new ModelAndView("redirect:/post/read?bno=" + bno);
  }

  @GetMapping("/post/read")
  public ModelAndView read(@RequestParam @NotNull(message="글번호는 필수입력입니다") Long pno, Principal principal) {
    String loginId = principal==null? null : principal.getName();
    PostDto.Read post =  postService.read(pno, loginId);
    return new ModelAndView("post/read").addObject("post", post);
  }

  @GetMapping("/post/update")
  public ModelAndView update(@RequestParam @NotNull(message="글번호는 필수입력입니다") Long pno) {
    PostDto.Read post =  postService.read(pno, null);
    return new ModelAndView("post/update").addObject("post", post);
  }

  @Validated
  @Secured("ROLE_USER")
  @DeleteMapping("/post/delete")
  public ModelAndView delete(@RequestParam(required=false) @NotNull(message="글번호는 필수입력입니다") Integer pno, Principal principal) {
    postService.delete(pno, principal.getName());
    return new ModelAndView("redirect:/");
  }

  @Secured("ROLE_USER")
  @PutMapping("/api/post")
  public ResponseEntity<Void> update(@ModelAttribute @Valid PostDto.Update dto, Principal principal) {
    postService.update(dto, principal.getName());
    return ResponseEntity.ok(null);
  }

  @Secured("ROLE_USER")
  @PutMapping("/api/post/good")
  public ResponseEntity<Long> good(@RequestParam(required=false) @NotNull(message="글번호는 필수입력입니다") Integer pno, Principal principal) {
    long newGoodCnt = postService.good(pno, principal.getName());
    return ResponseEntity.ok(newGoodCnt);
  }
}
