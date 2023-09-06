package com.example.auth.service;


import ch.qos.logback.core.boolex.EvaluationException;
import com.example.auth.entity.*;
import com.example.auth.exceptions.UserExistingWithEmail;
import com.example.auth.exceptions.UserExistingWithName;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public String generateToken(String username, int exp){
        return jwtService.generateToken(username, exp);
    }
    public void validateToken(HttpServletRequest request, HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException {
        String token = null;
        String refresh = null;
        for(Cookie value : Arrays.stream(request.getCookies()).toList()) {
            if (value.getName().equals("Authorization")) {
                token = value.getValue();
            } else if (value.getName().equals("refresh")) {
                refresh = value.getValue();
            }
        }
        try {
            jwtService.validateToken(token);
        } catch(IllegalArgumentException | ExpiredJwtException e){
            jwtService.validateToken(refresh);
            Cookie refreshCookie = cookieService.generateCookie("refresh", jwtService.refreshToken(refresh, refreshExp), refreshExp);
            Cookie cookie = cookieService.generateCookie("Authorization", jwtService.refreshToken(refresh, exp), exp);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        }


         jwtService.validateToken(token);
    }
    public void register(UserRegisterDto userRegisterDto) throws UserExistingWithName, UserExistingWithEmail {
         userRepository.findUserByLogin(userRegisterDto.getLogin())
                 .ifPresent(value -> {
                     throw new UserExistingWithName("Użytkownik o podanej nazwie już istnieje");
                 });
        userRepository.findUserByEmail(userRegisterDto.getEmail())
                .ifPresent(value -> {
                    throw new UserExistingWithEmail("Użytkownik o podanym adresie email już istnieje");
                });

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
         if(user != null) {
             Authentication authenticate = authenticationManager
                     .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                                                                           authRequest.getPassword()));
             if(authenticate.isAuthenticated()) {
                 Cookie cookie = cookieService.generateCookie("Authorization", generateToken(authRequest.getUsername(), exp),exp);
                 Cookie refresh = cookieService.generateCookie("refresh", generateToken(authRequest.getUsername(), refreshExp), refreshExp);
                 response.addCookie(cookie);
                 response.addCookie(refresh);
                 return ResponseEntity.ok(
                         UserRegisterDto
                                 .builder()
                                 .login(user.getUsername())
                                 .email(user.getEmail())
                                 .role(user.getRole())
                                 .build());
             } else {
                 return ResponseEntity.ok(new AuthResponse(Code.LOGIN_FAILED));
             }

         }
         return ResponseEntity.ok(new AuthResponse(Code.USER_NOT_EXIST));
    }

}
