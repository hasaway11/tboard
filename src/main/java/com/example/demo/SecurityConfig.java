package com.example.demo;

import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.access.*;
import org.springframework.security.web.authentication.*;

@EnableMethodSecurity(securedEnabled=true)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
  private final AccessDeniedHandler accessDeniedHandler;
  private final AuthenticationEntryPoint authenticationEntryPoint;
  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity config) throws Exception {
    config.csrf(csrf-> csrf.disable());
    config.formLogin(form->form.loginPage("/member/login").loginProcessingUrl("/member/login").successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler));
    config.logout(logout-> logout.logoutUrl("/member/logout").logoutSuccessUrl("/"));
    config.exceptionHandling(handler->handler.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint));
    return config.build();
  }
}
