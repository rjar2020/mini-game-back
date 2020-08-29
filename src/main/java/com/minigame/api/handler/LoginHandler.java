package com.minigame.api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler implements HttpHandler {

    public static final String PATH_REGEX = "/-?[0-9]*/login";
    private static final Logger LOGGER = Logger.getLogger(LoginHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var respText = "OK login";
        var userId = getUserId(exchange);
        if (HttpHandlerUtil.isValidIntId(userId) ) {
            exchange.sendResponseHeaders(200, respText.getBytes().length);
        } else {
            respText = "Invalid userId. Most be a positive integer of 31 bits";
            exchange.sendResponseHeaders(400, respText.getBytes().length);
        }
        HttpHandlerUtil.sendHttpResponse(exchange, respText);
    }

    private int getUserId(HttpExchange exchange) {
        try {
            return Integer.parseInt(exchange.getRequestURI().toASCIIString().split("/")[1]);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "UserId cannot be processed, service will return 404");
        }
        return -1;
    }
}
