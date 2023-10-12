package org.example.taskService.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.taskService.exception.TaskNotFoundException;
import org.example.taskService.model.Task;
import org.example.taskService.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.findById(task.getId())
                .map(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setDate(task.getDate());
                    existingTask.setCompleted(task.isCompleted());
                    return taskRepository.save(existingTask);
                })
                .orElseThrow(() -> new TaskNotFoundException(task.getId()));
    }

    @Override
    @Transactional
    public void toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }


    @Override
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @Override
    public List<Task> getTasksByDateAndCompletionStatus(LocalDate date, boolean completed) {
        return taskRepository.findByDateAndCompleted(date, completed);
    }

    @Override
    public List<Task> getTasksByWeekAndCompletionStatus(boolean isCompleted) {
        LocalDate now = LocalDate.now();
        return taskRepository.findByDateBetweenAndCompleted(now.minusDays(7), now, isCompleted);
    }

    @Override
    public List<Task> getTasksByMonthAndCompletionStatus(boolean isCompleted) {
        LocalDate now = LocalDate.now();
        return taskRepository.findByDateBetweenAndCompleted(now.minusMonths(1), now, isCompleted);
    }
}
