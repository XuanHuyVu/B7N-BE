package com.huy.b7n.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.EMatchPlayerRole;
import com.huy.b7n.common.ETeamCode;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.EMatchPlayerRoleConverter;
import com.huy.b7n.converter.EMatchStatusConverter;
import com.huy.b7n.converter.ETeamCodeConverter;
import com.huy.b7n.utils.DateUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = TableNameConstant.MATCH_PLAYER,
        indexes = {
                @Index(name = "IDX_MATCH_PLAYER_MATCH", columnList = "MATCH_ID"),
                @Index(name = "IDX_MATCH_PLAYER_PLAYER", columnList = "PLAYER_ID")
        }
)
public class MatchPlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false)
    private MatchEntity match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAYER_ID", nullable = false)
    private PlayerEntity player;

    @Column(name = "TEAM_CODE")
    @Convert(converter = ETeamCodeConverter.class)
    private ETeamCode teamCode;

    @Column(name = "ROLE")
    @Convert(converter = EMatchPlayerRoleConverter.class)
    private EMatchPlayerRole role;

    @Column(name = "COMPLETED")
    private Boolean completed;

    @Column(name = "JOINED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date joinedAt;

    @Column(name = "LEFT_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date leftAt;
}
