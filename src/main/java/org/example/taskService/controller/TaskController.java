package org.example.taskService.controller;

import jakarta.validation.Valid;
import org.example.taskService.dto.TaskCreationRequest;
import org.example.taskService.dto.TaskUpdateRequest;
import org.example.taskService.dto.TaskResponse;
import org.example.taskService.exception.InvalidIntervalException;
import org.example.taskService.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasksByDateAndCompletionStatus(@RequestParam LocalDateTime date, @RequestParam boolean completed) {
        return new ResponseEntity<>(taskService.getTasksByDateAndCompletionStatus(date, completed), HttpStatus.OK);
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<TaskResponse>> getTasksByIntervalAndCompletionStatus(
            @RequestParam(required = false) String interval,
            @RequestParam boolean completed,
            @RequestParam(required = false) String direction) {

        if ("today".equals(interval)) {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
            return new ResponseEntity<>(
                    taskService.getTasksByDateRangeAndCompletionStatus(startOfDay, endOfDay, completed),
                    HttpStatus.OK);
        } else if ("week".equals(interval)) {
            if ("future".equals(direction)) {
                return new ResponseEntity<>(taskService.getUpcomingTasksByWeekAndCompletionStatus(completed), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(taskService.getTasksByWeekAndCompletionStatus(completed), HttpStatus.OK);
            }
        } else if ("month".equals(interval)) {
            if ("future".equals(direction)) {
                return new ResponseEntity<>(taskService.getUpcomingTasksByMonthAndCompletionStatus(completed), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(taskService.getTasksByMonthAndCompletionStatus(completed), HttpStatus.OK);
            }
        } else {
            throw new InvalidIntervalException("Invalid interval value: " + interval);
        }
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreationRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest taskUpdate) {
        TaskResponse taskResponse = taskService.updateTask(id, taskUpdate);
        return ResponseEntity.ok(taskResponse);
    }

    @PostMapping("/{id}/toggle-completion")
    public ResponseEntity<Void> toggleTaskCompletion(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
