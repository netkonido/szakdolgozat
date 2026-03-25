package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    List<WorkExperience> findBySessionId(String sessionId);
}