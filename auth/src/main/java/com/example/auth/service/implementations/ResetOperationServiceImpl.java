package com.example.auth.service.implementations;

import com.example.auth.entity.ResetOperations;
import com.example.auth.entity.User;
import com.example.auth.repository.ResetOperationsRepository;
import com.example.auth.service.ResetOperationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ResetOperationServiceImpl implements ResetOperationService {

    private final ResetOperationsRepository repository;

    @Transactional
    public ResetOperations initResetOperation(User user){
        log.info("--START initResetOperation");
        ResetOperations resetOperations = new ResetOperations();
        resetOperations.setUid(UUID.randomUUID().toString());
        resetOperations.setCreateDate(new Timestamp(System.currentTimeMillis()));
        resetOperations.setUser(user);

        repository.deleteAllByUser(user);
        log.info("--STOP initResetOperation");
        return repository.saveAndFlush(resetOperations);
    }

    public void endOperation(String uid){
        repository.findByUid(uid).ifPresent(repository::delete);
    }

    @Scheduled(cron = "0 0/1 * * * *") //minute, cron = "0 0/15 * * * * - 15 minutes
    protected void deleteExpiredOperation(){
        List<ResetOperations> resetOperations = repository.findExpiredOperations();
        log.info("Found {} expired operations to delete ", resetOperations.size());
        if (resetOperations != null && !resetOperations.isEmpty()){
            repository.deleteAll(resetOperations);
        }
    }
}
