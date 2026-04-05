package com.lazarbela.ikthesis.repository;

import com.lazarbela.ikthesis.model.OtherField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherFieldRepository extends JpaRepository<OtherField, Integer> {
}
