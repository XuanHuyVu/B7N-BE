package com.huy.b7n.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.EMatchStatus;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.EMatchStatusConverter;
import com.huy.b7n.utils.DateUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = TableNameConstant.MATCH,
        uniqueConstraints = @UniqueConstraint(name = "UK_ROUND_COURT", columnNames = {
                "ROUND_ID", "COURT_NUMBER"
        })
)
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROUND_ID", nullable = false)
    private RoundEntity round;

    @Column(name = "COURT_NUMBER")
    private Integer courtNumber;

    @Column(name = "STATUS")
    @Convert(converter = EMatchStatusConverter.class)
    private EMatchStatus status;

    @Column(name = "TOTAL_SCORE_A", precision = 6, scale = 2)
    private BigDecimal totalScoreA;

    @Column(name = "TOTAL_SCORE_B", precision = 6, scale = 2)
    private BigDecimal totalScoreB;

    @Column(name = "SCORE_DIFFERENCE", precision = 6, scale = 2)
    private BigDecimal scoreDifference;

    @Column(name = "STARTED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date startedAt;

    @Column(name = "ENDED_AT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.NORMAL_TIME_PATTERN, timezone = DateUtils.DEFAULT_TIMEZONE_GMT7)
    private Date endedAt;
}
