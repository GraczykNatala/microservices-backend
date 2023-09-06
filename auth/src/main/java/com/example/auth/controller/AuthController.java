package com.example.auth.controller;

import com.example.auth.entity.*;
import com.example.auth.exceptions.UserExistingWithEmail;
import com.example.auth.exceptions.UserExistingWithName;
import com.example.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
        public ResponseEntity<AuthResponse> addNewUser(@Valid @RequestBody UserRegisterDto user){

        try{
            userService.register(user);
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        } catch(UserExistingWithName e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(Code.USERNAME_ALREADY_EXIST));
        } catch(UserExistingWithEmail e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(Code.EMAIL_ALREADY_EXIST));
        }
        }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> addNewUser(@RequestBody User user, HttpServletResponse response){
        return userService.login(response, user);
    }
    @RequestMapping(path = "/validate", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request, HttpServletResponse response) {
    try {
        userService.validateToken(request, response);
        return  ResponseEntity.ok(new AuthResponse(Code.PERMIT));
    } catch(IllegalArgumentException | ExpiredJwtException e){
        return ResponseEntity.status(401).body(new AuthResponse(Code.BAD_TOKEN));
    }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationMessage handleValidationExceptions(
            MethodArgumentNotValidException ex
    ){
        return new ValidationMessage(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}

