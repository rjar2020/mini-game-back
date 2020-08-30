package com.minigame.api.handler;

import com.minigame.api.util.Pair;
import com.minigame.service.LevelScoreService;
import com.minigame.service.LoginService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/score\\?sessionkey\\=([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";
    private final LoginService loginService;
    private final LevelScoreService levelScoreService;

    public UserScoreHandler(LoginService loginService, LevelScoreService levelScoreService) {
        this.loginService = loginService;
        this.levelScoreService = levelScoreService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<Integer, String> httpCodeAndBody;
        var levelId = HttpHandlerUtil.getLevelId(exchange);
        if (HttpHandlerUtil.isValidIntId(levelId)) {
            httpCodeAndBody = processUserScoreRequest(exchange, levelId);
        } else {
            httpCodeAndBody = new Pair<>(400, "Invalid levelId. Most be a positive integer of 31 bits");
        }
        HttpHandlerUtil.sendHttpResponse(exchange, httpCodeAndBody);
    }

    private Pair<Integer, String> processUserScoreRequest(HttpExchange exchange, int levelId) throws IOException {
        var sessionId = UUID.fromString(exchange.getRequestURI().getQuery().split("=")[1]);
        var userId = loginService.getUserIfActiveSession(sessionId);
        if(userId.isPresent()) {
            return saveNewScore(exchange, levelId, userId.get());
        } else {
            return new Pair<>(400, "Bad Request. Invalid session.");
        }
    }

    private Pair<Integer, String> saveNewScore(HttpExchange exchange, int levelId, Integer userId) throws IOException {
        try {
            var score = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            levelScoreService.saveScore(userId, levelId, Integer.parseInt(score));
            return new Pair<>(200, "Hello user score");
        } catch (Exception e) {
            return new Pair<>(400, "Bad Request. Invalid score.");
        }
    }
}
