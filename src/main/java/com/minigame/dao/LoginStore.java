package com.minigame.dao;

import com.minigame.api.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginStore {

    private static final LoginStore LOGIN_STORE = new LoginStore();
    private static final Map<Integer, Pair<UUID, Instant>> SESSION_STORE = new ConcurrentHashMap<>();

    private LoginStore() { }

    public static LoginStore getInstance() {
        return LOGIN_STORE;
    }

    public Optional<UUID> getSessionKey(int userId) {
        Pair<UUID, Instant> activeSessionForUser = SESSION_STORE.get(userId);
        if (Objects.isNull(activeSessionForUser) || isExpiredSession(activeSessionForUser)) {
            SESSION_STORE.put(userId, new Pair<>(UUID.randomUUID(), Instant.now()));
        } else {
            SESSION_STORE.putIfAbsent(userId, new Pair<>(UUID.randomUUID(), Instant.now()));
        }
        return Optional.of(SESSION_STORE.get(userId).getLeft());
    }

    private boolean isExpiredSession(Pair<UUID, Instant> activeSessionForUser) {
        return Duration.between(activeSessionForUser.getRight(), Instant.now()).toMinutes() > 10;
    }


}
