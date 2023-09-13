package com.example.auth.entity.responseAndData;

import com.example.auth.entity.enums.Code;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LoginResponse {

    private final String timestamp;
    private final boolean message;
    private final Code code;

    public LoginResponse(boolean message) {
        this.timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        this.message = message;
        this.code = Code.SUCCESS;
    }
}
