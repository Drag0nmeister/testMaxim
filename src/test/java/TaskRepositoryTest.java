import org.example.taskService.ApplicationRunner;
import org.example.taskService.model.Task;
import org.example.taskService.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ApplicationRunner.class)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task testTask;

    @BeforeEach
    public void setUp() {
        testTask = new Task();
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        testTask.setCompleted(false);
        taskRepository.save(testTask);
    }

    @Test
    public void whenFindByDateAndCompleted() {
        List<Task> foundTasks = taskRepository.findByDateAndCompleted(testTask.getDate().truncatedTo(ChronoUnit.SECONDS), false);
        assertThat(foundTasks).isNotEmpty();
        assertThat(foundTasks.get(0).getTitle()).isEqualTo(testTask.getTitle());
        assertThat(foundTasks.get(0).getDescription()).isEqualTo(testTask.getDescription());
        assertThat(foundTasks.get(0).isCompleted()).isEqualTo(testTask.isCompleted());
    }

    @Test
    public void whenFindByDateBetweenAndCompleted() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        List<Task> foundTasks = taskRepository.findByDateBetweenAndCompleted(startDate, endDate, false);
        assertThat(foundTasks).isNotEmpty();
        Task expectedTask = foundTasks.stream()
                .filter(task -> task.getTitle().equals(testTask.getTitle())
                        && task.getDescription().equals(testTask.getDescription())
                        && task.getDate().truncatedTo(ChronoUnit.SECONDS).equals(testTask.getDate().truncatedTo(ChronoUnit.SECONDS))
                        && task.isCompleted() == testTask.isCompleted())
                .findFirst()
                .orElse(null);

        assertThat(expectedTask).isNotNull();
    }
}
