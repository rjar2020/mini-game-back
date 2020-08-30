package com.minigame.api.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MiniGameServerShould {

    private static final String LOCAL_HOST = "http://localhost:8000";
    private static final String LOGIN_URI = LOCAL_HOST+"/{userId}/login";
    private static final String USER_SCORE_URI = LOCAL_HOST+"/{levelId}/score?sessionkey={sessionKey}";
    private static final String HIGH_SCORE_URI = LOCAL_HOST+"/{levelId}/highscorelist";
    private static final String SESSION_KEY_PATTERN = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

    @BeforeAll
    void setUp() {
        MiniGameServer.SERVER.start();
    }

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

    @Test
    void handleHighScoreRequestWhenRightPathAndLevelId() {
        var levelId = String.valueOf(Math.abs(new Random().nextInt()));
        var userId = String.valueOf(Math.abs(new Random().nextInt()));
        var sessionKey = getLoginSessionKey(userId);
        var score = "1500";
        postUserScore(levelId, sessionKey,score);
        getHighScoreAndAssertResponse(
                levelId,
                httpResponse -> {
                    assertEquals(200, httpResponse.statusCode(),
                        "Successful call expected. levelId="+levelId);
                    assertEquals(userId+"="+score, httpResponse.body().toString(),
                            "Successful call expected. response: "+userId+"="+score);
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

    private void getLoginAndAssertResponse(String userId, Consumer<HttpResponse<?>> httpResponseAssertions) {
        getAndAssertResponse(URI.create(LOGIN_URI.replace("{userId}", userId)), httpResponseAssertions);
    }

    private String getLoginSessionKey(String userId) {
        return getAndReturnResponse(URI.create(LOGIN_URI.replace("{userId}", userId)));
    }

    private void getHighScoreAndAssertResponse(String levelId, Consumer<HttpResponse<?>> httpResponseAssertions) {
        getAndAssertResponse(URI.create(HIGH_SCORE_URI.replace("{levelId}", levelId)), httpResponseAssertions);
    }

    private String postUserScore(String levelId, String sessionKey, String score) {
        return HttpClient.newHttpClient()
                .sendAsync(createUserScoreRequest(levelId, sessionKey, score), HttpResponse.BodyHandlers.ofString())
                .join()
                .body();
    }

    private void getAndAssertResponse(URI uri, Consumer<HttpResponse<?>> httpResponseAssertions) {
        HttpClient.newHttpClient()
                .sendAsync(createGetRequest(uri), HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponseAssertions)
                .join();
    }

    private String getAndReturnResponse(URI uri) {
        return HttpClient.newHttpClient()
                .sendAsync(createGetRequest(uri), HttpResponse.BodyHandlers.ofString())
                .join().body();
    }

    private HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
    }

    private void postUserScoreAndAssertResponse(String levelId, String sessionKey, String score, Consumer<HttpResponse<?>> httpResponseAssertions) {
        HttpClient.newHttpClient()
                .sendAsync(createUserScoreRequest(levelId, sessionKey, score), HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponseAssertions)
                .join();
    }

    private HttpRequest createUserScoreRequest(String levelId, String sessionKey, String score) {
        return HttpRequest.newBuilder()
                .uri(URI.create(USER_SCORE_URI.replace("{levelId}", levelId).replace("{sessionKey}", sessionKey)))
                .timeout(Duration.ofSeconds(3))
                .POST(HttpRequest.BodyPublishers.ofString(score))
                .build();
    }

    @AfterAll
    void cleanUp() {
        MiniGameServer.SERVER.stop();
    }
}
