package com.minigame.api.server;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MiniGameServer_LoginFlowShould extends MiniGameServerTestTemplate {

    @Test
    void handleLoginRequestWhenRightPathAndUserId() {
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        getLoginAndAssertResponse(
                userId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                            "Successful call expected. userId="+userId);
                    assertTrue(httpResponse.body().toString().matches(SESSION_KEY_PATTERN),
                            "Successful call expected with sessionKey being UUID. sessionKey="+httpResponse.body().toString());
                }
        );
    }

    @Test
    void returnSameSessionKeyForUserWhenActive() {
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = getLoginSessionKey(userId);
        getLoginAndAssertResponse(
                userId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                            "Successful call expected. userId="+userId);
                    assertTrue(httpResponse.body().toString().matches(SESSION_KEY_PATTERN),
                            "Successful call expected with sessionKey being UUID. sessionKey="+httpResponse.body().toString());
                    assertEquals(sessionKey, httpResponse.body().toString(),
                            "Active session key should be retrieved.");
                }
        );
    }

    @Test
    void rejectLoginRequestWhenWrongUserIdFormat() {
        var userId = "-" + Math.abs(new Random().nextInt());
        getLoginAndAssertResponse(
                userId,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for negative user id. userId="+userId)
        );
        var longUserId = String.valueOf(Integer.MAX_VALUE + 1L);
        getLoginAndAssertResponse(
                longUserId,
                httpResponse -> assertEquals(
                        400,
                        httpResponse.statusCode(),
                        "Unsuccessful call expected for long user id. userId="+longUserId)
        );
    }
}
