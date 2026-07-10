package com.huy.b7n.repository;

import com.huy.b7n.entity.MatchPlayerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayerEntity, Long> {

    @EntityGraph(attributePaths = {
            "match",
            "match.round",
            "match.round.session",
            "player"
    })
    List<MatchPlayerEntity> findAllByMatch_Round_Session_SessionCodeAndMatch_Round_RoundNumberAndMatch_CourtNumber(
            String sessionCode,
            Integer roundNumber,
            Integer courtNumber
    );

    @EntityGraph(attributePaths = {
            "match",
            "match.round",
            "match.round.session",
            "player"
    })
    List<MatchPlayerEntity> findAllByMatch_Round_Session_SessionCode(String sessionCode);

    @EntityGraph(attributePaths = {
            "match",
            "match.round",
            "match.round.session",
            "player"
    })
    Optional<MatchPlayerEntity> findByMatch_Round_Session_SessionCodeAndMatch_Round_RoundNumberAndMatch_CourtNumberAndPlayer_PlayerCode(
            String sessionCode,
            Integer roundNumber,
            Integer courtNumber,
            String playerCode
    );
}
