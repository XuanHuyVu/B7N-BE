package com.huy.b7n.manager;

import com.huy.b7n.request.CreatePlaySessionRequest;
import com.huy.b7n.response.CreatePlaySessionResponse;
import com.huy.b7n.service.PlaySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaySessionManager {

    private final PlaySessionService playSessionService;

    public CreatePlaySessionResponse createSession(CreatePlaySessionRequest request) {
        return playSessionService.createSession(request);
    }
}