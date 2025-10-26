package com.example.demo.controller;

import org.springframework.security.access.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {
  @Secured("ROLE_ADMIN")
  @GetMapping("/admin/main")
  public void main() {
  }
}
