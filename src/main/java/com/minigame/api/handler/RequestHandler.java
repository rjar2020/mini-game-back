package com.minigame.api.handler;

import com.minigame.dao.LevelStore;
import com.minigame.dao.LoginStore;
import com.minigame.service.LevelScoreService;
import com.minigame.service.LoginService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var uri = exchange.getRequestURI().toASCIIString();
        if (uri.matches(LoginHandler.PATH_REGEX) && "GET".equals(exchange.getRequestMethod())){
            new LoginHandler(new LoginService(LoginStore.getInstance())).handle(exchange);
        } else if(uri.matches(UserScoreHandler.PATH_REGEX) && "POST".equals(exchange.getRequestMethod())) {
            new UserScoreHandler(
                    new LoginService(LoginStore.getInstance()),
                    new LevelScoreService(LevelStore.getInstance())).handle(exchange);
        } else if(uri.matches(LevelHighScoreHandler.PATH_REGEX) && "GET".equals(exchange.getRequestMethod())) {
            new LevelHighScoreHandler(new LevelScoreService(LevelStore.getInstance())).handle(exchange);
        } else {
            resourceNotFound(exchange);
        }
    }

    private void resourceNotFound(HttpExchange exchange) throws IOException {
        var respText = "Resource not found";
        exchange.sendResponseHeaders(404, respText.getBytes().length);
        var outputStream = exchange.getResponseBody();
        outputStream.write(respText.getBytes());
        outputStream.flush();
        exchange.close();
    }
}
