package com.huy.b7n.controller.back;

import com.huy.b7n.controller.BaseController;
import com.huy.b7n.manager.ScheduleManager;
import com.huy.b7n.request.CompleteRoundRequest;
import com.huy.b7n.request.GenerateNextRoundRequest;
import com.huy.b7n.response.GenerateRoundResponse;
import com.huy.b7n.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController extends BaseController {

    private final ScheduleManager scheduleManager;

    @PostMapping("/rounds/next")
    public ResponseEntity<ResponseDto<GenerateRoundResponse>> generateNextRound(@RequestBody GenerateNextRoundRequest request) {
        return success(scheduleManager.generateNextRound(request));
    }

    @PostMapping("/rounds/complete")
    public ResponseEntity<ResponseDto<GenerateRoundResponse>> completeRound(@RequestBody CompleteRoundRequest request) {
        return success(scheduleManager.completeRound(request));
    }
}