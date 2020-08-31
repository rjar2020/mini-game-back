package com.minigame.dao;

import com.minigame.model.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class LevelStore {

    private static final LevelStore LEVEL_STORE = new LevelStore();
    private static final Map<Integer, ConcurrentSkipListSet<Pair<Integer, Integer>>> MAX_SCORE_BY_LEVEL_STORE = new ConcurrentHashMap<>();
    private static final Set<Integer> RECORDED_USERS_BY_LEVEL = new HashSet<>();

    private LevelStore() { }

    public static LevelStore getInstance() {
        return LEVEL_STORE;
    }

    public void saveScore(int userId, int level, int score) {
        if(Objects.isNull(MAX_SCORE_BY_LEVEL_STORE.get(level))) {
            MAX_SCORE_BY_LEVEL_STORE.putIfAbsent(
                    level,
                    new ConcurrentSkipListSet<>((p1, p2) -> p2.getRight().compareTo(p1.getRight())));
            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new Pair<>(userId, score));
        } else {
            addScoreWithoutUserDuplication(userId, level, score);
        }
        recordUserHasAtLeastOneEntryInScoreBoardForLevel(userId, level);
    }

    public Optional<Set<Pair<Integer, Integer>>> retrieveScoreBoardForLevel(int level) {
        return Optional.ofNullable(MAX_SCORE_BY_LEVEL_STORE.get(level));
    }

    public void removeLowestScoreForLevel(int level) {
        MAX_SCORE_BY_LEVEL_STORE.get(level).remove(MAX_SCORE_BY_LEVEL_STORE.get(level).last());
    }

    private void addScoreWithoutUserDuplication(int userId, int level, int score) {
        if (isUserAlreadyRecordedInLevel(userId, level)) {
            validateAndKeepMaxScoreForUser(userId, level, score);
        } else {
            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new Pair<>(userId, score));
        }
    }

    private void validateAndKeepMaxScoreForUser(int userId, int level, int score) {
        MAX_SCORE_BY_LEVEL_STORE.get(level)
                .stream()
                .filter(oldScore -> userId == oldScore.getLeft() && score > oldScore.getRight())
                .findAny().ifPresent(
                        formerHighScore -> {
                            MAX_SCORE_BY_LEVEL_STORE.get(level).remove(formerHighScore);
                            MAX_SCORE_BY_LEVEL_STORE.get(level).add(new Pair<>(userId, score));
                        });
    }

    private void recordUserHasAtLeastOneEntryInScoreBoardForLevel(int userId, int level) {
        RECORDED_USERS_BY_LEVEL.add(new Pair<>(level, userId).hashCode());
    }

    private boolean isUserAlreadyRecordedInLevel(int userId, int level) {
        return RECORDED_USERS_BY_LEVEL.contains(new Pair<>(level, userId).hashCode());
    }
}
