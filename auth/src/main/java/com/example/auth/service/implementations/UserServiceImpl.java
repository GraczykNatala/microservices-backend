package com.example.auth.service.implementations;


import com.example.auth.entity.*;
import com.example.auth.entity.enums.Code;
import com.example.auth.entity.enums.Role;
import com.example.auth.entity.responseAndData.AuthResponse;
import com.example.auth.entity.responseAndData.ChangePasswordData;
import com.example.auth.entity.responseAndData.LoginResponse;
import com.example.auth.entity.responseAndData.UserRegisterData;
import com.example.auth.exceptions.UserDontExistException;
import com.example.auth.exceptions.UserExistingWithEmail;
import com.example.auth.exceptions.UserExistingWithName;
import com.example.auth.repository.ResetOperationsRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.example.auth.utils.SecurityUtils.AUTH_TOKEN;
import static com.example.auth.utils.SecurityUtils.REFRESH_TOKEN;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetOperationService resetOperationService;
    private final ResetOperationsRepository resetOperationsRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CookieService cookieService;


    @Value("${jwt.exp}")
    private int exp;
    @Value("${jwt.refresh.exp}")
    private int refreshExp;



    public ResponseEntity<?> loginByToken(HttpServletRequest request, HttpServletResponse response){
        try {
            validateToken(request, response);
            String refresh = null;
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals(REFRESH_TOKEN)) {
                    refresh = value.getValue();
                }
            }
            String login = jwtService.getSubject(refresh);
            User user = userRepository.findUserByLoginAndLockAndEnabled(login).orElse(null);
            if (user != null){
                return ResponseEntity.ok(
                        UserRegisterData
                                .builder()
                                .login(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build());
            }
            log.warn("Login failed, user not exist");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.USER_NOT_EXIST));
        }catch (ExpiredJwtException|IllegalArgumentException e){
            log.warn("Login failed, token expired or null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.BAD_TOKEN));
        }
    }

    public void activateUser(String uid) throws UserDontExistException {
        User user = userRepository.findUserByUuid(uid).orElse(null);
        if (user != null){
            user.setLock(false);
            user.setEnabled(true);
            userRepository.save(user);
            return;
        }
        log.warn("User do not exist");
    throw new UserDontExistException("User do not exist");
    }

    public void recoveryPassword(String email) throws UserDontExistException {
        User user = userRepository.findUserByEmail(email).orElse(null);
        if (user != null){
            ResetOperations resetOperations = resetOperationService.initResetOperation(user);
            emailService.sendPasswordRecovery(user, resetOperations.getUid());
            return;
        }
        log.warn("User do not exist");
        throw new UserDontExistException("User do not exist");
    }

    private User saveUser(User user) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        log.info("Delete cookies");
        Cookie cookie = cookieService.removeCookie(request.getCookies(),AUTH_TOKEN);
        if (cookie != null){
            response.addCookie(cookie);
        }
        cookie = cookieService.removeCookie(request.getCookies(), REFRESH_TOKEN);
        if (cookie != null){
            response.addCookie(cookie);
        }
        return  ResponseEntity.ok(new AuthResponse(Code.SUCCESS));
    }

    public ResponseEntity<LoginResponse> loggedIn(HttpServletRequest request, HttpServletResponse response){
        try{
            validateToken(request, response);
            return ResponseEntity.ok(new LoginResponse(true));
        }catch (ExpiredJwtException|IllegalArgumentException e){
            return ResponseEntity.ok(new LoginResponse(false));
        }
    }


    public String generateToken(String username, int exp){
        return jwtService.generateToken(username, exp);
    }
    public void validateToken(HttpServletRequest request,HttpServletResponse response) throws ExpiredJwtException, IllegalArgumentException{
        String token = null;
        String refresh = null;
        if (request.getCookies() != null){
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals(AUTH_TOKEN)) {
                    token = value.getValue();
                } else if (value.getName().equals(REFRESH_TOKEN)) {
                    refresh = value.getValue();
                }
            }
        }else {
            log.info("login failed, empty token");
            throw new IllegalArgumentException("Token can't be null");
        }
        try {
            jwtService.validateToken(token);
        }catch (IllegalArgumentException | ExpiredJwtException e){
            jwtService.validateToken(refresh);
            Cookie refreshCokkie = cookieService.generateCookie(REFRESH_TOKEN, jwtService.refreshToken(refresh,refreshExp), refreshExp);
            Cookie cookie = cookieService.generateCookie(AUTH_TOKEN, jwtService.refreshToken(refresh,exp), exp);
            response.addCookie(cookie);
            response.addCookie(refreshCokkie);
        }

    }
    public void register(UserRegisterData userRegisterDto) throws UserExistingWithName, UserExistingWithEmail {
         userRepository.findUserByLogin(userRegisterDto.getLogin())
                 .ifPresent(value -> {
                     log.warn("Username is taken");
                     throw new UserExistingWithName("Użytkownik o podanej nazwie już istnieje");
                 });
        userRepository.findUserByEmail(userRegisterDto.getEmail())
                .ifPresent(value -> {
                    log.warn("Email is taken");
                    throw new UserExistingWithEmail("Użytkownik o podanym adresie email już istnieje");
                });

        User user = new User();
        user.setLock(true);
        user.setLogin(userRegisterDto.getLogin());
        user.setPassword(userRegisterDto.getPassword());
        user.setEmail(userRegisterDto.getEmail());
        if(userRegisterDto.getRole() != null) {
            user.setRole(userRegisterDto.getRole());
        } else {
            user.setRole(Role.USER);
        }
        saveUser(user);
        emailService.sendActivation(user);
    }

    public ResponseEntity<?> login(HttpServletResponse response, User authRequest) {
        log.info("--START LoginService");
        User user = userRepository.findUserByLoginAndLockAndEnabled(authRequest.getUsername()).orElse(null);
        if (user != null) {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                Cookie refresh = cookieService.generateCookie(REFRESH_TOKEN, generateToken(authRequest.getUsername(),refreshExp), refreshExp);
                Cookie cookie = cookieService.generateCookie(AUTH_TOKEN, generateToken(authRequest.getUsername(), exp), exp);
                response.addCookie(cookie);
                response.addCookie(refresh);
                return ResponseEntity.ok(
                        UserRegisterData
                                .builder()
                                .login(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build());
            } else {
                log.warn("Login Failed,WRONG DATA");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.WRONG_DATA));            }
        }
        log.warn("Login Failed, User do not exist");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(Code.LOGIN_FAILED));
    }


    public void resetPassword(ChangePasswordData changePasswordData) throws UserDontExistException{
        ResetOperations resetOperations = resetOperationsRepository.findByUid(changePasswordData.getUid()).orElse(null);
        if (resetOperations != null){
            User user = userRepository.findUserByUuid(resetOperations.getUser().getUuid()).orElse(null);

            if (user != null){
                user.setPassword(changePasswordData.getPassword());
                saveUser(user);
                resetOperationService.endOperation(resetOperations.getUid());
                return;
            }
        }
        log.warn("User do not exist");
        throw new UserDontExistException("User dont exist");
    }

    @Override
    public void authorize(HttpServletRequest request) throws UserDontExistException {
        String token = null;
        String refresh = null;
        if (request.getCookies() != null){
            for (Cookie value : Arrays.stream(request.getCookies()).toList()) {
                if (value.getName().equals(AUTH_TOKEN)) {
                    token = value.getValue();
                } else if (value.getName().equals(REFRESH_TOKEN)) {
                    refresh = value.getValue();
                }
            }
        }else {
            log.info("login failed, empty token");
            throw new IllegalArgumentException("Token can't be null");
        }
            if (token != null && !token.isEmpty()) {
                String subject = jwtService.getSubject(token);
                userRepository.findUserByLoginAndLockAndEnabledAndIsAdmin(subject)
                        .orElseThrow(() -> new UserDontExistException("User not found"));
            } else if (refresh != null && !refresh.isEmpty()) {
                String subject = jwtService.getSubject(token);
                userRepository.findUserByLoginAndLockAndEnabledAndIsAdmin(subject)
                        .orElseThrow(() -> new UserDontExistException("User not found"));

            }
    }

}
