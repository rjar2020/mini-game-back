package com.minigame.dao;

import com.minigame.model.SessionAttributes;
import com.minigame.model.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Modeling the stores as a singleton and with static collections because:
 * - This is a test, so it's the most straight forward way to simulate a persistence layer.
 * Other option could be an in-memory DB.
 *
 * - I consider an static structure is good enough for a test like this, as I don't think is a good idea to mock a java collection,
 * as the behaviour is well-known, tests will be less complex and with less boilerplate code.
 *
 * - The methods defined in the DAO classes are already abstractions of the persistence layer.
 * Changing the implementation details of the dao, including DB or data structures doesn't imply a change in the behaviour.
* */
public class LoginStore {

    private static final LoginStore LOGIN_STORE = new LoginStore();
    private static final Map<UUID, Pair<Integer, Instant>> SESSION_STORE = new ConcurrentHashMap<>();
    private static final BlockingQueue<Pair<UUID, Instant>> SESSION_CLEANUP_QUEUE =
            new PriorityBlockingQueue<>(11, Comparator.comparing(Pair::getRight));

    private LoginStore() { }

    public static LoginStore getInstance() {
        return LOGIN_STORE;
    }

    public Optional<UUID> createOrRetrieveSessionKey(int userId) {
        var activeSessionForUser = getExistingSession(userId);
        if (activeSessionForUser.isPresent()){
            if (isExpiredSession(activeSessionForUser.get().getKey())) {
                SESSION_STORE.remove(activeSessionForUser.get().getKey(), activeSessionForUser.get().getValue());
                createNewSession(userId);
            }
        } else {
            createNewSession(userId);
        }
        return getExistingSession(userId).map(Map.Entry::getKey);
    }

    public Optional<UUID> getOldestSession() {
        return Optional.ofNullable(SESSION_CLEANUP_QUEUE.peek()).map(Pair::getLeft);
    }

    public boolean removeOldestSession(UUID sessionKey) {
        AtomicBoolean removed = new AtomicBoolean(false);
        getOldestSession().ifPresent( oldestSession -> {
            if(oldestSession.equals(sessionKey)) {
                SESSION_CLEANUP_QUEUE.poll();
                SESSION_STORE.remove(sessionKey);
                removed.set(true);
            }
        });
        return removed.get();
    }

    public Optional<Pair<Integer, Instant>> getSessionDetails(UUID sessionKey) {
            return Optional.ofNullable(SESSION_STORE.get(sessionKey));
    }

    private void createNewSession(int userId) {
        var sessionKey = UUID.randomUUID();
        var sessionDetails = new Pair<>(userId, Instant.now());
        SESSION_STORE.putIfAbsent(sessionKey, sessionDetails);
        SESSION_CLEANUP_QUEUE.add(new Pair<>(sessionKey, sessionDetails.getRight()));
    }

    private boolean isExpiredSession(UUID activeSessionForUser) {
        return Duration.between(
                SESSION_STORE.getOrDefault(activeSessionForUser, new Pair<>(0, Instant.EPOCH)).getRight(),
                Instant.now()).toMinutes() > SessionAttributes.TTL.getTime();
    }

    private Optional<Map.Entry<UUID, Pair<Integer, Instant>>> getExistingSession(int userId) {
        return SESSION_STORE.entrySet()
                .stream()
                .filter((entry -> userId == entry.getValue().getLeft())).findFirst();
    }
}
