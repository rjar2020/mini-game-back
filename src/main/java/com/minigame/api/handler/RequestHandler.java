package com.minigame.api.handler;

import com.minigame.api.util.HttpHandlerUtil;
import com.minigame.dao.LevelStore;
import com.minigame.dao.LoginStore;
import com.minigame.model.Pair;
import com.minigame.service.LevelScoreBoardService;
import com.minigame.service.LoginService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        var uri = exchange.getRequestURI().toASCIIString();
        var httpHandlers = getHandlersMap().entrySet()
                .stream()
                .filter(isMatchingHandler(exchange, uri))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if(httpHandlers.isEmpty()) {
            HttpHandlerUtil.sendHttpResponseAndEndExchange(
                    exchange,
                    new Pair<>(404, "Resource not found"));
        } else {
            httpHandlers.forEach(handler -> handler.accept(exchange));
        }
    }

    private Predicate<Map.Entry<Pair<String, String>, Consumer<HttpExchange>>> isMatchingHandler(HttpExchange exchange, String uri) {
        return entry -> uri.matches(entry.getKey().getLeft()) && entry.getKey().getRight().equals(exchange.getRequestMethod());
    }

    private Map<Pair<String, String>, Consumer<HttpExchange>> getHandlersMap() {
        return Map.of(
                new Pair<>(LoginHandler.PATH_REGEX, "GET"), loginHandlerProcessor,
                new Pair<>(UserScoreHandler.PATH_REGEX, "POST"),  userScoreHandlerProcessor,
                new Pair<>(LevelHighScoreHandler.PATH_REGEX,"GET") , levelHighScoreHandlerProcessor
        );
    }

    private final Consumer<HttpExchange> loginHandlerProcessor =
            exchange -> new LoginHandler(new LoginService(LoginStore.getInstance())).handle(exchange);

    private final Consumer<HttpExchange> userScoreHandlerProcessor =
            exchange -> new UserScoreHandler(
                    new LoginService(LoginStore.getInstance()),
                    new LevelScoreBoardService(LevelStore.getInstance())).handle(exchange);

    private final Consumer<HttpExchange> levelHighScoreHandlerProcessor =
            exchange -> new LevelHighScoreHandler(new LevelScoreBoardService(LevelStore.getInstance())).handle(exchange);
}
