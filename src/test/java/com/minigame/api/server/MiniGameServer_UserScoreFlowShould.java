package com.minigame.api.server;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiniGameServer_UserScoreFlowShould extends MiniGameServerTestTemplate {

    @Test
    void handleUserScoreRequestWhenValidPathLevelIdAndSessionKey() {
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var score = "1500";
        var sessionKey = getLoginSessionKey(userId);
        postUserScoreAndAssertResponse(
                levelId,
                sessionKey,
                score,
                httpResponse -> assertEquals(
                        200,
                        httpResponse.statusCode(),
                        "Successful call expected. levelId="+levelId+". sessionKey="+sessionKey + " Response: " + httpResponse.body().toString())
        );
    }

    @Test
    void rejectPostUserScoreWhenWrongLevelIdFormat() {
        var levelId = "-" + Math.abs(new Random().nextInt());
        var sessionKey = UUID.randomUUID().toString();
        var score = "1500";
        postUserScoreAndAssertResponse(
                levelId,
                sessionKey,
                score,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for negative levelId="+levelId+". sessionKey="+sessionKey)
        );
        var longLevelId = String.valueOf(Integer.MAX_VALUE + 1L);
        postUserScoreAndAssertResponse(
                longLevelId,
                sessionKey,
                score,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for long levelId="+longLevelId+". sessionKey="+sessionKey)
        );
    }

    @Test
    void rejectPostUserScoreWhenWrongSessionKeyFormat() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = UUID.randomUUID().toString().replace("-", "");
        var score = "1500";
        postUserScoreAndAssertResponse(
                levelId,
                sessionKey,
                score,
                httpResponse -> assertEquals(
                        404,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for non UUID sessionKey levelId="+levelId+". sessionKey="+sessionKey)
        );
    }

    @Test
    void rejectPostUserScoreWhenInvalidOrExpiredSessionKey() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey =  UUID.randomUUID().toString();
        var score = "1500";
        postUserScoreAndAssertResponse(
                levelId,
                sessionKey,
                score,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for not authenticated user. levelId="+levelId+". sessionKey="+sessionKey)
        );
    }
}
