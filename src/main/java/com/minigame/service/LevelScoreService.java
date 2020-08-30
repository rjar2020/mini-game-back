package com.minigame.service;

import com.minigame.dao.LevelStore;

public class LevelScoreService {

    private final LevelStore levelStore;

    public LevelScoreService(LevelStore levelStore) {
        this.levelStore = levelStore;
    }

    public void saveScore(int userId, int level, int score) {
        levelStore.saveScore(userId, level, score);
    }
}
