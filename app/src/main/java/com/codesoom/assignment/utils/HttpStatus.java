package com.codesoom.assignment.utils;

public enum HttpStatus {
  OK(200),
  CREATE(201),
  NO_CONTENT(204),
  BAD_REQUEST(400),
  NOT_FOUND(404);

  private final int code;

  HttpStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
