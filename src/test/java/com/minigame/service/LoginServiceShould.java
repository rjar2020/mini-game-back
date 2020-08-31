package com.minigame.service;

import com.minigame.dao.LoginStore;
import com.minigame.model.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class LoginServiceShould {

    private LoginService loginService;

    @Mock
    private LoginStore loginStore;

    @BeforeEach
    void setUp(){
        openMocks(this);
        loginService = new LoginService(loginStore);
    }

    @Test
    void retrieveUserIdForValidSessions() {
        var sessionKey = UUID.randomUUID();
        var sessionDetails = Optional.of(new Pair<>(Math.abs(new Random().nextInt()), Instant.now()));
        when(loginStore.getSessionDetails(sessionKey)).thenReturn(sessionDetails);
        assertEquals(sessionDetails.get().getLeft(), loginService.getUserIfActiveSession(sessionKey).orElseThrow());
    }

    @Test
    void returnEmptyUserIdWhenValidSessions() {
        var sessionKey = UUID.randomUUID();
        var sessionDetails = Optional.of(new Pair<>(Math.abs(new Random().nextInt()), Instant.now().minus(10, MINUTES)));
        when(loginStore.getSessionDetails(sessionKey)).thenReturn(sessionDetails);
        assertFalse(loginService.getUserIfActiveSession(sessionKey).isPresent());
    }
}
