package com.codesoom.assignment.repositroy;

import com.codesoom.assignment.models.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskRepository {

  private Long id = 1L;
  private final List<Task> tasks = new ArrayList<>();

  public List<Task> getTaskList() {
    return new ArrayList<>(tasks);
  }

  public Task getTask(Long id) {
    return findTaskById(id);
  }

  private Task findTaskById(Long id) {
    return tasks.stream()
        .filter(task -> Objects.equals(task.getId(), id))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  public Task saveTask(Task task) {
    task.updateId(getCurrentId());
    tasks.add(task);
    return task;
  }

  private synchronized Long getCurrentId() {
    return id++;
  }

  public Task updateTask(Long id, String title) {
    return findTaskById(id).updateTitle(title);
  }

  public void deleteTask(Long id) {
    tasks.remove(findTaskById(id));
  }


}
