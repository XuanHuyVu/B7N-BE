package com.huy.b7n.controller.back;

import com.huy.b7n.controller.BaseController;
import com.huy.b7n.dto.PlayerDto;
import com.huy.b7n.manager.PlayerManager;
import com.huy.b7n.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController extends BaseController {

    private final PlayerManager playerManager;

    @PostMapping
    public ResponseEntity<ResponseDto<PlayerDto>> createPlayer(@RequestBody PlayerDto request) {
        return success(playerManager.createPlayer(request));
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<PlayerDto>>> getPlayers() {
        return success(playerManager.getPlayers());
    }

    @GetMapping("/{playerCode}")
    public ResponseEntity<ResponseDto<PlayerDto>> getPlayer(@PathVariable String playerCode) {
        return success(playerManager.getPlayer(playerCode));
    }

    @PatchMapping("/{playerCode}")
    public ResponseEntity<ResponseDto<PlayerDto>> updatePlayer(@PathVariable String playerCode, @RequestBody PlayerDto request) {
        return success(playerManager.updatePlayer(playerCode, request));
    }

    @DeleteMapping("/{playerCode}")
    public ResponseEntity<ResponseDto<?>> deletePlayer(@PathVariable String playerCode) {
        playerManager.deletePlayer(playerCode);
        return success();
    }
}