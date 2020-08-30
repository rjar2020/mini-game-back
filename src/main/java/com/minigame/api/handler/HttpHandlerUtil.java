package com.minigame.api.handler;

import com.minigame.api.util.Pair;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class HttpHandlerUtil {

    private static final Logger LOGGER = Logger.getLogger(HttpHandlerUtil.class.getName());

    private HttpHandlerUtil(){}

    public static boolean isValidIntId(int id) {
        return id > 0 && id < Integer.MAX_VALUE;
    }

    public static void sendHttpResponse(HttpExchange exchange, Pair<Integer, String> httpCodeAndBody) throws IOException {
        exchange.sendResponseHeaders(
                httpCodeAndBody.getLeft(),
                httpCodeAndBody.getRight().getBytes().length);
        var outputStream = exchange.getResponseBody();
        outputStream.write(httpCodeAndBody.getRight().getBytes());
        outputStream.flush();
        exchange.close();
    }

    public static int getLevelId(HttpExchange exchange) {
        try {
            return Integer.parseInt(exchange.getRequestURI().toASCIIString().split("/")[1]);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "UserId cannot be processed, service will return 404");
        }
        return -1;
    }
}
