package com.huy.b7n.service;

import com.huy.b7n.request.CompleteRoundRequest;
import com.huy.b7n.request.GenerateNextRoundRequest;
import com.huy.b7n.response.GenerateRoundResponse;
import org.springframework.transaction.annotation.Transactional;

public interface ScheduleService {

    GenerateRoundResponse generateNextRound(GenerateNextRoundRequest request);

    @Transactional
    GenerateRoundResponse completeRound(CompleteRoundRequest request);
}
