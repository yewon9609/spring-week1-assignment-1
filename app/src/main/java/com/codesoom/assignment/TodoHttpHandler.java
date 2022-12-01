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
    private final TodoHttpController todoHttpController;

    public TodoHttpHandler() {
        this.todoHttpController = new TodoHttpController();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            final String requestMethod = exchange.getRequestMethod();
            final String path = exchange.getRequestURI().getPath().substring(1);
            OutputStream outputStream = new ByteArrayOutputStream();
            InputStream requestBody = exchange.getRequestBody();
            OutputStream responseBody = exchange.getResponseBody();

            validateId(exchange, objectMapper, path, outputStream);
            getTasks(exchange, objectMapper, requestMethod, path, outputStream, responseBody);

            if ("GET".equals(requestMethod) && path.contains("tasks") && path.split("/").length > 1){
                getTask(exchange, objectMapper, path, outputStream, requestBody, responseBody);
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

            closeAll(outputStream, requestBody, responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateId(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream) throws IOException {
        try {
            if (path.split("/").length <= 1) {return ;}
        } catch (Exception e) {
            objectMapper.writeValue(outputStream, Arrays.asList());
            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.getCode(), outputStream.toString().getBytes().length);
            e.printStackTrace();
        }
    }

    // CRUD 겹치는 부분 추출해서 통합하는 최적화 작업 진행중
//    private void request(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
//        final String id = path.split("/")[1]; // delete, update ,getTask
//
//        final String content = new BufferedReader(new InputStreamReader(requestBody))
//                .lines()
//                .collect(Collectors.joining("\n"));
//
//        if (content.isBlank()) {
//            return;
//        }
//        Task body = objectMapper.readValue(content, Task.class);
//        body.setId(Long.parseLong(id));
//
//
//
//        if (!todoHttpController.isExist(id)) {
//            objectMapper.writeValue(outputStream, Arrays.asList());
//            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.getCode(), 0);
//            closeAll(outputStream, requestBody, responseBody);
//            return;
//        }
//
//    }

    private void getTask(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
        final String id = path.split("/")[1];

        if (todoHttpController.getTasks().isEmpty() || !todoHttpController.isExist(id)) {
            objectMapper.writeValue(outputStream, Arrays.asList());
            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.getCode(), outputStream.toString().getBytes().length);
            responseBody.write(outputStream.toString().getBytes());

            closeAll(outputStream, requestBody, responseBody);
            return;
        }

        objectMapper.writeValue(outputStream, todoHttpController.getTask(id));
        exchange.sendResponseHeaders(HttpStatus.OK.getCode(), outputStream.toString().getBytes().length);
        responseBody.write(outputStream.toString().getBytes());
    }

    private void getTasks(HttpExchange exchange, ObjectMapper objectMapper, String requestMethod, String path, OutputStream outputStream, OutputStream responseBody) throws IOException {
        if ("GET".equals(requestMethod) && path.contains("tasks") && path.split("/").length == 1){
            objectMapper.writeValue(outputStream, todoHttpController.getTasks());
            exchange.sendResponseHeaders(HttpStatus.OK.getCode(), outputStream.toString().getBytes().length);
            responseBody.write(outputStream.toString().getBytes());
        }
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
            closeAll(outputStream, requestBody, responseBody);
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
            closeAll(outputStream, requestBody, responseBody);
            return;
        }

        todoHttpController.delete(id);
        exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.getCode(), 0);
    }

    private void closeAll(OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
        requestBody.close();
        responseBody.flush();
        responseBody.close();
        outputStream.flush();
        outputStream.close();
    }
}