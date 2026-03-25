package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.FileLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileLinkRepository extends JpaRepository<FileLink, Integer> {
    List<FileLink> findBySessionId(String sessionId);
}
