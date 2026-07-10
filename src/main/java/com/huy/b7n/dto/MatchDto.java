package com.huy.b7n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.EMatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDto {
    private String sessionCode;
    private Integer roundNumber;
    private Integer courtNumber;
    private EMatchStatus status;
    private BigDecimal totalScoreA;
    private BigDecimal totalScoreB;
    private BigDecimal scoreDifference;
    private List<MatchPlayerDto> players;
}
