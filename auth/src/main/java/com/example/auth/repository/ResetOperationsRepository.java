package com.example.auth.repository;

import com.example.auth.entity.ResetOperations;
import com.example.auth.entity.User;
import jakarta.persistence.criteria.From;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResetOperationsRepository extends JpaRepository<ResetOperations, Long> {

    @Modifying
    void deleteAllByUser(User user);

    Optional<ResetOperations> findByUid(String uid);

    @Query(nativeQuery = true, value = "Select * From resetoperations where createdate <= current_timestamp - INTERVAL '15 minutes'" )
    List<ResetOperations> findExpiredOperations();

}
