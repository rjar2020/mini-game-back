package com.minigame.model;

import java.util.concurrent.TimeUnit;

public enum SessionAttributes {
    TTL(10, TimeUnit.MINUTES);

    private final int time;
    private final TimeUnit unit;

    SessionAttributes(int time, TimeUnit unit) {
        this.time = time;
        this.unit = unit;
    }

    public int getTime() {
        return time;
    }

    public TimeUnit getUnit() {
        return unit;
    }
}
