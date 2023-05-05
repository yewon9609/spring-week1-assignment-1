package com.codesoom.assignment.utils;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestUtils {

  public static String readRequestBody(HttpExchange exchange) {
    try (InputStream requestBody = exchange.getRequestBody()) {
      return new BufferedReader(new InputStreamReader(requestBody))
          .lines()
          .collect(Collectors.joining("/n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Optional<Long> getIdFromPathVariable(String path) {
    String[] splitPath = path.split("/");
    if (splitPath.length > 2) {
      try {
        return Optional.of(Long.parseLong(splitPath[2]));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("id가 숫자가 아닙니다");
      }
    }
    return Optional.empty();
  }
}
