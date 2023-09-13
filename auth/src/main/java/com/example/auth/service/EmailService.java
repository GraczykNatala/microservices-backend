package com.example.auth.service;

import com.example.auth.entity.User;

public interface EmailService {
    void sendActivation(User user);

    void sendPasswordRecovery(User user, String uid);
}
