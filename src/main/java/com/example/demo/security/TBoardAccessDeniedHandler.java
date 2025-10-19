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
    request.setAttribute("msg", "작업을 수행할 수 없습니다");
    RequestDispatcher dispatcher = request.getRequestDispatcher("/error");
    dispatcher.forward(request, response);
  }
}
