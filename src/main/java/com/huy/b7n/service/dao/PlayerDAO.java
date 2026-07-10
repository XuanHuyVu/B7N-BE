package com.huy.b7n.service.dao;

import com.huy.b7n.entity.PlayerEntity;
import com.huy.b7n.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerDAO {

    private final PlayerRepository playerRepository;

    public PlayerEntity save(PlayerEntity entity) {
        return playerRepository.save(entity);
    }

    public List<PlayerEntity> saveAll(List<PlayerEntity> entities) {
        return playerRepository.saveAll(entities);
    }

    public Optional<PlayerEntity> findByPlayerCode(String playerCode) {
        return playerRepository.findByPlayerCode(playerCode);
    }

    public PlayerEntity getRequired(String playerCode) {
        return playerRepository.findByPlayerCode(playerCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người chơi: " + playerCode));
    }

    public boolean existsByPlayerCode(String playerCode) {
        return playerRepository.existsByPlayerCode(playerCode);
    }

    public List<PlayerEntity> findAllByPlayerCodes(Collection<String> playerCodes) {
        return playerRepository.findAllByPlayerCodeIn(playerCodes);
    }

    public List<PlayerEntity> findAll() {
        return playerRepository.findAll();
    }

    public void delete(PlayerEntity entity) {
        playerRepository.delete(entity);
    }
}
