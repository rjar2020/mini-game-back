package com.minigame.api.server;

import com.minigame.api.handler.RequestHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MiniGameServer {

    private static final Logger LOGGER = Logger.getLogger(MiniGameServer.class.getName());
    public static final MiniGameServer SERVER = new MiniGameServer();
    private HttpServer httpServer;
    private final AtomicBoolean started = new AtomicBoolean(false);

    private MiniGameServer() {
        httpServer = null;
        try {
            var serverPort = 8000;
            httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
        } catch (IOException e) {
            LOGGER.log( Level.SEVERE, "The mini-gama server wasn't initialized. Exception: ", e);
        }
    }

    public void start() {
        if(Objects.nonNull(httpServer) && !started.getAndSet(true)) {
            httpServer.createContext("/", exchange -> new RequestHandler().handle(exchange));
            httpServer.setExecutor(Executors.newWorkStealingPool());
            httpServer.start();
        }
    }

    public void stop() {
        if(Objects.nonNull(httpServer) && !started.getAndSet(false)) {
            httpServer.stop(0);
        }
    }
}
