package com.codesoom.assignment.models;

import java.util.concurrent.atomic.AtomicLong;

public class Task {
    private Long id;
    private String title;
    private static AtomicLong sequence = new AtomicLong();

    public Task(){}

    public Task(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Long incrementId(){
        return sequence.addAndGet(1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Task updateTitle(String title) {
        return new Task(this.id, title);
    }

    @Override
    public String toString() {
        return "{" + "\"id\":" + id +
                ",\"title\":\"" + title + '\"' +
                '}';
    }
}
