package org.example.taskService.controller;

import jakarta.validation.Valid;
import org.example.taskService.exception.InvalidIntervalException;
import org.example.taskService.model.Task;
import org.example.taskService.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasksByDateAndCompletionStatus(@RequestParam LocalDate date, @RequestParam boolean completed) {
        return new ResponseEntity<>(taskService.getTasksByDateAndCompletionStatus(date, completed), HttpStatus.OK);
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Task>> getTasksByIntervalAndCompletionStatus(@RequestParam(required = false) LocalDate date, @RequestParam(required = false) String interval, @RequestParam boolean completed) {

        if (interval != null) {
            if ("today".equals(interval)) {
                return new ResponseEntity<>(taskService.getTasksByDateAndCompletionStatus(LocalDate.now(), completed), HttpStatus.OK);
            } else if ("week".equals(interval)) {
                return new ResponseEntity<>(taskService.getTasksByWeekAndCompletionStatus(completed), HttpStatus.OK);
            } else if ("month".equals(interval)) {
                return new ResponseEntity<>(taskService.getTasksByMonthAndCompletionStatus(completed), HttpStatus.OK);
            } else {
                throw new InvalidIntervalException("Invalid interval value: " + interval);
            }
        } else if (date != null) {
            return new ResponseEntity<>(taskService.getTasksByDateAndCompletionStatus(date, completed), HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Either date or interval must be provided");
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        return new ResponseEntity<>(taskService.createTask(task), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        task.setId(id);
        return new ResponseEntity<>(taskService.updateTask(task), HttpStatus.OK);
    }

    @PutMapping("/{id}/toggle-completion")
    public ResponseEntity<Void> toggleTaskCompletion(@PathVariable Long id) {
        taskService.toggleTaskCompletion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
