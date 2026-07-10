package com.huy.b7n.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.dto.MatchDto;
import com.huy.b7n.dto.RoundDto;
import com.huy.b7n.dto.SessionPlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateRoundResponse {
    private RoundDto round;
    private List<MatchDto> matches;
    private List<SessionPlayerDto> restingPlayers;
}
