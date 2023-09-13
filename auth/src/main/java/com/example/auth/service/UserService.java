package com.example.auth.service;

import com.example.auth.entity.ResetOperations;
import com.example.auth.entity.User;
import com.example.auth.entity.enums.Code;
import com.example.auth.entity.enums.Role;
import com.example.auth.entity.responseAndData.AuthResponse;
import com.example.auth.entity.responseAndData.ChangePasswordData;
import com.example.auth.entity.responseAndData.LoginResponse;
import com.example.auth.entity.responseAndData.UserRegisterData;
import com.example.auth.exceptions.UserDontExistException;
import com.example.auth.exceptions.UserExistingWithEmail;
import com.example.auth.exceptions.UserExistingWithName;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;


public interface UserService {
    ResponseEntity<?> loginByToken(HttpServletRequest request, HttpServletResponse response);

    void activateUser(String uid) throws UserDontExistException;

    void recoveryPassword(String email) throws UserDontExistException;

    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<LoginResponse> loggedIn(HttpServletRequest request, HttpServletResponse response);

    String generateToken(String username, int exp);
    void validateToken(HttpServletRequest request,HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException;

    void register(UserRegisterData userRegisterDto) throws UserExistingWithName, UserExistingWithEmail;

    ResponseEntity<?> login(HttpServletResponse response, User authRequest);
    void resetPassword(ChangePasswordData changePasswordData) throws UserDontExistException;
}


