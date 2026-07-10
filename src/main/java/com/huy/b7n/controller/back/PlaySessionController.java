package com.huy.b7n.controller.back;

import com.huy.b7n.controller.BaseController;
import com.huy.b7n.manager.PlaySessionManager;
import com.huy.b7n.request.CreatePlaySessionRequest;
import com.huy.b7n.response.CreatePlaySessionResponse;
import com.huy.b7n.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/play-sessions")
@RequiredArgsConstructor
public class PlaySessionController extends BaseController {

    private final PlaySessionManager playSessionManager;

    @PostMapping
    public ResponseEntity<ResponseDto<CreatePlaySessionResponse>> createSession(@RequestBody CreatePlaySessionRequest request) {
        return success(playSessionManager.createSession(request));
    }
}