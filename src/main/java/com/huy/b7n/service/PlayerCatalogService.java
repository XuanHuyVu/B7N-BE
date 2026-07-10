package com.huy.b7n.service;

import com.huy.b7n.dto.PlayerDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PlayerCatalogService {

    @Transactional
    PlayerDto createPlayer(PlayerDto request);

    List<PlayerDto> getPlayers();

    PlayerDto getPlayer(String playerCode);

    @Transactional
    PlayerDto updatePlayer(String playerCode, PlayerDto request);

    @Transactional
    void deletePlayer(String playerCode);
}
