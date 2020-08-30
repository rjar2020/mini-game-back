package com.minigame.api.handler;

import com.minigame.api.util.Pair;
import com.minigame.service.LevelScoreService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.function.Function;

public class LevelHighScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/highscorelist";
    private final LevelScoreService levelScoreService;

    public LevelHighScoreHandler(LevelScoreService levelScoreService) {
        this.levelScoreService = levelScoreService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpHandlerUtil.sendHttpResponseAndEndExchange(
                exchange,
                HttpHandlerUtil.getValidLevelId(exchange)
                        .map(getHighScoreForLevelId())
                        .orElse(new Pair<>(400, "Invalid levelId. Most be a positive integer of 31 bits"))
        );
    }

    private Function<Integer, Pair<Integer, String>> getHighScoreForLevelId() {
        return levelId -> levelScoreService.getHighestScoresForLevel(levelId)
                .map(scoreResponse -> new Pair<>(200, scoreResponse))
                .orElse(new Pair<>(404, ""));
    }
}