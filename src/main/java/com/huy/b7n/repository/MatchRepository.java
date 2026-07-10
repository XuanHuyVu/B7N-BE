package com.huy.b7n.repository;

import com.huy.b7n.entity.MatchEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long> {

    @EntityGraph(attributePaths = {"round", "round.session"})
    List<MatchEntity> findAllByRound_Session_SessionCodeAndRound_RoundNumberOrderByCourtNumberAsc(
            String sessionCode,
            Integer roundNumber
    );

    @EntityGraph(attributePaths = {"round", "round.session"})
    Optional<MatchEntity> findByRound_Session_SessionCodeAndRound_RoundNumberAndCourtNumber(
            String sessionCode,
            Integer roundNumber,
            Integer courtNumber
    );

    @EntityGraph(attributePaths = {"round", "round.session"})
    List<MatchEntity> findAllByRound_Session_SessionCode(String sessionCode);
}
