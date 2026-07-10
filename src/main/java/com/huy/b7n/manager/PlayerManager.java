package com.huy.b7n.manager;

import com.huy.b7n.dto.PlayerDto;
import com.huy.b7n.service.PlayerCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlayerManager {

    private final PlayerCatalogService playerCatalogService;

    public PlayerDto createPlayer(PlayerDto request) {
        return playerCatalogService.createPlayer(request);
    }

    public List<PlayerDto> getPlayers() {
        return playerCatalogService.getPlayers();
    }

    public PlayerDto getPlayer(String playerCode) {
        return playerCatalogService.getPlayer(playerCode);
    }

    public PlayerDto updatePlayer(String playerCode, PlayerDto request) {
        return playerCatalogService.updatePlayer(playerCode, request);
    }

    public void deletePlayer(String playerCode) {
        playerCatalogService.deletePlayer(playerCode);
    }
}