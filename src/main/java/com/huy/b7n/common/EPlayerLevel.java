package com.huy.b7n.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum EPlayerLevel {
    NEWBIE("Newbie", new BigDecimal("0.0"), new BigDecimal("30.0")),
    YEU("Yếu", new BigDecimal("30.1"), new BigDecimal("50.0")),
    YEU_PLUS("Yếu+", new BigDecimal("50.1"), new BigDecimal("68.0")),
    TBY_MINUS("TBY-", new BigDecimal("68.1"), new BigDecimal("82.0")),
    TBY("TBY", new BigDecimal("82.1"), new BigDecimal("96.0")),
    TBY_PLUS("TBY+", new BigDecimal("96.1"), new BigDecimal("110.0")),
    TBK("TBK", new BigDecimal("110.1"), new BigDecimal("135.0")),
    BAN_CHUYEN("Bán chuyên", new BigDecimal("135.1"), new BigDecimal("155.0")),
    CHUYEN_NGHIEP("Chuyên nghiệp", new BigDecimal("155.1"), new BigDecimal("175.0"));

    private final String level;
    private final BigDecimal minScore;
    private final BigDecimal maxScore;

    public static EPlayerLevel lookup(String level) {
        return Strings.isBlank(level) ? null : Arrays.stream(EPlayerLevel.values())
                .filter(l -> level.equalsIgnoreCase(l.getLevel()))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal getAverageScore() {
        return minScore.add(maxScore)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }
}
