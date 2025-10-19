package com.example.demo.advice;

import com.example.demo.exception.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

@ControllerAdvice
public class TBoardControllerAdvice {
  @ExceptionHandler(MemberNotFoundException.class)
  public Object handleMemberNotFoundException(MemberNotFoundException e, HttpServletRequest req) {
    String requestUri = req.getRequestURI();
    if (requestUri.startsWith("/api/")) {
      return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다");
    } else {
      return new ModelAndView("redirect:/").addObject("errorMessage", "사용자를 찾을 수 없습니다");
    }
  }

  @ExceptionHandler(PostNotFoundException.class)
  public Object handlePostNotFoundException(PostNotFoundException e, HttpServletRequest req) {
    String requestUri = req.getRequestURI();
    if (requestUri.startsWith("/api/")) {
      return ResponseEntity.status(404).body("글을 찾을 수 없습니다");
    } else {
      return new ModelAndView("redirect:/").addObject("errorMessage", "글을 찾을 수 없습니다");
    }
  }

  @ExceptionHandler(JobFailException.class)
  public Object handleJobFailException(JobFailException e, HttpServletRequest req) {
    String requestUri = req.getRequestURI();
    if (requestUri.startsWith("/api/")) {
      return ResponseEntity.status(409).body(e.getMessage());
    } else {
      return new ModelAndView("redirect:/").addObject("errorMessage", e.getMessage());
    }
  }
}
