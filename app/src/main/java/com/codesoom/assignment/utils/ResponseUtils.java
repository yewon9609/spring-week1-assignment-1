package com.codesoom.assignment.utils;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseUtils {

  public static void sendResponse(HttpStatus status, String content, HttpExchange exchange) {
    try (OutputStream responseBody = exchange.getResponseBody()) {
      exchange.sendResponseHeaders(status.getCode(), content.getBytes().length);
      responseBody.write(content.getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



}
