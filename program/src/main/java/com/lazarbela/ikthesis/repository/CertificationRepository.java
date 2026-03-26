package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    //List<Certification> FindBySessionID(String sessionId);
}
