package com.minigame.api.handler;

import com.minigame.api.util.HttpHandlerUtil;
import com.minigame.model.Pair;
import com.minigame.service.LoginService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/login";
    private static final Logger LOGGER = Logger.getLogger(LoginHandler.class.getName());
    private final LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        HttpHandlerUtil.sendHttpResponseAndEndExchange(
                exchange,
                getUserId(exchange).map(this::createSessionKey)
                        .orElse(new Pair<>(400, "Invalid userId. Most be a positive integer of 31 bits"))
        );
    }

    private Pair<Integer, String> createSessionKey(int userId) {
        return loginService.getSessionKeyForUser(userId)
                .map(key -> new Pair<>(200, key.toString()))
                .orElse(new Pair<>(409, "Error creating a sessionKey"));
    }

    private Optional<Integer> getUserId(HttpExchange exchange) {
        try {
            var userId = Integer.parseInt(exchange.getRequestURI().toASCIIString().split("/")[1]);
            if(HttpHandlerUtil.isValidIntId(userId)) {
                return Optional.of(userId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "UserId cannot be processed, service will return 404");
        }
        return Optional.empty();
    }
}
