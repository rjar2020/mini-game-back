package com.minigame.api.server;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiniGameServer_LevelHighScoreFlowShould extends MiniGameServerTestTemplate {

    @Test
    void handleHighScoreRequestWhenRightPathAndLevelId() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = getLoginSessionKey(userId);
        postUserScore(levelId, sessionKey,"800");
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                        "Successful call expected. levelId="+levelId);
                    assertEquals(userId+"=800", httpResponse.body().toString(),
                            "Successful call expected. response: "+userId+"=800");
                }
        );
    }

    @Test
    void handleHighScoreRequestKeepingOnlyHighestScoreForUserId() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = getLoginSessionKey(userId);
        postUserScore(levelId, sessionKey,"1500");
        postUserScore(levelId, sessionKey,"2000");
        postUserScore(levelId, sessionKey,"800");
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                            "Successful call expected. levelId="+levelId);
                    assertEquals(userId+"=2000", httpResponse.body().toString(),
                            "Successful call expected. response: "+userId+"=2000");
                }
        );
    }

    @Test
    void handleHighScoreRequestWhenMultipleUsersInLevelScoreBoard() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId2 = String.valueOf(Math.abs(new Random().nextInt()));
        var userId3 = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = getLoginSessionKey(userId);
        var sessionKey2 = getLoginSessionKey(userId2);
        var sessionKey3 = getLoginSessionKey(userId3);
        postUserScore(levelId, sessionKey,"1500");
        postUserScore(levelId, sessionKey2,"2000");
        postUserScore(levelId, sessionKey3,"800");
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                            "Successful call expected. levelId="+levelId);
                    assertEquals(userId2+"=2000,"+userId+"=1500,"+userId3+"=800", httpResponse.body().toString(),
                            "Successful call expected. response: "+userId2+"=2000,"+userId+"=1500,"+userId3+"=800");
                }
        );
    }

    @Test
    void handleHighScoreRequestAndReturn404AndEmptyStringWhenNoScoreFoundForRequestedLevel() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var score = "1500";
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> {
                    assertEquals(404, httpResponse.statusCode(),
                            "Unsuccessful call expected for a level without scores. levelId="+levelId);
                    assertEquals("", httpResponse.body().toString(),
                            "Unsuccessful call expected for a level without scores. levelId="+levelId);
                }
        );
    }

    @Test
    void rejectHighScoreRequestWhenWrongLevelIdFormat() {
        var levelId = "-" + Math.abs(new Random().nextInt());
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for negative levelId="+levelId)
        );
        var longLevelId = String.valueOf(Integer.MAX_VALUE + 1L);
        getHighScoreAndAssertResponse(
                longLevelId,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for long levelId="+longLevelId)
        );
    }
}
