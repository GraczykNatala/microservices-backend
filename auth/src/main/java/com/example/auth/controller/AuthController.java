package com.example.auth.controller;

import com.example.auth.entity.AuthResponse;
import com.example.auth.entity.Code;
import com.example.auth.entity.UserRegisterDto;
import com.example.auth.service.UserService;
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
}
