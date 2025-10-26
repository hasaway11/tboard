package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.support.*;

import java.security.*;

@Controller
public class MemberController {
  @Autowired
  private MemberService memberService;

  @PreAuthorize("isAnonymous()")
  @GetMapping("/member/join")
  public void join() {
  }

  @GetMapping("/member/login")
  public ModelAndView login(HttpSession session) {
    ModelAndView mav = new ModelAndView("member/login");
    if(session.getAttribute("msg")!=null) {
      mav.addObject("msg", session.getAttribute("msg"));
      session.removeAttribute("msg");
    }
    return mav;
  }

  @PreAuthorize("isAnonymous()")
  @GetMapping("/member/find")
  public void find() {
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/member/check-password")
  public void checkPassword() {
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/member/change-password")
  public void changePassword() {
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/member/check-password")
  public ModelAndView checkPassword(@RequestParam(required=false)  @NotEmpty(message="비밀번호는 필수입력입니다") String password, Principal principal, RedirectAttributes ra, HttpSession session) {
    boolean checkSuccess = memberService.checkPassword(password, principal.getName());
    if(checkSuccess) {
      session.setAttribute("isPasswordCheck", true);
      return new ModelAndView("redirect:/member/read");
    } else {
      ra.addFlashAttribute("msg", "사용자를 확인하지 못했습니다");
      return new ModelAndView("redirect:/member/check-password");
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/member/read")
  public ModelAndView read(Principal principal, HttpSession session) {
    if(session.getAttribute("isPasswordCheck")==null)
      return new ModelAndView("redirect:/member/check-password");
    MemberDto.MemberResponse dto = memberService.read(principal.getName());
    return new ModelAndView("member/read").addObject("member", dto);
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/member/update-password")
  public ModelAndView updatePassword(@ModelAttribute @Valid MemberDto.PasswordChangeRequest dto, Principal principal) {
    memberService.updatePassword(dto, principal.getName());
    return new ModelAndView("redirect:/");
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/member/delete")
  public ModelAndView remove(Principal principal, HttpSession session) {
    memberService.remove(principal.getName());
    session.invalidate();
    return new ModelAndView("redirect:/");
  }
}
