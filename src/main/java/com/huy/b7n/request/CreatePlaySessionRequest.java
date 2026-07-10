package com.huy.b7n.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.EMatchType;
import com.huy.b7n.dto.PlayerDto;
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
public class CreatePlaySessionRequest {
    private Integer courtCount;
    private EMatchType matchType;
    private List<String> playerCodes;
}
