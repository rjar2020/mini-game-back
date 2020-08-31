package com.minigame.service;

import com.minigame.dao.LevelStore;

import java.util.Optional;
import java.util.stream.Collectors;

public class LevelScoreBoardService {

    private static final int MAX_SCORES_AMOUNT_RECODED_PER_LEVEL = 15;
    private final LevelStore levelStore;

    public LevelScoreBoardService(LevelStore levelStore) {
        this.levelStore = levelStore;
    }

    public void saveScore(int userId, int level, int score) {
        levelStore.saveScore(userId, level, score);
        shrinkLevelToMaxScoreAmountAllowed(level);
    }

    public Optional<String> getHighestScoresForLevel(int levelId) {
        return levelStore.retrieveScoreBoardForLevel(levelId)
                .map(integerIntegerMap ->
                        integerIntegerMap
                                .stream()
                                .map(entry -> entry.getLeft() + "=" + entry.getRight())
                                .collect(Collectors.joining(",")));
    }

    private void shrinkLevelToMaxScoreAmountAllowed(int level) {
        levelStore.retrieveScoreBoardForLevel(level).ifPresent(levelScoreBoard -> {
            if(levelScoreBoard.size() > MAX_SCORES_AMOUNT_RECODED_PER_LEVEL) {
                levelStore.removeLowestScoreForLevel(level);
            }
        });
    }
}
