package com.huy.b7n.service;

import com.huy.b7n.request.CreatePlaySessionRequest;
import com.huy.b7n.response.CreatePlaySessionResponse;
import org.springframework.transaction.annotation.Transactional;

public interface PlaySessionService {

    @Transactional
    CreatePlaySessionResponse createSession(CreatePlaySessionRequest request);
}
