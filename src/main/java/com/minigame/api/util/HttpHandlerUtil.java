package com.minigame.api.util;

import com.minigame.model.Pair;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HttpHandlerUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpHandlerUtil.class.getName());

    private HttpHandlerUtil(){}

    public static boolean isValidIntId(int id) {
        return id > 0 && id < Integer.MAX_VALUE;
    }

    public static void sendHttpResponseAndEndExchange(HttpExchange exchange, Pair<Integer, String> httpCodeAndBody) {
        try {
            exchange.sendResponseHeaders(
                    httpCodeAndBody.getLeft(),
                    httpCodeAndBody.getRight().getBytes().length);
            var outputStream = exchange.getResponseBody();
            outputStream.write(httpCodeAndBody.getRight().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unexpected exception -> ", e);
        } finally {
            exchange.close();
        }
    }

    public static Optional<Integer> getValidLevelId(HttpExchange exchange) {
        try {
            var levelId = Integer.parseInt(exchange.getRequestURI().toASCIIString().split("/")[1]);
            if(isValidIntId(levelId)) {
                return Optional.of(levelId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "UserId cannot be processed, service will return 404");
        }
        return  Optional.empty();
    }
}
