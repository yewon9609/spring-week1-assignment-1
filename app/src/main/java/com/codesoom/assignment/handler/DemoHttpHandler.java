package com.codesoom.assignment.handler;

import com.codesoom.assignment.models.Task;
import com.codesoom.assignment.repositroy.TaskRepository;
import com.codesoom.assignment.utils.HttpStatus;
import com.codesoom.assignment.utils.JsonParser;
import com.codesoom.assignment.utils.RequestUtils;
import com.codesoom.assignment.utils.ResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class DemoHttpHandler implements HttpHandler {

  private final TaskRepository taskRepository;

  public DemoHttpHandler(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();
    URI uri = exchange.getRequestURI();
    String path = uri.getPath();
    String content = RequestUtils.readRequestBody(exchange);

    switch (method) {
      case "GET" -> getTasks(exchange);
      case "POST" -> createTask(exchange, content);
      case "PUT" -> updateTaskTitle(exchange, path, content);
    }

  }

  private void getTasks(HttpExchange exchange) {
    List<Task> tasks = taskRepository.getTaskList();
    ResponseUtils.sendResponse(HttpStatus.OK, JsonParser.toJson(tasks), exchange);
  }

  private void createTask(HttpExchange exchange, String content) throws JsonProcessingException {
    Task savedTask = taskRepository.saveTask(JsonParser.toTask(content));
    ResponseUtils.sendResponse(HttpStatus.CREATE, JsonParser.toJson(savedTask), exchange);
  }

  private void updateTaskTitle(HttpExchange exchange, String path, String content)
      throws JsonProcessingException {
    String title = JsonParser.toTask(content).getTitle();
    long id = RequestUtils.getIdFromPathVariable(path)
        .orElseThrow(() -> new IllegalArgumentException("id를 입력해주세요"));

    Task updatedTask = taskRepository.updateTask(id, title);
    ResponseUtils.sendResponse(HttpStatus.OK, JsonParser.toJson(updatedTask), exchange);
  }


}
