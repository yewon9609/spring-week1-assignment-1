package com.codesoom.assignment.models;

public enum HttpMethods {
    /**
     * @see https://www.rfc-editor.org/rfc/rfc7231
     */

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE")
    ;
    private String method;

    HttpMethods(String method) {
        this.method = method;
    }
}
