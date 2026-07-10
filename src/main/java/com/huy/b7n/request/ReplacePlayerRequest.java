package com.huy.b7n.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplacePlayerRequest {
    private String sessionCode;
    private Integer roundNumber;
    private Integer courtNumber;
    private String oldPlayerCode;
    private String newPlayerCode;
    private Boolean beforeMatchStart;
    private String reason;
}
