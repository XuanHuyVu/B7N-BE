package com.huy.b7n.service.dao;

import com.huy.b7n.entity.MatchEntity;
import com.huy.b7n.entity.MatchPlayerEntity;
import com.huy.b7n.entity.RoundEntity;
import com.huy.b7n.repository.MatchPlayerRepository;
import com.huy.b7n.repository.MatchRepository;
import com.huy.b7n.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleDAO {

    private final RoundRepository roundRepository;

    private final MatchRepository matchRepository;

    private final MatchPlayerRepository matchPlayerRepository;

    public Integer getNextRoundNumber(String sessionCode) {
        return roundRepository.findTopBySession_SessionCodeOrderByRoundNumberDesc(sessionCode)
                .map(round -> round.getRoundNumber() + 1)
                .orElse(1);
    }

    public RoundEntity saveRound(RoundEntity round) {
        return roundRepository.save(round);
    }

    public RoundEntity getRoundRequired(String sessionCode, Integer roundNumber) {
        return roundRepository.findBySession_SessionCodeAndRoundNumber(sessionCode, roundNumber)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lượt chơi. sessionCode = "
                                + sessionCode + ", roundNumber = " + roundNumber));
    }

    public MatchEntity saveMatch(MatchEntity match) {
        return matchRepository.save(match);
    }

    public MatchEntity getMatchRequired(String sessionCode, Integer roundNumber, Integer courtNumber) {
        return matchRepository.findByRound_Session_SessionCodeAndRound_RoundNumberAndCourtNumber(sessionCode, roundNumber, courtNumber)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay tran dau. sessionCode = " + sessionCode
                                + ", roundNumber = " + roundNumber + ", courtNumber = " + courtNumber));
    }

    public List<MatchEntity> findMatchesByRound(String sessionCode, Integer roundNumber) {
        return matchRepository.findAllByRound_Session_SessionCodeAndRound_RoundNumberOrderByCourtNumberAsc(sessionCode, roundNumber);
    }

    public List<MatchEntity> findMatchesBySession(String sessionCode) {
        return matchRepository.findAllByRound_Session_SessionCode(sessionCode);
    }

    public MatchPlayerEntity saveMatchPlayer(MatchPlayerEntity entity) {
        return matchPlayerRepository.save(entity);
    }

    public List<MatchPlayerEntity> saveMatchPlayers(List<MatchPlayerEntity> entities) {
        return matchPlayerRepository.saveAll(entities);
    }

    public List<MatchPlayerEntity> findMatchPlayers(String sessionCode, Integer roundNumber, Integer courtNumber) {
        return matchPlayerRepository.findAllByMatch_Round_Session_SessionCodeAndMatch_Round_RoundNumberAndMatch_CourtNumber(
                sessionCode, roundNumber, courtNumber);
    }

    public List<MatchPlayerEntity> findSessionMatchPlayers(String sessionCode) {
        return matchPlayerRepository.findAllByMatch_Round_Session_SessionCode(sessionCode);
    }

    public MatchPlayerEntity getMatchPlayerRequired(String sessionCode, Integer roundNumber,
                                                    Integer courtNumber, String playerCode) {
        return matchPlayerRepository
                .findByMatch_Round_Session_SessionCodeAndMatch_Round_RoundNumberAndMatch_CourtNumberAndPlayer_PlayerCode(
                        sessionCode, roundNumber, courtNumber, playerCode)
                .orElseThrow(() -> new IllegalArgumentException("Nguoi choi khong thuoc tran. sessionCode = " + sessionCode
                        + ", " + "roundNumber = " + roundNumber + ", courtNumber = " + courtNumber + ", playerCode = " + playerCode));
    }
}
