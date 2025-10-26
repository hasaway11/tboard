package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.*;

import java.io.*;

@Component
public class TBoardAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    String requestUri = request.getRequestURI();
    if (requestUri.startsWith("/api/")) {
      response.setStatus(401);
    } else {
      response.sendRedirect("/member/login");
    }
  }
}
