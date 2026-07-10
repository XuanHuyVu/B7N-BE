package com.huy.b7n.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.dto.MatchDto;
import com.huy.b7n.dto.PlayerEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplacePlayerResponse {
    private MatchDto match;
    private PlayerEventDto event;
}
