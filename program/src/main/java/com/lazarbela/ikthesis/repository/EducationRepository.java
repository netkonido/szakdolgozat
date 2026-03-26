package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Integer> {
    //List<Education> findBySessionId(String sessionId);
}
