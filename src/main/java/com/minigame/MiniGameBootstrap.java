package com.minigame;

import com.minigame.api.server.MiniGameServer;

import java.io.IOException;

public class MiniGameBootstrap {

    public static void main(String[] args) throws IOException {
        System.out.println("#######Starting mini-game backend############");
        MiniGameServer.SERVER.start();
    }
}
