package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.access.*;
import org.springframework.security.web.access.*;
import org.springframework.stereotype.*;

import java.io.*;

// 403(권한 없음)을 처리하는 자바 코드
@Component
public class TBoardAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    HttpSession session = request.getSession();
    session.setAttribute("msg", "작업 권한이 없습니다");
    response.sendRedirect("/member/login");
  }
}
