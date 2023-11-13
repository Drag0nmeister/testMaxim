package org.example.taskService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.taskService.dto.TaskCreationRequest;
import org.example.taskService.dto.TaskUpdateRequest;
import org.example.taskService.dto.TaskResponse;
import org.example.taskService.exception.TaskNotFoundException;
import org.example.taskService.model.Task;
import org.example.taskService.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskCreationRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDate(LocalDateTime.now());
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);
        return convertToTaskResponse(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest taskUpdate) {
        return taskRepository.findById(taskId)
                .map(task -> {
                    task.setTitle(taskUpdate.getTitle());
                    task.setDescription(taskUpdate.getDescription());
                    return taskRepository.save(task);
                })
                .map(this::convertToTaskResponse)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    @Transactional
    public void toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public List<TaskResponse> getTasksByDateAndCompletionStatus(LocalDateTime date, boolean completed) {
        return taskRepository.findByDateAndCompleted(date, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByDateRangeAndCompletionStatus(LocalDateTime start, LocalDateTime end, boolean completed) {
        return taskRepository.findByDateBetweenAndCompleted(start, end, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<TaskResponse> getTasksByWeekAndCompletionStatus(boolean completed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusWeeks(1);
        return taskRepository.findByDateBetweenAndCompleted(weekAgo, now, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByMonthAndCompletionStatus(boolean completed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        return taskRepository.findByDateBetweenAndCompleted(monthAgo, now, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getUpcomingTasksByWeekAndCompletionStatus(boolean completed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekLater = now.plusWeeks(1);
        return taskRepository.findByDateBetweenAndCompleted(now, weekLater, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getUpcomingTasksByMonthAndCompletionStatus(boolean completed) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthLater = now.plusMonths(1);
        return taskRepository.findByDateBetweenAndCompleted(now, monthLater, completed)
                .stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse convertToTaskResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getDate(), task.isCompleted());
    }
}
