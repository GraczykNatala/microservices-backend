package com.example.auth.service;


import com.example.auth.entity.Role;
import com.example.auth.entity.User;
import com.example.auth.entity.UserRegisterDto;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

}
