package com.minigame.dao;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class LevelStore {

    private static final LevelStore LEVEL_STORE = new LevelStore();
    private static final Map<Integer, Map<Integer, Integer>> MAX_SCORE_BY_LEVEL = new ConcurrentHashMap<>();

    private LevelStore() { }

    public static LevelStore getInstance() {
        return LEVEL_STORE;
    }

    public void saveScore(int userId, int level, int score) {
        if(Objects.isNull(MAX_SCORE_BY_LEVEL.get(level))) {
            MAX_SCORE_BY_LEVEL.putIfAbsent(level, new TreeMap<>(Comparator.reverseOrder()));
        }
        MAX_SCORE_BY_LEVEL.get(level).putIfAbsent(score, userId);
    }

    public Optional<Map<Integer, Integer>> retrieveScoresForLevel(int level) {
        return Optional.ofNullable(MAX_SCORE_BY_LEVEL.get(level));
    }

}
