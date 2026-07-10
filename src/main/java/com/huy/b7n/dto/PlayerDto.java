package com.huy.b7n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huy.b7n.common.EGender;
import com.huy.b7n.common.EPlayerLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerDto {
    private String name;
    private String playerCode;
    private EGender gender;
    private EPlayerLevel level;
    private BigDecimal levelScore;
}