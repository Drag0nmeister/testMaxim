package org.example.taskService.service;

import org.example.taskService.model.Task;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    Task createTask(Task task);

    Task updateTask(Task task);

    void toggleTaskCompletion(Long id);


    void deleteTask(Long id);

    List<Task> getTasksByDateAndCompletionStatus(LocalDate date, boolean completed);

    List<Task> getTasksByWeekAndCompletionStatus(boolean completed);

    List<Task> getTasksByMonthAndCompletionStatus(boolean completed);
}
