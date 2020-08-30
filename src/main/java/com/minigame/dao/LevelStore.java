package com.minigame.dao;

import com.minigame.api.util.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class LevelStore {

    private static final LevelStore LEVEL_STORE = new LevelStore();
    private static final Map<Integer, Set<Pair<Integer, Integer>>> MAX_SCORE_BY_LEVEL = new ConcurrentHashMap<>();

    private LevelStore() { }

    public static LevelStore getInstance() {
        return LEVEL_STORE;
    }

    public void saveScore(int userId, int level, int score) {
        if(Objects.isNull(MAX_SCORE_BY_LEVEL.get(level))) {
            MAX_SCORE_BY_LEVEL.putIfAbsent(
                    level,
                    new TreeSet<>((p1, p2) -> p2.getRight().compareTo(p1.getRight())));
            MAX_SCORE_BY_LEVEL.get(level).add(new Pair<>(userId, score));
        } else {
            addScoreWithoutUserDuplication(userId, level, score);
        }
    }

    public Optional<Set<Pair<Integer, Integer>>> retrieveScoresForLevel(int level) {
        return Optional.ofNullable(MAX_SCORE_BY_LEVEL.get(level));
    }

    private void addScoreWithoutUserDuplication(int userId, int level, int score) {
        if (userAlreadyHaveRecordedScore(userId, level)) {
            var formerUserMaxScore = MAX_SCORE_BY_LEVEL.get(level)
                    .stream()
                    .filter(oldScore -> userId == oldScore.getLeft() && score > oldScore.getRight())
                    .findAny();
            if(formerUserMaxScore.isPresent()) {
                MAX_SCORE_BY_LEVEL.get(level).remove(formerUserMaxScore.get());
                MAX_SCORE_BY_LEVEL.get(level).add(new Pair<>(userId, score));
            }
        } else {
            MAX_SCORE_BY_LEVEL.get(level).add(new Pair<>(userId, score));
        }
    }

    private boolean userAlreadyHaveRecordedScore(int userId, int level) {
        return MAX_SCORE_BY_LEVEL.get(level)
                .stream()
                .anyMatch(oldScore -> userId == oldScore.getLeft());
    }

}
