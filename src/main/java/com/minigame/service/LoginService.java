package com.minigame.service;

import com.minigame.dao.LoginStore;

import java.util.Optional;
import java.util.UUID;

public class LoginService {

    private final LoginStore loginStore;

    public LoginService(LoginStore loginStore) {
        this.loginStore = loginStore;
    }

    public Optional<UUID> getSessionKeyForUser(int id) {
        return loginStore.getSessionKey(id);
    }
}
