package com.huy.b7n.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public interface Constant {

    int CACHE_TIME_TO_LIVE = 30;

    String ID = "id";
    String DESC = "desc";
    String ASC = "asc";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class ScheduleAlgorithmConfig {
        public static final int PLAYERS_PER_DOUBLES_MATCH = 4;
        public static final int PLAYERS_PER_TEAM = 2;
        public static final int DEFAULT_SLOT_DURATION_MINUTES = 15;
        public static final int MAX_CONSECUTIVE_MATCHES = 2;
        public static final int REST_COUNT_WEIGHT = 5;  //ưu tiên người nghỉ nhiều
        public static final int MATCH_COUNT_WEIGHT = 4;  //Giảm ưu tiên ng đánh nhiều
        public static final int CONSECUTIVE_MATCH_WEIGHT = 3;  // Phạt người đánh liên tiếp
        public static final int LAST_ROUND_REST_BONUS = 2;
        public static final int LEVEL_BALANCE_WEIGHT = 5; //Ưu tiên trận cân bằng trình độ
        public static final int PARTNER_REPEAT_WEIGHT = 4;   //Phạt trùng đồng đội
        public static final int OPPONENT_REPEAT_WEIGHT = 3;   //Phạt trùng đối thủ
        public static final int SUBSTITUTE_LEVEL_BALANCE_WEIGHT = 5;
        public static final int SUBSTITUTE_REPEAT_PARTNER_WEIGHT = 3;
        public static final int SUBSTITUTE_REPEAT_OPPONENT_WEIGHT = 3;
        public static final long PARTNER_LEVEL_GAP_WEIGHT = 3;
    }
}