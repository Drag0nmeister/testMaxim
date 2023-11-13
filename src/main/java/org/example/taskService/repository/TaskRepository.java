package org.example.taskService.repository;

import org.example.taskService.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDateAndCompleted(LocalDateTime date, boolean completed);

    List<Task> findByDateBetweenAndCompleted(LocalDateTime startDate, LocalDateTime endDate, boolean completed);
}
