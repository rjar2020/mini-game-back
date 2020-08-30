package com.minigame.api.handler;

import com.minigame.api.util.Pair;
import com.minigame.service.LevelScoreService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LevelHighScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/highscorelist";
    private final LevelScoreService levelScoreService;

    public LevelHighScoreHandler(LevelScoreService levelScoreService) {
        this.levelScoreService = levelScoreService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<Integer, String> httpCodeAndBody;
        var levelId = HttpHandlerUtil.getLevelId(exchange);
        if (HttpHandlerUtil.isValidIntId(levelId) ) {
            httpCodeAndBody = levelScoreService.getHighestScoresForLevel(levelId)
                    .map(scoreResponse -> new Pair<>(200, scoreResponse))
                    .orElse(new Pair<>(404, ""));
        } else {
            httpCodeAndBody = new Pair<>(400, "Invalid levelId. Most be a positive integer of 31 bits");
        }
        HttpHandlerUtil.sendHttpResponse(exchange, httpCodeAndBody);
    }
}