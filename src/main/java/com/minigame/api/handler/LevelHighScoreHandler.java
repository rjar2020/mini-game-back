package com.minigame.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LevelHighScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/highscorelist";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var respText = "Hello High Score!";
        var levelId = HttpHandlerUtil.getLevelId(exchange);
        if (HttpHandlerUtil.isValidIntId(levelId) ) {
            exchange.sendResponseHeaders(200, respText.getBytes().length);
        } else {
            respText = "Invalid levelId. Most be a positive integer of 31 bits";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
        }
        HttpHandlerUtil.sendHttpResponse(exchange, respText);
    }
}