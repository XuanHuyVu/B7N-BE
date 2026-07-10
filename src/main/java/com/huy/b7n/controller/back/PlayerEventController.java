package com.huy.b7n.controller.back;

import com.huy.b7n.controller.BaseController;
import com.huy.b7n.manager.PlayerEventManager;
import com.huy.b7n.request.MarkPlayerEventRequest;
import com.huy.b7n.request.ReplacePlayerRequest;
import com.huy.b7n.response.ReplacePlayerResponse;
import com.huy.b7n.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player-events")
@RequiredArgsConstructor
public class PlayerEventController extends BaseController {

    private final PlayerEventManager playerEventManager;

    @PostMapping("/replace-player")
    public ResponseEntity<ResponseDto<ReplacePlayerResponse>> replacePlayer(@RequestBody ReplacePlayerRequest request) {
        return success(playerEventManager.replacePlayer(request));
    }

    @PostMapping("/mark")
    public ResponseEntity<ResponseDto<?>> markEvent(@RequestBody MarkPlayerEventRequest request) {
        playerEventManager.markEvent(request);
        return success();
    }
}