package com.huy.b7n.entity;

import com.huy.b7n.common.EGender;
import com.huy.b7n.common.EPlayerLevel;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.EGenderConverter;
import com.huy.b7n.converter.EPlayerLevelConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = TableNameConstant.PLAYER,
        uniqueConstraints = @UniqueConstraint(name = "UK_PLAYER_CODE", columnNames = "PLAYER_CODE")
)
public class PlayerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PLAYER_CODE")
    private String playerCode;

    @Column(name = "GENDER")
    @Convert(converter = EGenderConverter.class)
    private EGender gender;

    @Column(name = "LEVEL")
    @Convert(converter = EPlayerLevelConverter.class)
    private EPlayerLevel level;

    @Column(name = "LEVEL_SCORE")
    private BigDecimal levelScore;
}
