package com.minigame.api.dto;

import com.minigame.model.util.Pair;

import java.util.Objects;
import java.util.StringJoiner;

public class HttpResponseDTO {
    private final Integer httpCode;
    private String body;

    public HttpResponseDTO(Integer httpCode, String body) {
        this.httpCode = httpCode;
        this.body = body;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }

    public Pair<Integer, String> toPair() {
        return new Pair<>(httpCode, body);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponseDTO that = (HttpResponseDTO) o;
        return Objects.equals(getHttpCode(), that.getHttpCode()) &&
                Objects.equals(getBody(), that.getBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHttpCode(), getBody());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResponseDTO.class.getSimpleName() + "[", "]")
                .add("httpCode=" + httpCode)
                .add("body='" + body + "'")
                .toString();
    }
}
