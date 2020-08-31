package com.minigame.service;

import com.minigame.dao.LoginStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class SessionCleanUpServiceShould {

    private SessionCleanUpService sessionCleanUpService;

    @Mock
    private LoginService loginService;

    @Mock
    private LoginStore loginStore;

    @BeforeEach
    void setUp(){
        openMocks(this);
        sessionCleanUpService = new SessionCleanUpService(loginStore, loginService);
    }

    @Test
    void deleteOldExpiredSessions() {
        var sessionKey = UUID.randomUUID();
        var sessionKeyList = new LinkedList<UUID>();
        sessionKeyList.add(sessionKey);
        when(loginStore.getOldestSession()).then(answer -> {
            if(Objects.nonNull(sessionKeyList.peekFirst())) {
                return Optional.of(sessionKeyList.remove());
            }
            return Optional.empty();
        });
        when(loginService.getUserIfActiveSession(sessionKey)).thenReturn(Optional.empty());
        sessionCleanUpService.cleanUpOldSessions();
        verify(loginStore, times(1)).removeOldestSession(sessionKey);
    }

    @Test
    void doNotDeleteActiveSessions() {
        var sessionKey = UUID.randomUUID();
        var sessionKeyList = new LinkedList<UUID>();
        sessionKeyList.add(sessionKey);
        when(loginStore.getOldestSession()).then(answer -> {
            if(Objects.nonNull(sessionKeyList.peekFirst())) {
                return Optional.of(sessionKeyList.remove());
            }
            return Optional.empty();
        });
        when(loginService.getUserIfActiveSession(sessionKey)).thenReturn(Optional.of(22));
        sessionCleanUpService.cleanUpOldSessions();
        verify(loginStore, never()).removeOldestSession(sessionKey);
    }
}
