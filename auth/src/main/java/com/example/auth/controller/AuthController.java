package com.example.auth.controller;

import com.example.auth.entity.*;
import com.example.auth.entity.enums.Code;
import com.example.auth.entity.responseAndData.*;
import com.example.auth.exceptions.UserDontExistException;
import com.example.auth.exceptions.UserExistingWithEmail;
import com.example.auth.exceptions.UserExistingWithName;
import com.example.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @RequestMapping(path = "/register", method = RequestMethod.POST)
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserRegisterData user){
        try{
            log.info("--START REGISTER");
            userService.register(user);
            log.info("--STOP REGISTER");
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        } catch(UserExistingWithName e){
            log.warn("Username don't exist in database");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(Code.USERNAME_ALREADY_EXIST));
        } catch(UserExistingWithEmail e) {
            log.warn("Email don't exist in database");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(Code.EMAIL_ALREADY_EXIST));
        }
        }


    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response){
        log.info("--START LOGIN");
        return userService.login(response,user);
    }
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public ResponseEntity<?> logout( HttpServletResponse response,HttpServletRequest request){
        log.info("--START LOGOUT");
        return userService.logout(request, response);
    }


    @RequestMapping(path = "/auto-login", method = RequestMethod.GET)
    public ResponseEntity<?> autoLogin(HttpServletResponse response, HttpServletRequest request){
        log.info("--START AUTO LOGIN");
        return userService.loginByToken(request, response);
    }
    @RequestMapping(path = "/logged-in",method = RequestMethod.GET)
    public ResponseEntity<?> loggedIn(HttpServletResponse response,HttpServletRequest request){
        log.info("--CHECK USER LOGGED IN");
        return userService.loggedIn(request,response);
    }

    @RequestMapping(path = "/validate",method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> validateToken(HttpServletRequest request, HttpServletResponse response) {
        try{
            log.info("--START VALIDATE TOKEN");
            userService.validateToken(request,response);
            log.info("--STOP VALIDATE TOKEN");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT));
        }catch (IllegalArgumentException | ExpiredJwtException e){
            log.warn("Wrong token");
            return ResponseEntity.status(401).body(new AuthResponse(Code.BAD_TOKEN));
        }
    }

    @RequestMapping(path = "/authorize",method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> authorize(HttpServletRequest request, HttpServletResponse response) {
        try{
            log.info("--START AUTHORIZE");
            userService.validateToken(request,response);
            userService.authorize(request);
            log.info("--STOP AUTHORIZE");
            return ResponseEntity.ok(new AuthResponse(Code.PERMIT));
        } catch (IllegalArgumentException | ExpiredJwtException e){
            log.warn("Wrong token");
            return ResponseEntity.status(401).body(new AuthResponse(Code.BAD_TOKEN));
        } catch (UserDontExistException e){
            log.warn("User do not exist");
            return ResponseEntity.status(401).body(new AuthResponse(Code.USER_NOT_EXIST));
        }
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationMessage handleValidationExceptions(
            MethodArgumentNotValidException ex
    ){
        return new ValidationMessage(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }


    @RequestMapping(path = "/activate", method = RequestMethod.GET)
    public ResponseEntity<AuthResponse> activateUser(@RequestParam String uid){
        try{
            log.info("--START ACTIVATE USER");
            userService.activateUser(uid);
            log.info("--STOP ACTIVATE USER");
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        } catch(UserDontExistException e){
            log.warn("Email don't exist in database");
            return ResponseEntity.status(400).body(new AuthResponse(Code.USER_NOT_EXIST));
        }
    }
    @RequestMapping(path = "/reset-password", method = RequestMethod.POST)
    public ResponseEntity<AuthResponse> sendMailRecovery(@RequestBody ResetPasswordData resetPasswordData){
        try {
            log.info("--START SEND E-MAIL");
            userService.recoveryPassword(resetPasswordData.getEmail());
            log.info("--STOP SEND E-MAIL");
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        } catch(UserDontExistException e){
            log.warn("Email don't exist in database");
            return  ResponseEntity.status(400).body(new AuthResponse(Code.USER_NOT_EXIST));
        }
    }
    @RequestMapping(path = "/reset-password", method = RequestMethod.PATCH)
    public ResponseEntity<AuthResponse> recoveryMail(@RequestBody ChangePasswordData changePasswordData){
        try {
            log.info("--START RESET PASSWORD");
            userService.resetPassword(changePasswordData);
            log.info("--STOP RESET PASSWORD");
            return ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
        } catch(UserDontExistException e){
            log.warn("Email don't exist in database");
            return  ResponseEntity.status(400).body(new AuthResponse(Code.USER_NOT_EXIST));
        }
    }
}

