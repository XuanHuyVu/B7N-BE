package com.huy.b7n.service.impl;

import com.huy.b7n.common.EMatchPlayerRole;
import com.huy.b7n.common.EPlayerEventType;
import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.dto.MatchDto;
import com.huy.b7n.dto.PlayerDto;
import com.huy.b7n.dto.PlayerEventDto;
import com.huy.b7n.entity.*;
import com.huy.b7n.request.MarkPlayerEventRequest;
import com.huy.b7n.request.ReplacePlayerRequest;
import com.huy.b7n.response.ReplacePlayerResponse;
import com.huy.b7n.service.BaseService;
import com.huy.b7n.service.PlayerEventService;
import com.huy.b7n.service.dao.PlaySessionDAO;
import com.huy.b7n.service.dao.PlayerDAO;
import com.huy.b7n.service.dao.PlayerEventDAO;
import com.huy.b7n.service.dao.ScheduleDAO;
import com.huy.b7n.utils.MapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlayerEventServiceImpl extends BaseService implements PlayerEventService {

    private final PlayerDAO playerDao;
    private final PlaySessionDAO playSessionDao;
    private final ScheduleDAO scheduleDao;
    private final PlayerEventDAO playerEventDao;

    @Override
    public ReplacePlayerResponse replacePlayer(ReplacePlayerRequest request) {
        PlaySessionEntity session = playSessionDao.getSessionRequired(request.getSessionCode());
        RoundEntity round = scheduleDao.getRoundRequired(request.getSessionCode(), request.getRoundNumber());
        MatchEntity match = scheduleDao.getMatchRequired(request.getSessionCode(), request.getRoundNumber(), request.getCourtNumber());
        MatchPlayerEntity oldMatchPlayer = scheduleDao.getMatchPlayerRequired(request.getSessionCode(), request.getRoundNumber(), request.getCourtNumber(), request.getOldPlayerCode());
        PlayerEntity newPlayer = playerDao.getRequired(request.getNewPlayerCode());
        SessionPlayerEntity oldSessionPlayer = playSessionDao.getSessionPlayerRequired(request.getSessionCode(), request.getOldPlayerCode());
        SessionPlayerEntity newSessionPlayer = playSessionDao.getSessionPlayerRequired(request.getSessionCode(), request.getNewPlayerCode());
        validateSubstitutePlayer(newSessionPlayer);
        replaceMatchPlayer(match, oldMatchPlayer, newPlayer);
        adjustSessionPlayerWhenReplace(oldSessionPlayer, newSessionPlayer, Boolean.TRUE.equals(request.getBeforeMatchStart()));
        PlayerEventEntity event = createSubstituteEvent(session, round, match, newPlayer, oldMatchPlayer.getPlayer(), request.getReason());
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("match", toMatchDto(match, request.getSessionCode(), request.getRoundNumber(), request.getCourtNumber()));
        responseMap.put("event", toPlayerEventDto(event));
        return MapperUtils.convertValue(responseMap, ReplacePlayerResponse.class);
    }

    @Override
    public void markEvent(MarkPlayerEventRequest request) {
        PlaySessionEntity session = playSessionDao.getSessionRequired(request.getSessionCode());
        PlayerEntity player = playerDao.getRequired(request.getPlayerCode());
        RoundEntity round = resolveRound(request);
        MatchEntity match = resolveMatch(request);
        PlayerEntity relatedPlayer = resolveRelatedPlayer(request);
        PlayerEventEntity event = new PlayerEventEntity();
        event.setSession(session);
        event.setRound(round);
        event.setMatch(match);
        event.setPlayer(player);
        event.setRelatedPlayer(relatedPlayer);
        event.setEventType(request.getEventType());
        event.setReason(request.getReason());
        event.setCreatedAt(new Date());
        playerEventDao.save(event);
        updateSessionPlayerStatusByEvent(request);
        updateMatchPlayerByEventIfNeeded(request);
    }

    private void replaceMatchPlayer(MatchEntity match, MatchPlayerEntity oldMatchPlayer, PlayerEntity newPlayer) {
        oldMatchPlayer.setRole(EMatchPlayerRole.REPLACED);
        oldMatchPlayer.setCompleted(false);
        oldMatchPlayer.setLeftAt(new Date());
        scheduleDao.saveMatchPlayer(oldMatchPlayer);
        MatchPlayerEntity newMatchPlayer = new MatchPlayerEntity();
        newMatchPlayer.setMatch(match);
        newMatchPlayer.setPlayer(newPlayer);
        newMatchPlayer.setTeamCode(oldMatchPlayer.getTeamCode());
        newMatchPlayer.setRole(EMatchPlayerRole.SUBSTITUTE);
        newMatchPlayer.setCompleted(true);
        newMatchPlayer.setJoinedAt(new Date());
        scheduleDao.saveMatchPlayer(newMatchPlayer);
    }

    private PlayerEventEntity createSubstituteEvent(PlaySessionEntity session, RoundEntity round, MatchEntity match,
                                                    PlayerEntity newPlayer, PlayerEntity oldPlayer, String reason) {
        PlayerEventEntity event = new PlayerEventEntity();
        event.setSession(session);
        event.setRound(round);
        event.setMatch(match);
        event.setPlayer(newPlayer);
        event.setRelatedPlayer(oldPlayer);
        event.setEventType(EPlayerEventType.SUBSTITUTED_IN);
        event.setReason(reason);
        event.setCreatedAt(new Date());
        return playerEventDao.save(event);
    }

    private void validateSubstitutePlayer(SessionPlayerEntity newSessionPlayer) {
        boolean invalid = ESessionPlayerStatus.INJURED.equals(newSessionPlayer.getCurrentStatus())
                || ESessionPlayerStatus.LEFT.equals(newSessionPlayer.getCurrentStatus())
                || ESessionPlayerStatus.TEMP_PAUSED.equals(newSessionPlayer.getCurrentStatus())
                || ESessionPlayerStatus.UNAVAILABLE.equals(newSessionPlayer.getCurrentStatus())
                || ESessionPlayerStatus.PLAYING.equals(newSessionPlayer.getCurrentStatus());
        if (invalid) throw new IllegalArgumentException("Người thay không hợp lệ");
    }

    private void adjustSessionPlayerWhenReplace(SessionPlayerEntity oldPlayer, SessionPlayerEntity newPlayer,
                                                boolean beforeMatchStart) {
        if ((beforeMatchStart)) adjustBeforeMatchStart(oldPlayer, newPlayer);
        else adjustDuringMatch(oldPlayer, newPlayer);
        playSessionDao.saveSessionPlayer(oldPlayer);
        playSessionDao.saveSessionPlayer(newPlayer);
    }

    private void adjustBeforeMatchStart(SessionPlayerEntity oldPlayer, SessionPlayerEntity newPlayer) {
        oldPlayer.setMatchCount(Math.max(0, valueOrZero(oldPlayer.getMatchCount()) - 1));
        oldPlayer.setConsecutiveMatchCount(Math.max(0, valueOrZero(oldPlayer.getConsecutiveMatchCount()) - 1));
        oldPlayer.setRestCount(valueOrZero(oldPlayer.getRestCount()) + 1);
        oldPlayer.setCurrentStatus(ESessionPlayerStatus.RESTING);
        if (newPlayer.getCurrentStatus() == ESessionPlayerStatus.RESTING)
            newPlayer.setRestCount(Math.max(0, valueOrZero(newPlayer.getRestCount()) - 1));
        newPlayer.setMatchCount(valueOrZero(newPlayer.getMatchCount()) + 1);
        newPlayer.setConsecutiveMatchCount(valueOrZero(newPlayer.getConsecutiveMatchCount()) + 1);
        newPlayer.setCurrentStatus(ESessionPlayerStatus.PLAYING);
    }

    private void adjustDuringMatch(SessionPlayerEntity oldPlayer, SessionPlayerEntity newPlayer) {
        oldPlayer.setCurrentStatus(ESessionPlayerStatus.INJURED);
        oldPlayer.setLeftAt(new Date());
        newPlayer.setCurrentStatus(ESessionPlayerStatus.PLAYING);
    }

    private RoundEntity resolveRound(MarkPlayerEventRequest request) {
        if (Objects.isNull(request.getRoundNumber())) return null;
        return scheduleDao.getRoundRequired(request.getSessionCode(), request.getRoundNumber());
    }

    private MatchEntity resolveMatch(MarkPlayerEventRequest request) {
        if (Objects.isNull(request.getRoundNumber()) || Objects.isNull(request.getCourtNumber())) return null;
        return scheduleDao.getMatchRequired(
                request.getSessionCode(),
                request.getRoundNumber(),
                request.getCourtNumber()
        );
    }

    private PlayerEntity resolveRelatedPlayer(MarkPlayerEventRequest request) {
        if (Objects.isNull(request.getRelatedPlayerCode()) || request.getRelatedPlayerCode().isBlank()) return null;
        return playerDao.getRequired(request.getRelatedPlayerCode());
    }

    private void updateSessionPlayerStatusByEvent(MarkPlayerEventRequest request) {
        ESessionPlayerStatus newStatus = resolveStatusByEvent(request.getEventType());
        if (Objects.isNull(newStatus)) return;
        SessionPlayerEntity sessionPlayer = playSessionDao.getSessionPlayerRequired(
                request.getSessionCode(),
                request.getPlayerCode()
        );
        sessionPlayer.setCurrentStatus(newStatus);
        if (newStatus == ESessionPlayerStatus.INJURED || newStatus == ESessionPlayerStatus.LEFT)
            sessionPlayer.setLeftAt(new Date());
        playSessionDao.saveSessionPlayer(sessionPlayer);
    }

    private ESessionPlayerStatus resolveStatusByEvent(EPlayerEventType eventType) {
        return switch (eventType) {
            case INJURED -> ESessionPlayerStatus.INJURED;
            case LEFT -> ESessionPlayerStatus.LEFT;
            case TEMP_PAUSED -> ESessionPlayerStatus.TEMP_PAUSED;
            case RESUMED -> ESessionPlayerStatus.AVAILABLE;
            default -> null;
        };
    }

    private void updateMatchPlayerByEventIfNeeded(MarkPlayerEventRequest request) {
        boolean missingMatchContext = Objects.isNull(request.getRoundNumber())
                || Objects.isNull(request.getCourtNumber());
        if (missingMatchContext || request.getEventType() != EPlayerEventType.INJURED) {
            return;
        }
        MatchPlayerEntity matchPlayer = scheduleDao.getMatchPlayerRequired(request.getSessionCode(),
                request.getRoundNumber(), request.getCourtNumber(), request.getPlayerCode());
        matchPlayer.setCompleted(false);
        matchPlayer.setRole(EMatchPlayerRole.REPLACED);
        matchPlayer.setLeftAt(new Date());
        scheduleDao.saveMatchPlayer(matchPlayer);
    }

    private MatchDto toMatchDto(MatchEntity match, String sessionCode,
                                Integer roundNumber, Integer courtNumber) {
        List<MatchPlayerEntity> matchPlayers = scheduleDao.findMatchPlayers(sessionCode, roundNumber, courtNumber);
        return buildMatchDto(match, sessionCode, roundNumber, courtNumber, matchPlayers);
    }

    private PlayerEventDto toPlayerEventDto(PlayerEventEntity event) {
        PlayerEventDto dto = new PlayerEventDto();
        dto.setSessionCode(event.getSession().getSessionCode());
        if (Objects.nonNull(event.getRound()))
            dto.setRoundNumber(event.getRound().getRoundNumber());
        if (Objects.nonNull(event.getMatch()))
            dto.setCourtNumber(event.getMatch().getCourtNumber());
        dto.setPlayer(MapperUtils.convertValue(event.getPlayer(), PlayerDto.class));
        dto.setRelatedPlayer(Objects.nonNull(event.getRelatedPlayer())
                        ? MapperUtils.convertValue(event.getRelatedPlayer(), PlayerDto.class) : null);
        dto.setEventType(event.getEventType());
        dto.setReason(event.getReason());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }
}