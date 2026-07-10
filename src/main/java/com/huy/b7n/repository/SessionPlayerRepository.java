package com.huy.b7n.repository;

import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.entity.SessionPlayerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionPlayerRepository extends JpaRepository<SessionPlayerEntity, Long> {

    @EntityGraph(attributePaths = {"session", "player"})
    List<SessionPlayerEntity> findAllBySession_SessionCode(String sessionCode);

    @Query("""
            SELECT DISTINCT sp
            FROM SessionPlayerEntity sp
            JOIN FETCH sp.player p
            JOIN sp.session s
            WHERE s.sessionCode = :sessionCode
              AND sp.currentStatus IN :statuses
            """)
    List<SessionPlayerEntity> findBySessionCodeAndStatuses(
            @Param("sessionCode") String sessionCode,
            @Param("statuses") List<ESessionPlayerStatus> statuses
    );

    @EntityGraph(attributePaths = {"session", "player"})
    Optional<SessionPlayerEntity> findBySession_SessionCodeAndPlayer_PlayerCode(
            String sessionCode,
            String playerCode
    );
}
