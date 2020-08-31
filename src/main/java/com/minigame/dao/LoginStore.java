package com.minigame.dao;

import com.minigame.model.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LoginStore {

    private static final LoginStore LOGIN_STORE = new LoginStore();
    private static final Map<UUID, Pair<Integer, Instant>> SESSION_STORE = new ConcurrentHashMap<>();

    private LoginStore() { }

    public static LoginStore getInstance() {
        return LOGIN_STORE;
    }

    public Optional<UUID> createOrRetrieveSessionKey(int userId) {
        var activeSessionForUser = getExistingSession(userId);
        if (activeSessionForUser.isPresent()){
            if (isExpiredSession(activeSessionForUser.get().getKey())) {
                SESSION_STORE.remove(activeSessionForUser.get().getKey(), activeSessionForUser.get().getValue());
                SESSION_STORE.putIfAbsent(UUID.randomUUID(), new Pair<>(userId, Instant.now()));
            }
        } else {
            SESSION_STORE.putIfAbsent(UUID.randomUUID(), new Pair<>(userId, Instant.now()));
        }
        return getExistingSession(userId).map(Map.Entry::getKey);
    }

    public Optional<Pair<Integer, Instant>> getSessionDetails(UUID sessionKey) {
            return Optional.ofNullable(SESSION_STORE.get(sessionKey));
    }

    private boolean isExpiredSession(UUID activeSessionForUser) {
        return Duration.between(
                SESSION_STORE.getOrDefault(activeSessionForUser, new Pair<>(0, Instant.EPOCH)).getRight(),
                Instant.now()).toMinutes() > 10;
    }

    private Optional<Map.Entry<UUID, Pair<Integer, Instant>>> getExistingSession(int userId) {
        return SESSION_STORE.entrySet()
                .stream()
                .filter((entry -> userId == entry.getValue().getLeft())).findFirst();
    }
}
