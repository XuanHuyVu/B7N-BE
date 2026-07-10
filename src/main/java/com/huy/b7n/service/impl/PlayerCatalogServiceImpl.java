package com.huy.b7n.service.impl;

import com.huy.b7n.common.EPlayerLevel;
import com.huy.b7n.dto.PlayerDto;
import com.huy.b7n.entity.PlayerEntity;
import com.huy.b7n.service.BaseService;
import com.huy.b7n.service.PlayerCatalogService;
import com.huy.b7n.service.dao.PlayerDAO;
import com.huy.b7n.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlayerCatalogServiceImpl extends BaseService implements PlayerCatalogService {

    private final PlayerDAO playerDAO;

    @Override
    public PlayerDto createPlayer(PlayerDto request) {
        validateCreatePlayerRequest(request);
        if (playerDAO.existsByPlayerCode(request.getPlayerCode()))
            throw new IllegalArgumentException("Mã người chơi đã tồn tại: " + request.getPlayerCode());
        PlayerEntity player = MapperUtils.convertValue(request, PlayerEntity.class);
        player.setLevelScore(resolveLevelScore(request.getLevel(), request.getLevelScore()));
        return MapperUtils.convertValue(playerDAO.save(player), PlayerDto.class);
    }

    @Override
    public List<PlayerDto> getPlayers() {
        return mapList(playerDAO.findAll(), PlayerDto.class);
    }

    @Override
    public PlayerDto getPlayer(String playerCode) {
        Assert.hasText(playerCode, "Mã người chơi không được rỗng");
        return MapperUtils.convertValue( playerDAO.getRequired(playerCode), PlayerDto.class);
    }

    @Override
    public PlayerDto updatePlayer(String playerCode, PlayerDto request) {
        Assert.hasText(playerCode, "Mã người chơi không được rỗng");
        PlayerEntity player = playerDAO.getRequired(playerCode);
        MapperUtils.copyNonNullIgnore(request, player, Set.of("id", "playerCode"));
        if (Objects.nonNull(request.getLevel()) && Objects.isNull(request.getLevelScore()))
            player.setLevelScore(resolveLevelScore(request.getLevel(), null));
        return MapperUtils.convertValue(playerDAO.save(player), PlayerDto.class);
    }

    @Override
    public void deletePlayer(String playerCode) {
        Assert.hasText(playerCode, "Mã người chơi không được rỗng");
        playerDAO.delete(playerDAO.getRequired(playerCode));
    }

    private void validateCreatePlayerRequest(PlayerDto request) {
        Assert.hasText(request.getPlayerCode(), "Mã người chơi không được rỗng.");
        Assert.hasText(request.getName(), "Tên người chơi không được rỗng");
        Assert.notNull(request.getLevel(), "Trình độ người chơi không được rỗng");
    }

    private BigDecimal resolveLevelScore(EPlayerLevel level, BigDecimal levelScore) {
        if (Objects.nonNull(levelScore)) return levelScore;
        return Objects.nonNull(level) ? level.getAverageScore() : null;
    }
}