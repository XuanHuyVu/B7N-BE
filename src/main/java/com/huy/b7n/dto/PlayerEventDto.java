package com.huy.b7n.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.EPlayerEventType;
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
public class PlayerEventDto {
    private String sessionCode;
    private Integer roundNumber;
    private Integer courtNumber;
    private PlayerDto player;
    private PlayerDto relatedPlayer;
    private EPlayerEventType eventType;
    private String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date createdAt;
}
