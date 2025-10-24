package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.security.*;
import java.util.*;

@Secured("ROLE_USER")
@RestController
public class CommentController {
  @Autowired
  private CommentService service;

  @PostMapping("/api/comments/new")
  public ResponseEntity<List<Comment>> write(@Valid CommentDto.CreateRequest dto, Principal principal) {
    return ResponseEntity.ok(service.write(dto, principal.getName()));
  }

  @DeleteMapping("/api/comments")
  public ResponseEntity<List<Comment>> delete(@Valid CommentDto.DeleteRequest dto, Principal principal) {
    List<Comment> comments =  service.delete(dto, principal.getName());
    return ResponseEntity.ok(comments);
  }
}
