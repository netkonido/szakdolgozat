package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Integer> {
    UserData findBySessionId(String sessionId);
    boolean existsBySessionId(String sessionId);
}
