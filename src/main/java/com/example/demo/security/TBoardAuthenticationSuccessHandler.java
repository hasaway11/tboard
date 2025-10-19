package com.example.demo.security;

import com.example.demo.dao.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.savedrequest.*;
import org.springframework.stereotype.*;

import java.io.*;

@Component
public class TBoardAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  @Autowired
  private MemberDao memberDao;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    String username = authentication.getName();
    memberDao.resetFailedAttemptsByUsername(username);

    // RequestCache : 사용자가 가려던 목적지를 저장하는 인터페이스
    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
    RedirectStrategy rs = new DefaultRedirectStrategy();
    if(savedRequest!=null)
      rs.sendRedirect(request, response, savedRequest.getRedirectUrl());
    else
      rs.sendRedirect(request, response, "/");
  }
}








