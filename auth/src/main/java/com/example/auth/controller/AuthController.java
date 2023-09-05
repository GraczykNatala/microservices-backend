package com.example.auth.controller;

import com.example.auth.entity.AuthResponse;
import com.example.auth.entity.Code;
import com.example.auth.entity.User;
import com.example.auth.entity.UserRegisterDto;
import com.example.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
        public ResponseEntity<AuthResponse> addNewUser(@RequestBody UserRegisterDto user){
            userService.register(user);
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> addNewUser(@RequestBody User user, HttpServletResponse response){
        return userService.login(response, user);
    }
    @RequestMapping(path = "/validate", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request) {
    try {
        userService.validateToken(request);
        return  ResponseEntity.ok(new AuthResponse(Code.PERMIT));
    } catch(IllegalArgumentException | ExpiredJwtException e){
        return ResponseEntity.status(401).body(new AuthResponse(Code.BAD_TOKEN));
    }
    }

}
