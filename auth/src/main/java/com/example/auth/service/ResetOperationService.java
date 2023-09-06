package com.example.auth.service;

import com.example.auth.entity.ResetOperations;
import com.example.auth.entity.User;
import com.example.auth.repository.ResetOperationsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ResetOperationService {

    private final ResetOperationsRepository repository;

    @Transactional
    public ResetOperations initResetOperation(User user){
        ResetOperations resetOperations = new ResetOperations();

        resetOperations.setUid(UUID.randomUUID().toString());
        resetOperations.setCreateDate(new Timestamp(System.currentTimeMillis()).toString());
        resetOperations.setUser(user);

        repository.deleteAllByUser(user);
        return repository.saveAndFlush(resetOperations);
    }

    public void endOperation(String uid){
        repository.findByUid(uid).ifPresent(repository::delete);
    }

    @Scheduled(cron = "0 0/1 * * * *") //minute, cron = "0 0/15 * * * * - 15 minutes
    protected void deleteExpiredOperation(){
        List<ResetOperations> resetOperations = repository.findExpiredOperations();
        if (resetOperations != null && !resetOperations.isEmpty()){
            repository.deleteAll(resetOperations);
        }
    }
}