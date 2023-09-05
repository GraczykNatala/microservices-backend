package com.example.auth.service;


import com.example.auth.entity.*;
import com.example.auth.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CookieService cookieService;


    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;


    private final AuthenticationManager authenticationManager;
     private User saveUser(User user) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    public String generateToken(String username){
        return jwtService.generateToken(username);
    }
    public void validateToken(String token){
         jwtService.validateToken(token);
    }
    public void register(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setLogin(userRegisterDto.getLogin());
        user.setPassword(userRegisterDto.getPassword());
        user.setEmail(userRegisterDto.getEmail());
        if(userRegisterDto.getRole() != null) {
            user.setRole(userRegisterDto.getRole());
        } else {
            user.setRole(Role.USER);
        }
        saveUser(user);
    }

    public ResponseEntity<?> login(HttpServletResponse response,  User authRequest) {
         User user = userRepository.findUserByLogin(authRequest.getUsername()).orElse(null);
         if(user !=null) {
             Authentication authenticate = authenticationManager
                     .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                                                           authRequest.getPassword()));
             if(authenticate.isAuthenticated()) {
                 Cookie cookie = cookieService.generateCookie("token", generateToken(authRequest.getUsername()),exp);
                 Cookie refresh = cookieService.generateCookie("refresh", generateToken(authRequest.getUsername()),refreshExp);
                 response.addCookie(cookie);
                 response.addCookie(refresh);
                 return ResponseEntity.ok(
                         UserRegisterDto
                                 .builder()
                                 .login(user.getUsername())
                                 .email(user.getEmail())
                                 .build());
             } else {
                 return ResponseEntity.ok(new AuthResponse(Code.LOGIN_FAILED));
             }

         }
         return ResponseEntity.ok(new AuthResponse(Code.USER_NOT_EXIST));
    }

}
