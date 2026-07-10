package com.huy.b7n.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huy.b7n.common.EMatchType;
import com.huy.b7n.common.EPlaySessionStatus;
import com.huy.b7n.common.TableNameConstant;
import com.huy.b7n.converter.EMatchTypeConverter;
import com.huy.b7n.converter.EPlaySessionStatusConverter;
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
        name = TableNameConstant.PLAY_SESSION,
        uniqueConstraints = @UniqueConstraint(name = "UK_SESSION_CODE", columnNames = "SESSION_CODE")
)
public class PlaySessionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SESSION_CODE")
    private String sessionCode;

    @Column(name = "COURT_COUNT")
    private Integer courtCount;

    @Column(name = "MATCH_TYPE")
    @Convert(converter = EMatchTypeConverter.class)
    private EMatchType matchType;

    @Column(name = "STATUS")
    @Convert(converter = EPlaySessionStatusConverter.class)
    private EPlaySessionStatus status;

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