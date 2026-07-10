package com.huy.b7n.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.EPlayerEventType;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.EPlayerEventTypeConverter;
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
        name = TableNameConstant.PLAYER_EVENT,
        indexes = {
                @Index(name = "IDX_PLAYER_EVENT_SESSION", columnList = "SESSION_ID"),
                @Index(name = "IDX_PLAYER_EVENT_ROUND", columnList = "ROUND_ID"),
                @Index(name = "IDX_PLAYER_EVENT_MATCH", columnList = "MATCH_ID"),
                @Index(name = "IDX_PLAYER_EVENT_PLAYER", columnList = "PLAYER_ID")
        }
)
public class PlayerEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private PlaySessionEntity session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUND_ID", nullable = false)
    private RoundEntity round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", nullable = false)
    private MatchEntity match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAYER_ID", nullable = false)
    private PlayerEntity player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RELATED_PLAYER_ID")
    private PlayerEntity relatedPlayer;

    @Column(name = "EVENT_TYPE")
    @Convert(converter = EPlayerEventTypeConverter.class)
    private EPlayerEventType eventType;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "CREATED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date createdAt;
}
