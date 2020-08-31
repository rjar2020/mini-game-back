package com.minigame.service;

import com.minigame.dao.LoginStore;
import com.minigame.model.SessionAttributes;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionCleanUpService {

    private static final Logger LOGGER = Logger.getLogger(SessionCleanUpService.class.getName());

    private final LoginStore loginStore;
    private final LoginService loginService;

    public SessionCleanUpService(LoginStore loginStore, LoginService loginService) {
        this.loginStore = loginStore;
        this.loginService = loginService;
    }

    public void ScheduleOldSessionsCleanUp() {
        LOGGER.log(Level.INFO, "Scheduling session clean-up job");
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::cleanUpOldSessions,
                        SessionAttributes.TTL.getTime(),
                        SessionAttributes.TTL.getTime(),
                        SessionAttributes.TTL.getUnit());
    }

    void cleanUpOldSessions() {
        LOGGER.log(Level.INFO, "Start of execution of session clean-up job");
        Optional<UUID> oldestSession;
        var oldestSessionExpired = false;
        do {
            oldestSession = loginStore.getOldestSession();
            if (oldestSession.isPresent()) {
                 oldestSessionExpired = loginService.getUserIfActiveSession(oldestSession.get()).isEmpty();
                 if (oldestSessionExpired) {
                     loginStore.removeOldestSession(oldestSession.get());
                     LOGGER.log(Level.INFO, "Session removed: " + oldestSession.get());
                 }
            }
        } while (oldestSession.isPresent() && oldestSessionExpired);
        LOGGER.log(Level.INFO, "End of execution of session clean-up job");
    }
}
