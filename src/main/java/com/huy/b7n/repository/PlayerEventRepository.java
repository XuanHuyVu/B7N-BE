package com.huy.b7n.repository;

import com.huy.b7n.entity.PlayerEventEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerEventRepository extends JpaRepository<PlayerEventEntity, Long> {

    @EntityGraph(attributePaths = {
            "session",
            "round",
            "match",
            "player",
            "relatedPlayer"
    })
    List<PlayerEventEntity> findAllBySession_SessionCodeOrderByCreatedAtDesc(String sessionCode);
}
