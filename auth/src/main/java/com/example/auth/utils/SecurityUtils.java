package com.example.auth.utils;

public class SecurityUtils {
    public static final String REFRESH_TOKEN = "refresh";
    public static final String AUTH_TOKEN = "Authorization";

    // PERMIT ALL ENDPOINTS
    public static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    public static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    public static final String VALIDATE_ENDPOINT = "/api/v1/auth/validate";
    public static final String RESET_PASSWORD_ENDPOINT = "/api/v1/auth/reset-password";
    public static final String ACTIVATE_ENDPOINT = "/api/v1/auth/activate";
    public static final String LOGOUT_ENDPOINT = "/api/v1/auth/logout";
    public static final String AUTO_LOGIN_ENDPOINT = "/api/v1/auth/auto-login";
    public static final String LOGGED_IN_ENDPOINT = "/api/v1/auth/logged-in";
    public static final String AUTHORIZE_ENDPOINT = "/api/v1/auth/authorize";

}
