package com.example.demo.security;

import com.example.demo.dao.*;
import com.example.demo.entity.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Component
public class TBoardAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Autowired
  private MemberDao memberDao;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    HttpSession session = request.getSession();

    // 로그인에 실패하는 2가지 경우를 if문으로 처리
    // BadCredentialException : 아이디나 비밀번호를 찾지 못해 로그인 실패
    // DisabledException : 비활성화된 계정이라 로그인 실패
    if(e instanceof BadCredentialsException) {
      String username = request.getParameter("username");
      try {
        Member member =  memberDao.findByUsername(username).get();

        // 로그인 실패횟수가 3회이하라면 실패횟수를 1증가, 로그인 실패횟수가 4회이상이라면 실패횟수를 증가하고 비활성화한다
        if(member.getFailedAttempts()<4) {
          memberDao.increaseFailedAttemptsByUsername(member.getUsername());
          String msg = "로그인에 " + (member.getFailedAttempts()+1) + "회 실패했습니다. 5회 실패 시 계정이 비활성화됩니다";
          session.setAttribute("msg", msg);
        } else {
          memberDao.increaseFailedAttemptsByUsername(member.getUsername());
          memberDao.lockAccountByUsername(member.getUsername());
          session.setAttribute("msg", "로그인에 5회 실패해 계정이 비활성화되었습니다. 관리자에게 문의하세요");
        }
      } catch(NoSuchElementException e1) {
        session.setAttribute("msg", "아이디 또는 비밀번호를 잘못 입력했습니다");
      }
    } else if(e instanceof DisabledException) {
      session.setAttribute("msg", "비활성화된 계정입니다. 관리자에게 문의하세요");
    }
    response.sendRedirect("/member/login");
  }
}
