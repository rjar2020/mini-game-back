package com.minigame.api.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class MiniGameServerTestTemplate {

    protected static final String LOCAL_HOST = "http://localhost:8000";
    protected static final String LOGIN_URI = LOCAL_HOST+"/{userId}/login";
    protected static final String USER_SCORE_URI = LOCAL_HOST+"/{levelId}/score?sessionkey={sessionKey}";
    protected static final String HIGH_SCORE_URI = LOCAL_HOST+"/{levelId}/highscorelist";
    protected static final String SESSION_KEY_PATTERN = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

    @BeforeAll
    void setUp() {
        MiniGameServer.SERVER.start();
    }

    protected void getLoginAndAssertResponse(String userId, Consumer<HttpResponse<?>> httpResponseAssertions) {
        getAndAssertResponse(URI.create(LOGIN_URI.replace("{userId}", userId)), httpResponseAssertions);
    }

    protected String getLoginSessionKey(String userId) {
        return getAndReturnResponse(URI.create(LOGIN_URI.replace("{userId}", userId)));
    }

    protected void getHighScoreAndAssertResponse(String levelId, Consumer<HttpResponse<?>> httpResponseAssertions) {
        getAndAssertResponse(URI.create(HIGH_SCORE_URI.replace("{levelId}", levelId)), httpResponseAssertions);
    }

    protected String postUserScore(String levelId, String sessionKey, String score) {
        return HttpClient.newHttpClient()
                .sendAsync(createUserScoreRequest(levelId, sessionKey, score), HttpResponse.BodyHandlers.ofString())
                .join()
                .body();
    }

    protected void getAndAssertResponse(URI uri, Consumer<HttpResponse<?>> httpResponseAssertions) {
        HttpClient.newHttpClient()
                .sendAsync(createGetRequest(uri), HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponseAssertions)
                .join();
    }

    protected String getAndReturnResponse(URI uri) {
        return HttpClient.newHttpClient()
                .sendAsync(createGetRequest(uri), HttpResponse.BodyHandlers.ofString())
                .join().body();
    }

    protected HttpRequest createGetRequest(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
    }

    protected void postUserScoreAndAssertResponse(String levelId, String sessionKey, String score, Consumer<HttpResponse<?>> httpResponseAssertions) {
        HttpClient.newHttpClient()
                .sendAsync(createUserScoreRequest(levelId, sessionKey, score), HttpResponse.BodyHandlers.ofString())
                .thenAccept(httpResponseAssertions)
                .join();
    }

    protected HttpRequest createUserScoreRequest(String levelId, String sessionKey, String score) {
        return HttpRequest.newBuilder()
                .uri(URI.create(USER_SCORE_URI.replace("{levelId}", levelId).replace("{sessionKey}", sessionKey)))
                .timeout(Duration.ofSeconds(3))
                .POST(HttpRequest.BodyPublishers.ofString(score))
                .build();
    }
}
