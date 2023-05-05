package com.codesoom.assignment.models;

public class Task {

  private Long id;

  private String title;

  public void updateId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Task updateTitle(String title) {
    this.title = title;
    return this;
  }

  private Task() {
  }

  @Override
  public String toString() {
    return "Task{" +
        "id=" + id +
        ", title='" + title + '\'' +
        '}';
  }
}
