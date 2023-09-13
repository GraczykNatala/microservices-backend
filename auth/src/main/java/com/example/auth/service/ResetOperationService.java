package com.example.auth.service;

import com.example.auth.entity.ResetOperations;
import com.example.auth.entity.User;


public interface ResetOperationService {

    ResetOperations initResetOperation(User user);

   void endOperation(String uid);

}
