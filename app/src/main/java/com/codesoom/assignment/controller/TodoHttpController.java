package com.codesoom.assignment.controller;

import com.codesoom.assignment.TodoHttpHandler;
import com.codesoom.assignment.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TodoHttpController {
    private List<Task> todoList;
    private Long id;
    public TodoHttpController(){
        this.todoList = new ArrayList<>();
        this.id = 0L;
    }

    public List<Task> getTasks(){
        return this.todoList;
    }

    public Task getTask(String id){
        return this.todoList.stream()
                .filter(t -> t.getId().equals(Long.parseLong(id)))
                .findFirst()
                .get();
    }

    public boolean isEmpty() {
        return this.todoList.size() == 0;
    }

    public Task insert(Task readValue) {
        readValue.setId(this.id++);
        this.todoList.add(readValue);
        return readValue;
    }

    public Task update(Task body) {
        for (Task todo : todoList) {
            if (todo.getId().equals(body.getId())) {
                todo.setTitle(body.getTitle());
                return todo;
            }
        }
        return body;
    }

    public boolean isExist(String id) {
        return todoList.stream().filter(t -> t.getId() == Long.parseLong(id)).count() != 0;
    }

    public void delete(String id) {
        todoList.removeIf(t -> t.getId() == Long.parseLong(id));
    }

}
