package com.minigame.dao;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginStoreShould {

    private final LoginStore loginStore = LoginStore.getInstance();

    @Test
    void storeSessionsInDescendingOrderByInstantOfCreation() {
        var sessionKeyList = IntStream.range(0, 100)
                .mapToObj(loginStore::createOrRetrieveSessionKey)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        var sessionsKeySet = new HashSet<>(sessionKeyList);
        List<UUID> resultList = new ArrayList<>();
        Optional<UUID> oldestSession;
        do {
            oldestSession = loginStore.getOldestSession();
            if (oldestSession.isPresent()) {
                if(sessionsKeySet.contains(oldestSession.orElseThrow())) {
                    resultList.add(oldestSession.orElseThrow());
                }
                loginStore.removeOldestSession(oldestSession.get());
            }
        } while (oldestSession.isPresent());
        assertEquals(sessionKeyList, resultList);
    }

    @Test
    void storeSessionsInDescendingOrderByInstantOfCreationReverseScenario() {
        var sessionKeyList = IntStream.range(0, 100)
                .mapToObj(loginStore::createOrRetrieveSessionKey)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        var sessionsKeySet = new HashSet<>(sessionKeyList);
        List<UUID> resultList = new ArrayList<>();
        Collections.reverse(sessionKeyList);
        Optional<UUID> oldestSession;
        do {
            oldestSession = loginStore.getOldestSession();
            if (oldestSession.isPresent()) {
                if(sessionsKeySet.contains(oldestSession.orElseThrow())) {
                    resultList.add(oldestSession.orElseThrow());
                }
                loginStore.removeOldestSession(oldestSession.get());
            }
        } while (oldestSession.isPresent());
        Collections.reverse(resultList);
        assertEquals(sessionKeyList, resultList);
    }
}
