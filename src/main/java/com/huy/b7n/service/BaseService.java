package com.huy.b7n.service;

import com.huy.b7n.dto.MatchDto;
import com.huy.b7n.dto.MatchPlayerDto;
import com.huy.b7n.entity.MatchEntity;
import com.huy.b7n.entity.MatchPlayerEntity;
import com.huy.b7n.utils.MapperUtils;

import java.util.*;

public abstract class BaseService {

    protected <T> List<T> mapList(Collection<?> source, Class<T> clazz) {
        return Objects.isNull(source) ? List.of() : source.stream()
                .map(item -> MapperUtils.convertValue(item, clazz))
                .toList();
    }

    protected int valueOrZero(Integer value) {
        return Optional.ofNullable(value).orElse(0);
    }

    protected MatchDto buildMatchDto(MatchEntity match, String sessionCode, Integer roundNumber,
                                        Integer courtNumber, List<MatchPlayerEntity> matchPlayers) {
        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put("sessionCode", sessionCode);
        matchMap.put("roundNumber", roundNumber);
        matchMap.put("courtNumber", courtNumber);
        matchMap.put("status", match.getStatus());
        matchMap.put("totalScoreA", match.getTotalScoreA());
        matchMap.put("totalScoreB", match.getTotalScoreB());
        matchMap.put("scoreDifference", match.getScoreDifference());
        matchMap.put("players", mapList(matchPlayers, MatchPlayerDto.class));
        return MapperUtils.convertValue(matchMap, MatchDto.class);
    }
}