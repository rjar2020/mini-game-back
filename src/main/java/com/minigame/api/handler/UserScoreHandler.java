package com.minigame.api.handler;

import com.minigame.api.util.Pair;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class UserScoreHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/score\\?sessionkey\\=([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<Integer, String> httpCodeAndBody;
        var levelId = HttpHandlerUtil.getLevelId(exchange);
        if (HttpHandlerUtil.isValidIntId(levelId) ) {
            httpCodeAndBody = new Pair<>(200, "Hello user score");
        } else {
            httpCodeAndBody = new Pair<>(400, "Invalid levelId. Most be a positive integer of 31 bits");
        }
        HttpHandlerUtil.sendHttpResponse(exchange, httpCodeAndBody);
    }
}
