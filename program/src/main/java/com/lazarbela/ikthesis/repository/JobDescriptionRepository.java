package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Integer> {
    JobDescription findBySessionId(String sessionId);
    boolean existsBySessionId(String sessionId);
}
