package com.huy.b7n.manager;

import com.huy.b7n.request.MarkPlayerEventRequest;
import com.huy.b7n.request.ReplacePlayerRequest;
import com.huy.b7n.response.ReplacePlayerResponse;
import com.huy.b7n.service.PlayerEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerEventManager {

    private final PlayerEventService playerEventService;

    public ReplacePlayerResponse replacePlayer(ReplacePlayerRequest request) {
        return playerEventService.replacePlayer(request);
    }

    public void markEvent(MarkPlayerEventRequest request) {
        playerEventService.markEvent(request);
    }
}