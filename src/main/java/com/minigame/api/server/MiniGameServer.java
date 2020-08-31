package com.minigame.api.server;

import com.minigame.api.handler.RequestHandler;
import com.minigame.dao.LoginStore;
import com.minigame.service.LoginService;
import com.minigame.service.SessionCleanUpService;
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
    public static final int SERVER_PORT = 8000;
    private HttpServer httpServer;
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final SessionCleanUpService SESSION_CLEAN_UP_SERVICE = new SessionCleanUpService(
            LoginStore.getInstance(),
            new LoginService(LoginStore.getInstance())
    );

    private MiniGameServer() {
        httpServer = null;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The mini-gama server wasn't initialized. Exception: ", e);
        }
    }

    public void start() {
        if(Objects.nonNull(httpServer) && !started.getAndSet(true)) {
            httpServer.createContext("/", exchange -> new RequestHandler().handle(exchange));
            httpServer.setExecutor(Executors.newWorkStealingPool());
            httpServer.start();
            SESSION_CLEAN_UP_SERVICE.ScheduleOldSessionsCleanUp();
        }
    }
}
