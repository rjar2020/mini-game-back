package com.minigame.service;

import com.minigame.dao.LoginStore;
import com.minigame.model.SessionAttributes;
import com.minigame.model.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class LoginService {

    private final LoginStore loginStore;

    public LoginService(LoginStore loginStore) {
        this.loginStore = loginStore;
    }

    public Optional<UUID> getSessionKeyForUser(int id) {
        return loginStore.createOrRetrieveSessionKey(id);
    }

    public Optional<Integer> getUserIfActiveSession(UUID sessionKey) {
        return loginStore.getSessionDetails(sessionKey)
                .filter(details -> isActiveSession(details.getRight()))
                .map(Pair::getLeft);
    }

    private boolean isActiveSession(Instant sessionCreationTime) {
        return Duration.between(sessionCreationTime, Instant.now()).toMinutes() < SessionAttributes.TTL.getTime();
    }
}
