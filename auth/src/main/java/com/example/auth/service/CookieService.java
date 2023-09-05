package com.example.auth.service;

import jakarta.servlet.http.Cookie;

public class CookieService {

    public Cookie generateCookie(String name, String value, int exp){
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); //not seen from browser
        cookie.setMaxAge(exp);
        return cookie;

    }
}
