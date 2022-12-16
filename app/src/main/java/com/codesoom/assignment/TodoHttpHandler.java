package com.codesoom.assignment;

import com.codesoom.assignment.controller.TodoHttpController;
import com.codesoom.assignment.models.HttpMethods;
import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.springframework.http.HttpStatus;

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
    private final TodoHttpController todoHttpController = new TodoHttpController();
    private static final int firstIndex = 1;

    @Override
    public void handle(HttpExchange exchange) throws IOException, IllegalArgumentException {
        try (InputStream requestBody = exchange.getRequestBody();
             OutputStream responseBody = exchange.getResponseBody();
             OutputStream outputStream = new ByteArrayOutputStream()){

            final String requestMethod = exchange.getRequestMethod();
            final String path = exchange.getRequestURI().getPath().substring(1);
            if (path.contains("tasks")){
                switch(HttpMethods.valueOf(requestMethod)){
                    case GET:
                        get(exchange, objectMapper, path, outputStream, responseBody);
                        break;
                    case POST:
                        insert(exchange, objectMapper, requestBody, responseBody);
                        break;
                    case PUT: case PATCH:
                        update(exchange, objectMapper, path, outputStream, requestBody, responseBody);
                        break;
                    case DELETE:
                        delete(exchange, objectMapper, path, outputStream);
                        break;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("GET, POST, PUT, PATCH, DELETE 요청만 가능합니다.");
        }
    }

    private void validateId(HttpExchange exchange, ObjectMapper objectMapper, OutputStream outputStream, String id) throws IOException {
        if (!todoHttpController.isExist(id)) {
            objectMapper.writeValue(outputStream, Arrays.asList());
            exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.value(), 0);
            return;
        }
    }

    private void response(HttpExchange exchange, OutputStream responseBody, Object content, int rCode) throws IOException {
        exchange.sendResponseHeaders(rCode, content.toString().getBytes().length);
        responseBody.write(content.toString().getBytes());
    }

    private void get(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, OutputStream responseBody) throws IOException {
        int rCode = HttpStatus.OK.value();
        Object task = null;
        try{
            String id = path.split("/")[firstIndex];
            validateId(exchange, objectMapper, outputStream, id);
            task = todoHttpController.getTask(id);
        } catch (ArrayIndexOutOfBoundsException e) {
            task = todoHttpController.getTasks();
        }
        objectMapper.writeValue(outputStream, task);
        response(exchange, responseBody, outputStream, rCode);
    }

    private void insert(HttpExchange exchange, ObjectMapper objectMapper, InputStream requestBody, OutputStream responseBody) throws IOException {
        String content = new BufferedReader(new InputStreamReader(requestBody))
                .lines()
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            return;
        }
        Task inserted = todoHttpController.insert(objectMapper.readValue(content, Task.class));
        response(exchange, responseBody, inserted, HttpStatus.CREATED.value());
    }

    private void update(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream, InputStream requestBody, OutputStream responseBody) throws IOException {
        final String id = path.split("/")[firstIndex];
        final String content = new BufferedReader(new InputStreamReader(requestBody))
                .lines()
                .collect(Collectors.joining("\n"));
        if (content.isBlank()) {
            return;
        }
        validateId(exchange, objectMapper, outputStream, id);
        Task body = objectMapper.readValue(content, Task.class);
        Task updated = todoHttpController.update(body);
        response(exchange, responseBody, updated, HttpStatus.OK.value());
    }

    private void delete(HttpExchange exchange, ObjectMapper objectMapper, String path, OutputStream outputStream) throws IOException {
        final String id = path.split("/")[firstIndex];
        validateId(exchange, objectMapper, outputStream, id);
        todoHttpController.delete(id);
        exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.value(), 0);
    }
}