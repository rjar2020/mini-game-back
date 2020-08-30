package com.minigame.service;

import com.minigame.dao.LevelStore;

import java.util.Optional;
import java.util.stream.Collectors;

public class LevelScoreService {

    private final LevelStore levelStore;

    public LevelScoreService(LevelStore levelStore) {
        this.levelStore = levelStore;
    }

    public void saveScore(int userId, int level, int score) {
        levelStore.saveScore(userId, level, score);
    }

    public Optional<String> getHighestScoresForLevel(int levelId) {
        return levelStore.retrieveScoresForLevel(levelId)
                .map(integerIntegerMap ->
                        integerIntegerMap.entrySet()
                                .stream()
                                .map(entry -> entry.getValue() + "=" + entry.getKey())
                                .collect(Collectors.joining(",")));
    }
}
