package com.huy.b7n.service;

import com.huy.b7n.request.MarkPlayerEventRequest;
import com.huy.b7n.request.ReplacePlayerRequest;
import com.huy.b7n.response.ReplacePlayerResponse;
import org.springframework.transaction.annotation.Transactional;

public interface PlayerEventService {

    @Transactional
    ReplacePlayerResponse replacePlayer(ReplacePlayerRequest request);

    @Transactional
    void markEvent(MarkPlayerEventRequest request);
}
