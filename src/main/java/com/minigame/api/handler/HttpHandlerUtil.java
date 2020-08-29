package com.minigame.api.handler;

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

    public static void sendHttpResponse(HttpExchange exchange, String respText) throws IOException {
        var outputStream = exchange.getResponseBody();
        outputStream.write(respText.getBytes());
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
