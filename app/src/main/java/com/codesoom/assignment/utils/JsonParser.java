package com.codesoom.assignment.utils;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class JsonParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Task toTask(String content) throws JsonProcessingException {
    return objectMapper.readValue(content, Task.class);
  }

  public static String toJson(Task task) {
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      objectMapper.writeValue(outputStream, task);
      return outputStream.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String toJson(List<Task> tasks) {
    try (OutputStream outputStream = new ByteArrayOutputStream()) {
      objectMapper.writeValue(outputStream, tasks);
      return outputStream.toString();
    }catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
