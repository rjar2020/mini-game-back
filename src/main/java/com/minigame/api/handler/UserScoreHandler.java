package com.minigame.api.handler;

import com.minigame.api.util.HttpHandlerUtil;
import com.minigame.model.Pair;
import com.minigame.service.LevelScoreBoardService;
import com.minigame.service.LoginService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/score\\?sessionkey\\=([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";
    private final LoginService loginService;
    private final LevelScoreBoardService levelScoreBoardService;

    public UserScoreHandler(LoginService loginService, LevelScoreBoardService levelScoreBoardService) {
        this.loginService = loginService;
        this.levelScoreBoardService = levelScoreBoardService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        HttpHandlerUtil.sendHttpResponseAndEndExchange(
                exchange,
                HttpHandlerUtil.getValidLevelId(exchange)
                        .map(integer -> processUserScoreRequest(exchange, integer))
                        .orElse(new Pair<>(400, "Invalid levelId. Most be a positive integer of 31 bits"))
        );
    }

    private Pair<Integer, String> processUserScoreRequest(HttpExchange exchange, int levelId)  {
        var sessionId = UUID.fromString(exchange.getRequestURI().getQuery().split("=")[1]);
        return loginService.getUserIfActiveSession(sessionId)
                .map(userId -> saveNewScore(exchange, levelId, userId))
                .orElseGet(() -> new Pair<>(400, "Bad Request. Invalid session."));
    }

    private Pair<Integer, String> saveNewScore(HttpExchange exchange, int levelId, Integer userId) {
        try {
            var score = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            levelScoreBoardService.saveScore(userId, levelId, Integer.parseInt(score));
            return new Pair<>(200, "Hello user score");
        } catch (Exception e) {
            return new Pair<>(400, "Bad Request. Invalid score.");
        }
    }
}
