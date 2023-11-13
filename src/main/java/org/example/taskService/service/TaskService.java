package org.example.taskService.service;

import org.example.taskService.dto.TaskCreationRequest;
import org.example.taskService.dto.TaskUpdateRequest;
import org.example.taskService.dto.TaskResponse;

import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskCreationRequest taskRequest);

    TaskResponse updateTask(Long taskId, TaskUpdateRequest taskUpdate);

    void toggleTaskCompletion(Long id);

    void deleteTask(Long id);

    List<TaskResponse> getTasksByDateRangeAndCompletionStatus(LocalDateTime start, LocalDateTime end, boolean completed);

    List<TaskResponse> getTasksByDateAndCompletionStatus(LocalDateTime date, boolean completed);

    List<TaskResponse> getTasksByWeekAndCompletionStatus(boolean completed);

    List<TaskResponse> getTasksByMonthAndCompletionStatus(boolean completed);

    List<TaskResponse> getUpcomingTasksByWeekAndCompletionStatus(boolean completed);

    List<TaskResponse> getUpcomingTasksByMonthAndCompletionStatus(boolean completed);
}
