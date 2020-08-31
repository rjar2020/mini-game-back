package com.minigame.service;

import com.minigame.dao.LevelStore;
import com.minigame.model.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LevelScoreBoardServiceShould {

    private final LevelScoreBoardService levelScoreBoardService = new LevelScoreBoardService(LevelStore.getInstance());

    @Test
    void retrieveOnlyTheHighestScoreForSameUserByLevel() {
        // Given
        var userId = 24;
        var levelId = 1_000_000;
        var lowerScore = 3_000;
        var midScore = 5_000;
        var highestScore = 7_000;
        // When
        levelScoreBoardService.saveScore(userId, levelId, midScore);
        levelScoreBoardService.saveScore(userId, levelId, highestScore);
        levelScoreBoardService.saveScore(userId, levelId, lowerScore);
        levelScoreBoardService.saveScore(userId, levelId, highestScore);
        // Then
        assertEquals(userId+"="+highestScore,
                levelScoreBoardService.getHighestScoresForLevel(levelId).orElseThrow());
    }

    @Test
    void retrieveScoreBoardInDescendingOrderByLevel() {
        // Given
        var levelId = Math.abs(new Random().nextInt());
        var higherScore = new Pair<>(Math.abs(new Random().nextInt()), 500_000);
        var lowerScore = new Pair<>(Math.abs(new Random().nextInt()), 5_000);
        var midScore = new Pair<>(Math.abs(new Random().nextInt()), 6_000);
        var otherMidScore = new Pair<>(Math.abs(new Random().nextInt()), 100_000);
        var moreMidScores = new Pair<>(Math.abs(new Random().nextInt()), 499_999);
        var scoreList = List.of(midScore, lowerScore, moreMidScores, higherScore, otherMidScore);
        var sortedScores = List.of(higherScore, moreMidScores, otherMidScore, midScore, lowerScore);
        // When
        scoreList.forEach(userScore -> levelScoreBoardService.saveScore(userScore.getLeft(), levelId, userScore.getRight()));
        // Then
        assertEquals(
                sortedScores.stream()
                        .map(userScore -> userScore.getLeft()+"="+userScore.getRight())
                        .collect(Collectors.joining(",")),
                levelScoreBoardService.getHighestScoresForLevel(levelId)
                        .orElseThrow()
        );
    }

    @Test
    void retrieveAtMostFifteenScoresInScoreBoardByLevel() {
        // Given
        var levelId = Math.abs(new Random().nextInt());
        // When
        IntStream.range(0, 100)
                .mapToObj(i -> new Pair<>(Math.abs(new Random().nextInt()), Math.abs(new Random().nextInt())))
                .collect(Collectors.toList())
                .forEach(userScore -> levelScoreBoardService.saveScore(userScore.getLeft(), levelId, userScore.getRight()));
        // Then
        assertEquals(15,
                levelScoreBoardService.getHighestScoresForLevel(levelId)
                        .orElseThrow()
                        .replace("=", "")
                        .split(",")
                        .length
        );
    }

    @Test
    void retrieveEmptyScoreBoardWhenNoScoreStoredByLevel() {
        assertFalse(levelScoreBoardService.getHighestScoresForLevel(2).isPresent());
    }
}
