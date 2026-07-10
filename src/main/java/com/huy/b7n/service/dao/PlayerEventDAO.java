package com.huy.b7n.service.dao;

import com.huy.b7n.entity.PlayerEventEntity;
import com.huy.b7n.repository.PlayerEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerEventDAO {

    private final PlayerEventRepository playerEventRepository;

    public PlayerEventEntity save(PlayerEventEntity entity) {
        return playerEventRepository.save(entity);
    }

    public List<PlayerEventEntity> findEventsBySession(String sessionCode) {
        return playerEventRepository.findAllBySession_SessionCodeOrderByCreatedAtDesc(sessionCode);
    }
}
