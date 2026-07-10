package com.huy.b7n.repository;

import com.huy.b7n.entity.PlaySessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaySessionRepository extends JpaRepository<PlaySessionEntity, Long> {

    Optional<PlaySessionEntity> findBySessionCode(String sessionCode);

    boolean existsBySessionCode(String sessionCode);

    long countBySessionCodeStartingWith(String prefix);
}
