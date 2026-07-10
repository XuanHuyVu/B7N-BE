package com.huy.b7n.service.dao;

import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.entity.PlaySessionEntity;
import com.huy.b7n.entity.SessionPlayerEntity;
import com.huy.b7n.repository.PlaySessionRepository;
import com.huy.b7n.repository.SessionPlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaySessionDAO {

    private final PlaySessionRepository playSessionRepository;

    private final SessionPlayerRepository sessionPlayerRepository;

    public PlaySessionEntity saveSession(PlaySessionEntity entity) {
        return playSessionRepository.save(entity);
    }

    public PlaySessionEntity getSessionRequired(String sessionCode) {
        return playSessionRepository.findBySessionCode(sessionCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca chơi: " + sessionCode));
    }

    public boolean existsBySessionCode(String sessionCode) {
        return playSessionRepository.existsBySessionCode(sessionCode);
    }

    public List<SessionPlayerEntity> saveSessionPlayers(List<SessionPlayerEntity> entities) {
        return sessionPlayerRepository.saveAll(entities);
    }

    public SessionPlayerEntity saveSessionPlayer(SessionPlayerEntity entity) {
        return sessionPlayerRepository.save(entity);
    }

    public List<SessionPlayerEntity> findSessionPlayers(String sessionCode) {
        return sessionPlayerRepository.findAllBySession_SessionCode(sessionCode);
    }

    public List<SessionPlayerEntity> findSessionPlayersByStatuses(String sessionCode, List<ESessionPlayerStatus> statuses) {
        return sessionPlayerRepository.findBySessionCodeAndStatuses(sessionCode, statuses);
    }

    public SessionPlayerEntity getSessionPlayerRequired(String sessionCode, String playerCode) {
        return sessionPlayerRepository.findBySession_SessionCodeAndPlayer_PlayerCode(sessionCode, playerCode)
                .orElseThrow(() -> new IllegalArgumentException("Người chơi không thuộc ca chơi. sessionCode = "
                        + sessionCode + ", playerCode = " + playerCode));
    }

    public long countSessionCodeStartingWith(String prefix) {
        return playSessionRepository.countBySessionCodeStartingWith(prefix);
    }
}
