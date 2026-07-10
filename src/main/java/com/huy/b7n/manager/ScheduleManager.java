package com.huy.b7n.manager;

import com.huy.b7n.request.CompleteRoundRequest;
import com.huy.b7n.request.GenerateNextRoundRequest;
import com.huy.b7n.response.GenerateRoundResponse;
import com.huy.b7n.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleManager {

    private final ScheduleService scheduleService;

    public GenerateRoundResponse generateNextRound(GenerateNextRoundRequest request) {
        return scheduleService.generateNextRound(request);
    }

    public GenerateRoundResponse completeRound(CompleteRoundRequest request) {
        return scheduleService.completeRound(request);
    }
}