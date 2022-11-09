package com.codesoom.assignment;

import com.codesoom.assignment.models.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TodoHttpHandler implements HttpHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Task> tasks = new ArrayList<>();
    private Long idNum = 1L;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String body = getBody(exchange);
        String id = path.replaceAll("[^0-9]", "");


        String content = ""; // 인텔리제이 콘솔에 나오는 것

        if (method.equals("GET") && path.contains("/tasks")){
            // 여기서 오류 나기 시작함 : socket hang up < 확인 핋요
            if (id.equals("")) {content = tasksToJSON();}
            else { content = taskToJSON(id); }
        }

        if (method.equals("POST") && path.contains("/tasks")){
            Task task = toTask(body);
            task.setId(idNum);
            tasks.add(task);
            content = taskToJSON(idNum.toString());
            idNum++;

        }

        if ((method.equals("PATCH") || method.equals("PUT")) && path.contains("/tasks")){
            // 제목 수정
            Task task = getTask(id);
            task.setTitle(toTask(body).getTitle());
            content = taskToJSON(id);
        }

        if (method.equals("DELETE") && path.contains("/tasks")){
            // 삭제
            tasks.remove(Integer.parseInt(id) - 1);
        }

        exchange.sendResponseHeaders(200, content.getBytes().length);
                                    // getBytes() 를 하는 이유 : 영어나 숫자는 상관없지 한국어에서 오류가 생길 수 있음
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(content.getBytes()); // write 가 byte[] 를 매개변수로 받음
        outputStream.flush();
        outputStream.close();
    }

    // 바디를 String 으로 읽어오는 메소드
    // 바디 ? : 한 칸 띄고 title="어쩌고" 이 부분임
    private String getBody(HttpExchange exchange){
        InputStream inputStream = exchange.getRequestBody();
        String body = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
        return body;
    }

    private Task toTask(String content) throws JsonProcessingException {
        return objectMapper.readValue(content, Task.class);
    }

    private Task getTask(String id){
        try{
            return tasks.get(Integer.parseInt(id) - 1);
        } catch (Exception e) {
            return null;
        }
       //Optional<Task> optional = Optional.ofNullable(tasks.get(Integer.parseInt(id) - 1));
        //return optional.orElse(null);

//        return tasks.stream()
//                .filter(task -> task.getId().equals(Long.parseLong(id)))
//                .findFirst()
//                .orElse(null);
    }

    private String tasksToJSON() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, tasks);
        return outputStream.toString();
    }

    private String taskToJSON(String id) throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();
        objectMapper.writeValue(outputStream, getTask(id));// 여기서 오류남
        return outputStream.toString();
    }
}
