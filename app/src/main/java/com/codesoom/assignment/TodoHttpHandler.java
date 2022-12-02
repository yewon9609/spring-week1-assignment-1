package com.codesoom.assignment;

import com.codesoom.assignment.controller.TodoHttpController;
import com.codesoom.assignment.models.HttpStatus;
import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TodoHttpHandler implements HttpHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final TodoHttpController todoHttpController = new TodoHttpController();;

    @Override
    public void handle(HttpExchange exchange) throws IOException, IllegalArgumentException {
        try (InputStream requestBody = exchange.getRequestBody();
             OutputStream responseBody = exchange.getResponseBody();
             OutputStream outputStream = new ByteArrayOutputStream()){

            final String requestMethod = exchange.getRequestMethod();
            final String path = exchange.getRequestURI().getPath().substring(1);

            if ("GET".equals(requestMethod) && path.contains("tasks")){
                get(exchange, objectMapper, path, outputStream, responseBody);
            }
            if ("POST".equals(requestMethod) && path.equals("tasks")) {
                insert(exchange, objectMapper, requestBody, responseBody);
            }
            if (("PUT".equals(requestMethod) || "PATCH".equals(requestMethod)) && path.contains("tasks")) {
                update(exchange, objectMapper, path, outputStream, requestBody, responseBody);
            }
            if ("DELETE".equals(requestMethod) && path.contains("tasks")) {
                delete(exchange, objectMapper, path, outputStream, requestBody, responseBody);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("GET, POST, PUT, PATCH, DELETE 요청만 가능합니다.");
        }
    }

    private void get(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, OutputStream responseBody) throws IOException {
        int rCode = HttpStatus.OK.getCode();
        Object task = null;

        try{
            String id = path.split("/")[1];
            if (!todoHttpController.isExist(id)) {
                rCode = HttpStatus.NOT_FOUND.getCode();
                task = Arrays.asList();
            }else {
                task = todoHttpController.getTask(id);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            task = todoHttpController.getTasks();
        }
        objectMapper.writeValue(outputStream, task);
        exchange.sendResponseHeaders(rCode, outputStream.toString().getBytes().length);
        responseBody.write(outputStream.toString().getBytes());
    }

    private void insert(HttpExchange exchange, ObjectMapper objectMapper, InputStream requestBody, OutputStream responseBody) throws IOException {
        String content = new BufferedReader(new InputStreamReader(requestBody))
                .lines()
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            return;
        }
        Task inserted = todoHttpController.insert(objectMapper.readValue(content, Task.class));
        exchange.sendResponseHeaders(HttpStatus.CREATED.getCode(), inserted.toString().getBytes().length);
        responseBody.write(inserted.toString().getBytes());
    }

    private void update(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
        final String id = path.split("/")[1];

        final String content = new BufferedReader(new InputStreamReader(requestBody))
                .lines()
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            return;
        }
        Task body = objectMapper.readValue(content, Task.class);

        if (!todoHttpController.isExist(id)) {
            objectMapper.writeValue(outputStream, Arrays.asList());
            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.getCode(), 0);
            return;
        }

        Task updated = todoHttpController.update(body);
        exchange.sendResponseHeaders(HttpStatus.OK.getCode(), updated.toString().getBytes().length);
        responseBody.write(updated.toString().getBytes());
    }

    private void delete(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
        final String id = path.split("/")[1];
        if (!todoHttpController.isExist(id)) {
            objectMapper.writeValue(outputStream, Arrays.asList());
            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.getCode(), 0);
            return;
        }

        todoHttpController.delete(id);
        exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.getCode(), 0);
    }
}