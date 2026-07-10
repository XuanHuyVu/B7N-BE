package com.huy.b7n.service.impl;

import com.huy.b7n.common.EMatchType;
import com.huy.b7n.common.EPlaySessionStatus;
import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.dto.PlaySessionDto;
import com.huy.b7n.dto.SessionPlayerDto;
import com.huy.b7n.entity.PlaySessionEntity;
import com.huy.b7n.entity.PlayerEntity;
import com.huy.b7n.entity.SessionPlayerEntity;
import com.huy.b7n.request.CreatePlaySessionRequest;
import com.huy.b7n.response.CreatePlaySessionResponse;
import com.huy.b7n.service.BaseService;
import com.huy.b7n.service.PlaySessionService;
import com.huy.b7n.service.dao.PlaySessionDAO;
import com.huy.b7n.service.dao.PlayerDAO;
import com.huy.b7n.utils.DateUtils;
import com.huy.b7n.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PlaySessionServiceImpl extends BaseService implements PlaySessionService {

    private final PlayerDAO playerDao;
    private final PlaySessionDAO playSessionDao;

    private static final DateTimeFormatter SESSION_CODE_DATE_FORMAT = DateTimeFormatter.ofPattern(DateUtils.DDMMYYYY);

    @Override
    public CreatePlaySessionResponse createSession(CreatePlaySessionRequest request) {
        String sessionCode = generateSessionCode();
        validateCreateSessionRequest(request, sessionCode, request.getPlayerCodes());
        PlaySessionEntity session = Objects.requireNonNull(MapperUtils.convertValue(request, PlaySessionEntity.class));
        session.setSessionCode(sessionCode);
        session.setStatus(EPlaySessionStatus.CREATED);
        session.setMatchType(EMatchType.DOUBLES);
        session.setCreatedAt(DateUtils.now());
        session = playSessionDao.saveSession(session);
        List<PlayerEntity> players = request.getPlayerCodes().stream().map(playerDao::getRequired).toList();
        List<SessionPlayerEntity> sessionPlayers = createSessionPlayers(session, players);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("session", MapperUtils.convertValue(session, PlaySessionDto.class));
        responseMap.put("players", mapList(sessionPlayers, SessionPlayerDto.class));
        return MapperUtils.convertValue(responseMap, CreatePlaySessionResponse.class);
    }

    private void validateCreateSessionRequest(CreatePlaySessionRequest request,
                                                String sessionCode, List<String> playerCodes) {
        Assert.isTrue(!playSessionDao.existsBySessionCode(sessionCode),
                "Ca chơi hôm nay đã được tạo: " + sessionCode);
        Assert.isTrue(Objects.nonNull(request.getCourtCount()) && request.getCourtCount() > 0,
                "Số sân phải lớn hơn 0");
        Assert.notEmpty(playerCodes, "Không có người chơi trong danh sách");
        playerCodes.forEach(code -> Assert.hasText(code, "Mã người chơi không được rỗng"));
        long distinctCount = playerCodes.stream().distinct().count();
        Assert.isTrue(distinctCount == playerCodes.size(), "Trùng mã người chơi.");
    }

    private List<SessionPlayerEntity> createSessionPlayers(PlaySessionEntity session, List<PlayerEntity> players) {
        Function<PlayerEntity, SessionPlayerEntity> buildSessionPlayer = player -> {
            SessionPlayerEntity sessionPlayer = new SessionPlayerEntity();
            sessionPlayer.setSession(session);
            sessionPlayer.setPlayer(player);
            sessionPlayer.setCurrentStatus(ESessionPlayerStatus.AVAILABLE);
            sessionPlayer.setMatchCount(0);
            sessionPlayer.setRestCount(0);
            sessionPlayer.setConsecutiveMatchCount(0);
            sessionPlayer.setJoinedAt(new Date());
            return sessionPlayer;
        };
        List<SessionPlayerEntity> sessionPlayers = players.stream()
                .map(buildSessionPlayer)
                .toList();
        return playSessionDao.saveSessionPlayers(sessionPlayers);
    }

    private String generateSessionCode() {
        String prefix = "BMT" + LocalDate.now().format(SESSION_CODE_DATE_FORMAT);
        long sequence = playSessionDao.countSessionCodeStartingWith(prefix) + 1;
        return prefix + "-" + sequence;
    }
}