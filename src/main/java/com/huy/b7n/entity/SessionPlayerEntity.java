package com.huy.b7n.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.ESessionPlayerStatus;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.ESessionPlayerStatusConverter;
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
        name = TableNameConstant.SESSION_PLAYER,
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_SESSION_PLAYER", columnNames = {
                        "SESSION_ID",
                        "PLAYER_ID"
                })
        }
)
public class SessionPlayerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private PlaySessionEntity session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAYER_ID", nullable = false)
    private PlayerEntity player;

    @Column(name = "CURRENT_STATUS")
    @Convert(converter = ESessionPlayerStatusConverter.class)
    private ESessionPlayerStatus currentStatus;

    @Column(name = "MATCH_COUNT")
    private Integer matchCount;

    @Column(name = "REST_COUNT")
    private Integer restCount;

    @Column(name = "CONSECUTIVE_MATCH_COUNT")
    private Integer consecutiveMatchCount;

    @Column(name = "LAST_PLAYED_ROUND")
    private Integer lastPlayedRound;

    @Column(name = "LAST_REST_ROUND")
    private Integer lastRestRound;

    @Column(name = "JOINED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date joinedAt;

    @Column(name = "LEFT_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date leftAt;
}
