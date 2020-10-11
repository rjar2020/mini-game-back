package com.minigame.dao;

import com.minigame.model.UserScore;
import com.minigame.model.util.Pair;

import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
public class LevelStore {

    private static final LevelStore LEVEL_STORE = new LevelStore();
    private static final Map<Integer, NavigableSet<UserScore>> MAX_SCORE_BY_LEVEL_STORE = new ConcurrentHashMap<>();
    private static final Set<Integer> RECORDED_USERS_BY_LEVEL = new ConcurrentSkipListSet<>();

    private LevelStore() { }

    public static LevelStore getInstance() {
        return LEVEL_STORE;
    }

    public void saveScore(int userId, int level, int score) {
        if(Objects.isNull(MAX_SCORE_BY_LEVEL_STORE.get(level))) {
            MAX_SCORE_BY_LEVEL_STORE.putIfAbsent(
                    level,
                    new ConcurrentSkipListSet<>((firstScore, secondScore) -> secondScore.getScore().compareTo(firstScore.getScore())));
            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new UserScore(userId, score));
        } else {
            addScoreWithoutUserDuplication(userId, level, score);
        }
        recordUserHasAtLeastOneEntryInScoreBoardForLevel(userId, level);
    }

    public Optional<Set<UserScore>> retrieveScoreBoardForLevel(int level) {
        return Optional.ofNullable(MAX_SCORE_BY_LEVEL_STORE.get(level));
    }

    public void removeLowestScoreForLevel(int level) {
        MAX_SCORE_BY_LEVEL_STORE.get(level).remove(MAX_SCORE_BY_LEVEL_STORE.get(level).last());
    }

    private void addScoreWithoutUserDuplication(int userId, int level, int score) {
        if (isUserAlreadyRecordedInLevel(userId, level)) {
            validateAndKeepMaxScoreForUser(userId, level, score);
        } else {
            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new UserScore(userId, score));
        }
    }

    private void validateAndKeepMaxScoreForUser(int userId, int level, int score) {
        MAX_SCORE_BY_LEVEL_STORE.get(level)
                .stream()
                .filter(oldScore -> userId == oldScore.getUserId() && score > oldScore.getScore())
                .findAny().ifPresent(
                        formerHighScore -> {
                            MAX_SCORE_BY_LEVEL_STORE.get(level).remove(formerHighScore);
                            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new UserScore(userId, score));
                        });
    }

    private void recordUserHasAtLeastOneEntryInScoreBoardForLevel(int userId, int level) {
        RECORDED_USERS_BY_LEVEL.add(new Pair<>(level, userId).hashCode());
    }

    private boolean isUserAlreadyRecordedInLevel(int userId, int level) {
        return RECORDED_USERS_BY_LEVEL.contains(new Pair<>(level, userId).hashCode());
    }
}
