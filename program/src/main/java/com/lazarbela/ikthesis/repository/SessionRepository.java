package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String> {
}
