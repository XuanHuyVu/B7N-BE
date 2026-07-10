package com.huy.b7n.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.EPlayerEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarkPlayerEventRequest {
    private String sessionCode;
    private Integer roundNumber;
    private Integer courtNumber;
    private String playerCode;
    private String relatedPlayerCode;
    private EPlayerEventType eventType;
    private String reason;
}
