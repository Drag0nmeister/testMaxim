package org.example.taskService.repository;

import org.example.taskService.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDateAndCompleted(LocalDate date, boolean completed);

    List<Task> findByDateBetweenAndCompleted(LocalDate startDate, LocalDate endDate, boolean completed);
}
