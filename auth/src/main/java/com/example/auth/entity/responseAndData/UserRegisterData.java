package com.example.auth.entity.responseAndData;

import com.example.auth.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;

@Getter
@Setter
@Builder
public class UserRegisterData {

    @Length(min = 5, max = 50, message = "Login powinien mieć od 5 do 50 znaków")
    private String login;
    @Email
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Length(min = 8, max = 75, message = "Hasło powinno mieć od 8 do 75 znaków")
    private String password;
    private Role role;
}
