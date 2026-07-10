package com.huy.b7n.service.impl;

import com.huy.b7n.common.*;
import com.huy.b7n.dto.MatchDto;
import com.huy.b7n.dto.RoundDto;
import com.huy.b7n.dto.SessionPlayerDto;
import com.huy.b7n.entity.*;
import com.huy.b7n.request.CompleteRoundRequest;
import com.huy.b7n.request.GenerateNextRoundRequest;
import com.huy.b7n.response.GenerateRoundResponse;
import com.huy.b7n.service.BaseService;
import com.huy.b7n.service.ScheduleService;
import com.huy.b7n.service.dao.PlaySessionDAO;
import com.huy.b7n.service.dao.ScheduleDAO;
import com.huy.b7n.utils.MapperUtils;
import com.huy.b7n.utils.SessionPlayerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends BaseService implements ScheduleService {

    private final PlaySessionDAO playSessionDao;
    private final ScheduleDAO scheduleDao;
    private final SessionPlayerMapper  sessionPlayerMapper;

    @Override
    public GenerateRoundResponse generateNextRound(GenerateNextRoundRequest request) {
        PlaySessionEntity session = playSessionDao.getSessionRequired(request.getSessionCode());
        List<SessionPlayerEntity> queriedPlayers = playSessionDao.findSessionPlayersByStatuses(
                request.getSessionCode(), List.of(ESessionPlayerStatus.AVAILABLE, ESessionPlayerStatus.RESTING));
        List<SessionPlayerEntity> activePlayers = distinctSessionPlayersByPlayerId(queriedPlayers);
        int playersPerMatch = Constant.ScheduleAlgorithmConfig.PLAYERS_PER_DOUBLES_MATCH;
        int matchCount = Math.min(session.getCourtCount(), activePlayers.size() / playersPerMatch);
        if (matchCount == 0)
            throw new IllegalArgumentException("Không đủ " + playersPerMatch + " người chơi khác nhau để tạo trận. "
                    + "Hiện chỉ có " + activePlayers.size() + " người");
        int playerNeeded = matchCount * playersPerMatch;
        Integer roundNumber = scheduleDao.getNextRoundNumber(request.getSessionCode());
        HistoryIndex historyIndex = buildHistoryIndex(request.getSessionCode());
        List<SessionPlayerEntity> selectedPlayers = selectPlayers(activePlayers, playerNeeded, roundNumber);
        Set<Long> selectedPlayerIds = selectedPlayers.stream()
                .map(SessionPlayerEntity::getPlayer)
                .map(PlayerEntity::getId)
                .collect(Collectors.toSet());
        List<SessionPlayerEntity> restingPlayers = activePlayers.stream()
                .filter(player -> !selectedPlayerIds.contains(player.getPlayer().getId()))
                .toList();
        RoundEntity round = new RoundEntity();
        round.setSession(session);
        round.setRoundNumber(roundNumber);
        round.setStatus(ERoundStatus.SCHEDULED);
        round.setCreatedAt(new Date());
        round = scheduleDao.saveRound(round);
        List<PlayerPair> pairs = createPairs(selectedPlayers, historyIndex);
        List<MatchEntity> matches = createMatches(round, pairs, matchCount, historyIndex);
        updateSessionPlayersAfterSchedule(selectedPlayers, restingPlayers, roundNumber);
        return new GenerateRoundResponse(toRoundDto(round), toMatchDtos(matches), sessionPlayerMapper.toDtos(restingPlayers));
    }

    @Override
    public GenerateRoundResponse completeRound(CompleteRoundRequest request) {
        RoundEntity round = scheduleDao.getRoundRequired(request.getSessionCode(), request.getRoundNumber());
        List<MatchEntity> matches = scheduleDao.findMatchesByRound(request.getSessionCode(), request.getRoundNumber());
        Date now = new Date();
        matches.forEach(match -> completeMatch(request.getSessionCode(), request.getRoundNumber(), match, now));
        round.setStatus(ERoundStatus.COMPLETED);
        round.setEndedAt(now);
        round = scheduleDao.saveRound(round);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("round", toRoundDto(round));
        responseMap.put("matches", toMatchDtos(matches));
        responseMap.put("restingPlayers", List.of());
        return MapperUtils.convertValue(responseMap, GenerateRoundResponse.class);
    }

    private void completeMatch(String sessionCode, Integer roundNumber, MatchEntity match, Date now) {
        match.setStatus(EMatchStatus.COMPLETED);
        match.setEndedAt(now);
        scheduleDao.saveMatch(match);
        scheduleDao.findMatchPlayers(sessionCode, roundNumber, match.getCourtNumber()).stream()
                .filter(matchPlayer -> !Boolean.FALSE.equals(matchPlayer.getCompleted()))
                .forEach(matchPlayer -> tryReturnPlayerToAvailable(sessionCode, matchPlayer));
    }

    private void tryReturnPlayerToAvailable(String sessionCode, MatchPlayerEntity matchPlayer) {
        String playerCode = matchPlayer.getPlayer().getPlayerCode();
        SessionPlayerEntity sessionPlayer = playSessionDao.getSessionPlayerRequired(sessionCode, playerCode);
        if (canReturnToAvailable(sessionPlayer)) {
            sessionPlayer.setCurrentStatus(ESessionPlayerStatus.AVAILABLE);
            playSessionDao.saveSessionPlayer(sessionPlayer);
        }
    }

    private boolean canReturnToAvailable(SessionPlayerEntity sessionPlayer) {
        return !ESessionPlayerStatus.INJURED.equals(sessionPlayer.getCurrentStatus())
                && !ESessionPlayerStatus.LEFT.equals(sessionPlayer.getCurrentStatus())
                && !ESessionPlayerStatus.TEMP_PAUSED.equals(sessionPlayer.getCurrentStatus())
                && !ESessionPlayerStatus.UNAVAILABLE.equals(sessionPlayer.getCurrentStatus());
    }

    private int calculatePriority(SessionPlayerEntity player, Integer roundNumber) {
        int restCount = valueOrZero(player.getRestCount());
        int matchCount = valueOrZero(player.getMatchCount());
        int consecutiveCount = valueOrZero(player.getConsecutiveMatchCount());
        int score = 0;
        score += restCount * Constant.ScheduleAlgorithmConfig.REST_COUNT_WEIGHT;
        score -= matchCount * Constant.ScheduleAlgorithmConfig.MATCH_COUNT_WEIGHT;
        score -= consecutiveCount * Constant.ScheduleAlgorithmConfig.CONSECUTIVE_MATCH_WEIGHT;
        if (Objects.nonNull(player.getLastRestRound()) && player.getLastRestRound().equals(roundNumber - 1))
            score += Constant.ScheduleAlgorithmConfig.LAST_ROUND_REST_BONUS;
        return score;
    }

    private List<PlayerPair> createPairs(List<SessionPlayerEntity> selectedPlayers, HistoryIndex historyIndex) {
        List<SessionPlayerEntity> pool = new ArrayList<>(selectedPlayers);
        List<PlayerPair> pairs = new ArrayList<>();
        pool.sort(Comparator.comparingInt(player -> valueOrZero(player.getMatchCount())));
        while (pool.size() >= 2) {
            SessionPlayerEntity first = pool.removeFirst();
            int bestPartnerIndex = java.util.stream.IntStream
                    .range(0, pool.size())
                    .filter(index -> !Objects.equals(first.getPlayer().getId(), pool.get(index).getPlayer().getId()))
                    .boxed()
                    .min(Comparator.comparing(index -> calculatePartnerPenalty(first, pool.get(index), historyIndex)))
                    .orElseThrow(() -> new IllegalStateException("Không tìm được đồng đội hợp lệ cho " + first.getPlayer().getPlayerCode()));
            SessionPlayerEntity bestPartner = pool.remove(bestPartnerIndex);
            pairs.add(new PlayerPair(first, bestPartner));
        }
        return pairs;
    }

    private BigDecimal calculatePartnerPenalty(SessionPlayerEntity a, SessionPlayerEntity b, HistoryIndex historyIndex) {
        BigDecimal levelGap = resolveLevelScore(a.getPlayer()).subtract(resolveLevelScore(b.getPlayer())).abs();
        int repeatCount = historyIndex.getPartnerCount(a.getPlayer().getPlayerCode(), b.getPlayer().getPlayerCode());
        BigDecimal levelPenalty = levelGap.multiply(BigDecimal.valueOf(Constant.ScheduleAlgorithmConfig.PARTNER_LEVEL_GAP_WEIGHT));
        BigDecimal repeatPenalty = BigDecimal.valueOf((long) repeatCount * Constant.ScheduleAlgorithmConfig.PARTNER_REPEAT_WEIGHT);
        return levelPenalty.add(repeatPenalty);
    }

    private List<MatchEntity> createMatches(RoundEntity round, List<PlayerPair> pairs, int matchCount, HistoryIndex historyIndex) {
        List<PlayerPair> pool = new ArrayList<>(pairs);
        List<MatchEntity> matches = new ArrayList<>();
        for (int court = 1; court <= matchCount; court++) {
            if (pool.size() < 2)
                throw new IllegalStateException("Không đủ cặp người chơi để tạo trận cho sân " + court);
            PlayerPair pairA = pool.removeFirst();
            int finalCourt = court;
            PlayerPair pairB = pool.stream()
                    .filter(candidate -> !hasCommonPlayer(pairA, candidate))
                    .min(Comparator.comparing(
                            candidate -> calculateMatchPenalty(pairA, candidate, historyIndex)))
                    .orElseThrow(() -> new IllegalStateException("Không tìm được đội B gồm 2 người khác với đội A " + "cho sân " + finalCourt));
            pool.remove(pairB);
            validateMatchPlayers(pairA, pairB);
            MatchEntity match = createMatch(round, court, pairA, pairB);
            List<MatchPlayerEntity> matchPlayers = createMatchPlayers(match, pairA, pairB);
            scheduleDao.saveMatchPlayers(matchPlayers);
            matches.add(match);
        }
        return matches;
    }

    private void validateMatchPlayers(PlayerPair pairA, PlayerPair pairB) {
        List<SessionPlayerEntity> players = List.of(pairA.first(), pairA.second(), pairB.first(), pairB.second());
        Set<Long> uniquePlayerIds = players.stream()
                .map(this::getPlayerId)
                .collect(Collectors.toSet());
        if (uniquePlayerIds.size() != Constant.ScheduleAlgorithmConfig.PLAYERS_PER_DOUBLES_MATCH)
            throw new IllegalStateException("Không thể tạo trận vì không đủ 4 người chơi khác nhau");
    }

    private MatchEntity createMatch(RoundEntity round, Integer courtNumber,
                                    PlayerPair pairA, PlayerPair pairB) {
        BigDecimal totalA = pairA.totalScore();
        BigDecimal totalB = pairB.totalScore();
        BigDecimal difference = totalA.subtract(totalB).abs();
        MatchEntity match = new MatchEntity();
        match.setRound(round);
        match.setCourtNumber(courtNumber);
        match.setStatus(EMatchStatus.SCHEDULED);
        match.setTotalScoreA(totalA);
        match.setTotalScoreB(totalB);
        match.setScoreDifference(difference);
        return scheduleDao.saveMatch(match);
    }

    private BigDecimal calculateMatchPenalty(PlayerPair pairA, PlayerPair pairB, HistoryIndex historyIndex) {
        BigDecimal levelDifference = pairA.totalScore()
                .subtract(pairB.totalScore())
                .abs();
        int opponentRepeat = countOpponentRepeat(pairA, pairB, historyIndex);
        BigDecimal levelPenalty = levelDifference.multiply(BigDecimal.valueOf(Constant.ScheduleAlgorithmConfig.LEVEL_BALANCE_WEIGHT));
        BigDecimal opponentPenalty = BigDecimal.valueOf((long) opponentRepeat * Constant.ScheduleAlgorithmConfig.OPPONENT_REPEAT_WEIGHT);
        return levelPenalty.add(opponentPenalty);
    }

    private int countOpponentRepeat(PlayerPair pairA, PlayerPair pairB, HistoryIndex historyIndex) {
        List<SessionPlayerEntity> teamA = List.of(pairA.first(), pairA.second());
        List<SessionPlayerEntity> teamB = List.of(pairB.first(), pairB.second());
        return teamA.stream()
                .flatMap(a -> teamB.stream().map(b -> historyIndex.getOpponentCount(
                        a.getPlayer().getPlayerCode(),
                        b.getPlayer().getPlayerCode()
                )))
                .mapToInt(Integer::intValue)
                .sum();
    }

    private List<MatchPlayerEntity> createMatchPlayers(MatchEntity match, PlayerPair pairA, PlayerPair pairB) {
        List<SessionPlayerEntity> sessionPlayers = List.of(pairA.first(), pairA.second(), pairB.first(), pairB.second());
        Set<String> playerCodes = sessionPlayers.stream()
                .map(this::getPlayerCode)
                .collect(Collectors.toSet());
        if (playerCodes.size() != Constant.ScheduleAlgorithmConfig.PLAYERS_PER_DOUBLES_MATCH)
            throw new IllegalStateException("Một trận đánh đôi phải có 4 người chơi khác nhau: " + playerCodes);
        return List.of(
                createMatchPlayer(match, pairA.first().getPlayer(), ETeamCode.A),
                createMatchPlayer(match, pairA.second().getPlayer(), ETeamCode.A),
                createMatchPlayer(match, pairB.first().getPlayer(), ETeamCode.B),
                createMatchPlayer(match, pairB.second().getPlayer(), ETeamCode.B));
    }

    private MatchPlayerEntity createMatchPlayer(MatchEntity match, PlayerEntity player, ETeamCode teamCode) {
        MatchPlayerEntity entity = new MatchPlayerEntity();
        entity.setMatch(match);
        entity.setPlayer(player);
        entity.setTeamCode(teamCode);
        entity.setRole(EMatchPlayerRole.MAIN);
        entity.setCompleted(true);
        entity.setJoinedAt(new Date());
        return entity;
    }

    private void updateSessionPlayersAfterSchedule(List<SessionPlayerEntity> selectedPlayers,
                                                   List<SessionPlayerEntity> restingPlayers, Integer roundNumber) {
        selectedPlayers.forEach(player -> markPlayerAsPlaying(player, roundNumber));
        restingPlayers.forEach(player -> markPlayerAsResting(player, roundNumber));
    }

    private void markPlayerAsPlaying(SessionPlayerEntity player, Integer roundNumber) {
        player.setCurrentStatus(ESessionPlayerStatus.PLAYING);
        player.setMatchCount(valueOrZero(player.getMatchCount()) + 1);
        player.setConsecutiveMatchCount(valueOrZero(player.getConsecutiveMatchCount()) + 1);
        player.setLastPlayedRound(roundNumber);
        playSessionDao.saveSessionPlayer(player);
    }

    private void markPlayerAsResting(SessionPlayerEntity player, Integer roundNumber) {
        player.setCurrentStatus(ESessionPlayerStatus.RESTING);
        player.setRestCount(valueOrZero(player.getRestCount()) + 1);
        player.setConsecutiveMatchCount(0);
        player.setLastRestRound(roundNumber);
        playSessionDao.saveSessionPlayer(player);
    }

    private HistoryIndex buildHistoryIndex(String sessionCode) {
        List<MatchPlayerEntity> matchPlayers = scheduleDao.findSessionMatchPlayers(sessionCode);
        Map<String, List<MatchPlayerEntity>> byMatch = matchPlayers.stream()
                .filter(matchPlayer -> !Boolean.FALSE.equals(matchPlayer.getCompleted()))
                .collect(Collectors.groupingBy(this::matchKey));
        HistoryIndex index = new HistoryIndex();
        byMatch.values().forEach(players -> registerMatchHistory(players, index));
        return index;
    }

    private String matchKey(MatchPlayerEntity matchPlayer) {
        MatchEntity match = matchPlayer.getMatch();
        return match.getRound().getRoundNumber() + "_" + match.getCourtNumber();
    }

    private void registerMatchHistory(List<MatchPlayerEntity> players, HistoryIndex index) {
        List<MatchPlayerEntity> teamA = players.stream()
                .filter(player -> ETeamCode.A.equals(player.getTeamCode())).toList();
        List<MatchPlayerEntity> teamB = players.stream()
                .filter(player -> ETeamCode.B.equals(player.getTeamCode())).toList();
        increasePartnerHistory(teamA, index);
        increasePartnerHistory(teamB, index);
        increaseOpponentHistory(teamA, teamB, index);
    }

    private void increasePartnerHistory(List<MatchPlayerEntity> team, HistoryIndex index) {
        if (team.size() < 2) return;
        String first = team.get(0).getPlayer().getPlayerCode();
        String second = team.get(1).getPlayer().getPlayerCode();
        index.increasePartner(first, second);
    }

    private void increaseOpponentHistory(List<MatchPlayerEntity> teamA, List<MatchPlayerEntity> teamB, HistoryIndex index) {
        teamA.forEach(a -> teamB.forEach(b -> index.increaseOpponent(
                a.getPlayer().getPlayerCode(), b.getPlayer().getPlayerCode())));
    }

    private RoundDto toRoundDto(RoundEntity round) {
        RoundDto dto = new RoundDto();
        dto.setSessionCode(round.getSession().getSessionCode());
        dto.setRoundNumber(round.getRoundNumber());
        dto.setStatus(round.getStatus());
        dto.setStartedAt(round.getStartedAt());
        dto.setEndedAt(round.getEndedAt());
        dto.setCreatedAt(round.getCreatedAt());
        return dto;
    }

    private List<MatchDto> toMatchDtos(List<MatchEntity> matches) {
        return matches.stream()
                .map(this::toMatchDto)
                .toList();
    }

    private MatchDto toMatchDto(MatchEntity match) {
        String sessionCode = match.getRound().getSession().getSessionCode();
        Integer roundNumber = match.getRound().getRoundNumber();
        Integer courtNumber = match.getCourtNumber();
        List<MatchPlayerEntity> matchPlayers = scheduleDao.findMatchPlayers(sessionCode, roundNumber, courtNumber);
        return buildMatchDto(match, sessionCode, roundNumber, courtNumber, matchPlayers);
    }

    private record PlayerPair(SessionPlayerEntity first, SessionPlayerEntity second) {
        BigDecimal totalScore() {
            return resolveLevelScore(first.getPlayer()).add(resolveLevelScore(second.getPlayer()));
        }
    }

    private static class HistoryIndex {
        private final Map<String, Integer> partnerHistory = new HashMap<>();
        private final Map<String, Integer> opponentHistory = new HashMap<>();

        void increasePartner(String a, String b) {
            partnerHistory.merge(key(a, b), 1, Integer::sum);
        }

        void increaseOpponent(String a, String b) {
            opponentHistory.merge(key(a, b), 1, Integer::sum);
        }

        int getPartnerCount(String a, String b) {
            return partnerHistory.getOrDefault(key(a, b), 0);
        }

        int getOpponentCount(String a, String b) {
            return opponentHistory.getOrDefault(key(a, b), 0);
        }

        private String key(String a, String b) {
            return a.compareTo(b) <= 0 ? a + "_" + b : b + "_" + a;
        }
    }

    private static BigDecimal resolveLevelScore(PlayerEntity player) {
        return Objects.nonNull(player.getLevelScore())
                ? player.getLevelScore()
                : Objects.nonNull(player.getLevel()) ? player.getLevel().getAverageScore() : BigDecimal.ZERO;
    }

    private String getPlayerCode(SessionPlayerEntity sessionPlayer) {
        return sessionPlayer.getPlayer().getPlayerCode();
    }

    private List<SessionPlayerEntity> distinctSessionPlayersByPlayerId(List<SessionPlayerEntity> players) {
        Map<Long, SessionPlayerEntity> uniquePlayers = new LinkedHashMap<>();
        for (SessionPlayerEntity player : players) {
            if (Objects.isNull(player) || Objects.isNull(player.getPlayer()) || Objects.isNull(player.getPlayer().getId()))
                continue;
            uniquePlayers.putIfAbsent(player.getPlayer().getId(), player);
        }
        return new ArrayList<>(uniquePlayers.values());
    }

    private List<SessionPlayerEntity> selectPlayers(List<SessionPlayerEntity> players, int playerNeeded, Integer roundNumber) {
        return players.stream()
                .sorted(Comparator.comparingInt((SessionPlayerEntity player) -> calculatePriority(player, roundNumber)).reversed())
                .limit(playerNeeded)
                .toList();
    }

    private boolean hasCommonPlayer(PlayerPair pairA, PlayerPair pairB) {
        Set<Long> pairAPlayerIds = Set.of(getPlayerId(pairA.first()), getPlayerId(pairA.second()));
        return pairAPlayerIds.contains(getPlayerId(pairB.first())) || pairAPlayerIds.contains(getPlayerId(pairB.second()));
    }

    private Long getPlayerId(SessionPlayerEntity sessionPlayer) {
        if (Objects.isNull(sessionPlayer) || Objects.isNull(sessionPlayer.getPlayer()) || Objects.isNull(sessionPlayer.getPlayer().getId()))
            throw new IllegalStateException("SessionPlayer không có thông tin người chơi hợp lệ");
        return sessionPlayer.getPlayer().getId();
    }
}