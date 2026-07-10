package com.huy.b7n.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionPlayerDto {
    private PlayerDto player;
    private ESessionPlayerStatus currentStatus;
    private Integer matchCount;
    private Integer restCount;
    private Integer consecutiveMatchCount;
    private Integer lastPlayedRound;
    private Integer lastRestRound;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date joinedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date leftAt;
}
