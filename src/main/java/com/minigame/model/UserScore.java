package com.minigame.model;

import java.util.Objects;
import java.util.StringJoiner;

public class UserScore {

    private final Integer userId;
    private final Integer score;

    public UserScore(Integer userId, Integer score) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(score);
        this.userId = userId;
        this.score = score;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserScore userScore = (UserScore) o;
        return Objects.equals(getUserId(), userScore.getUserId()) &&
                Objects.equals(getScore(), userScore.getScore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getScore());
    }

    //Custom toString. Be mindful when changing it
    @Override
    public String toString() {
        return new StringJoiner("=").add(String.valueOf(userId)).add(String.valueOf(score)).toString();
    }
}
