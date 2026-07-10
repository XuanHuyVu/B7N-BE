package com.huy.b7n.utils;

import com.huy.b7n.dto.PlayerDto;
import com.huy.b7n.dto.SessionPlayerDto;
import com.huy.b7n.entity.PlayerEntity;
import com.huy.b7n.entity.SessionPlayerEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SessionPlayerMapper {
    public SessionPlayerDto toDto(SessionPlayerEntity entity) {
        if (Objects.isNull(entity)) return null;
        SessionPlayerDto dto = new SessionPlayerDto();
        BeanUtils.copyProperties(entity, dto, "player");
        dto.setPlayer(toPlayerDto(entity.getPlayer()));
        return dto;
    }

    public List<SessionPlayerDto> toDtos(List<SessionPlayerEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDto)
                .toList();
    }

    private PlayerDto toPlayerDto(PlayerEntity entity) {
        if (Objects.isNull(entity)) return null;
        PlayerDto dto = new PlayerDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}