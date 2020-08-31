package com.minigame.api.handler;

import com.minigame.api.dto.HttpResponseDTO;
import com.minigame.api.util.HttpHandlerUtil;
import com.minigame.service.LevelScoreBoardService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.function.Function;

public class LevelHighScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/highscorelist";
    private final LevelScoreBoardService levelScoreBoardService;

    public LevelHighScoreHandler(LevelScoreBoardService levelScoreBoardService) {
        this.levelScoreBoardService = levelScoreBoardService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        HttpHandlerUtil.sendHttpResponseAndEndExchange(
                exchange,
                HttpHandlerUtil.getValidLevelId(exchange)
                        .map(getHighScoreForLevelId())
                        .orElse(new HttpResponseDTO(400, "Invalid levelId. Most be a positive integer of 31 bits"))
        );
    }

    private Function<Integer, HttpResponseDTO> getHighScoreForLevelId() {
        return levelId -> levelScoreBoardService.getHighestScoresForLevel(levelId)
                .map(scoreResponse -> new HttpResponseDTO(200, scoreResponse))
                .orElse(new HttpResponseDTO(404, ""));
    }
}