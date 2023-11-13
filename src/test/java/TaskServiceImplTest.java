import org.example.taskService.model.Task;
import org.example.taskService.dto.TaskCreationRequest;
import org.example.taskService.dto.TaskUpdateRequest;
import org.example.taskService.dto.TaskResponse;
import org.example.taskService.repository.TaskRepository;
import org.example.taskService.service.TaskServiceImpl;
import org.example.taskService.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask() {
        TaskCreationRequest creationRequest = new TaskCreationRequest("Test Title", "Test Description");
        Task mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Title");
        mockTask.setDescription("Test Description");
        mockTask.setDate(LocalDateTime.now());
        mockTask.setCompleted(false);

        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        TaskResponse response = taskService.createTask(creationRequest);

        assertNotNull(response);
        assertEquals(mockTask.getTitle(), response.getTitle());
        assertEquals(mockTask.getDescription(), response.getDescription());
    }

    @Test
    void updateTask() {
        TaskUpdateRequest updateRequest = new TaskUpdateRequest("Updated Title", "Updated Description");
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setDate(LocalDateTime.now());
        existingTask.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getTitle(), response.getTitle());
        assertEquals(updateRequest.getDescription(), response.getDescription());
    }

    @Test
    void toggleTaskCompletion() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        taskService.toggleTaskCompletion(1L);

        assertTrue(existingTask.isCompleted());
    }

    @Test
    void deleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void getTasksByDateAndCompletionStatus() {
        LocalDateTime date = LocalDateTime.now();
        Task task = new Task();
        task.setId(1L);
        task.setDate(date);
        task.setCompleted(true);

        when(taskRepository.findByDateAndCompleted(date, true)).thenReturn(Collections.singletonList(task));

        var tasks = taskService.getTasksByDateAndCompletionStatus(date, true);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    void getUpcomingTasksByWeekAndCompletionStatus() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime weekLater = now.plusWeeks(1).truncatedTo(ChronoUnit.SECONDS);
        Task upcomingTask = createTestTask(1L, "Upcoming Week Task", weekLater, false);

        when(taskRepository.findByDateBetweenAndCompleted(any(LocalDateTime.class), any(LocalDateTime.class), eq(false)))
                .thenReturn(Collections.singletonList(upcomingTask));

        List<TaskResponse> tasks = taskService.getUpcomingTasksByWeekAndCompletionStatus(false);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("Upcoming Week Task", tasks.get(0).getTitle());
    }

    @Test
    void getUpcomingTasksByMonthAndCompletionStatus() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime monthLater = now.plusMonths(1).truncatedTo(ChronoUnit.SECONDS);
        Task upcomingTask = createTestTask(2L, "Upcoming Month Task", monthLater, true);

        when(taskRepository.findByDateBetweenAndCompleted(any(LocalDateTime.class), any(LocalDateTime.class), eq(true)))
                .thenReturn(Collections.singletonList(upcomingTask));

        List<TaskResponse> tasks = taskService.getUpcomingTasksByMonthAndCompletionStatus(true);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("Upcoming Month Task", tasks.get(0).getTitle());
    }

    private Task createTestTask(Long id, String title, LocalDateTime date, boolean completed) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Test Description");
        task.setDate(date);
        task.setCompleted(completed);
        return task;
    }

    @Test
    void taskNotFound() {
        when(taskRepository.findById(1L)).thenThrow(new TaskNotFoundException(1L));

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, new TaskUpdateRequest("Title", "Description")));
    }
}
