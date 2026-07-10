package com.huy.b7n.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.ERoundStatus;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.ERoundStatusConverter;
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
        name = TableNameConstant.ROUND,
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_SESSION_ROUND_NUMBER", columnNames = {
                        "SESSION_ID",
                        "ROUND_NUMBER"
                })
        }
)
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID", nullable = false)
    private PlaySessionEntity session;

    @Column(name = "ROUND_NUMBER")
    private Integer roundNumber;

    @Column(name = "STATUS")
    @Convert(converter = ERoundStatusConverter.class)
    private ERoundStatus status;

    @Column(name = "STARTED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date startedAt;

    @Column(name = "ENDED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date endedAt;

    @Column(name = "CREATED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date createdAt;
}
