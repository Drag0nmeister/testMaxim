
import org.example.taskService.ApplicationRunner;
import org.example.taskService.model.Task;
import org.example.taskService.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApplicationRunner.class)
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
    }

    @Test
    public void testCreateTask() throws Exception {
        String jsonRequest = """
                {
                    "title": "New Task",
                    "description": "Test Task"
                }
                """;

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Test Task"));
    }

    @Test
    public void testGetTasksByDate() throws Exception {
        LocalDateTime testDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Task task = new Task();
        task.setTitle("Test Task 1");
        task.setDescription("This is a test task.");
        task.setDate(testDate);
        task.setCompleted(false);
        taskRepository.save(task);
        String formattedDate = testDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get("/api/v1/tasks")
                        .param("date", formattedDate)
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Test Task 1")))
                .andExpect(jsonPath("$[*].description", hasItem("This is a test task.")));
    }

    @Test
    public void testGetTasksForToday() throws Exception {
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Task taskForToday = new Task();
        taskForToday.setTitle("Today's Task");
        taskForToday.setDescription("Task for today.");
        taskForToday.setDate(today);
        taskForToday.setCompleted(false);
        taskRepository.save(taskForToday);
        mockMvc.perform(get("/api/v1/tasks/filtered")
                        .param("interval", "today")
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Today's Task")))
                .andExpect(jsonPath("$[*].description", hasItem("Task for today.")));
    }

    @Test
    public void testGetTasksForThisWeek() throws Exception {
        LocalDateTime thisWeek = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Task taskForThisWeek = new Task();
        taskForThisWeek.setTitle("This Week's Task");
        taskForThisWeek.setDescription("Task for this week.");
        taskForThisWeek.setDate(thisWeek);
        taskForThisWeek.setCompleted(false);
        taskRepository.save(taskForThisWeek);
        mockMvc.perform(get("/api/v1/tasks/filtered")
                        .param("interval", "week")
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("This Week's Task")))
                .andExpect(jsonPath("$[*].description", hasItem("Task for this week.")));
    }

    @Test
    public void testGetTasksForThisMonth() throws Exception {
        LocalDateTime thisMonth = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        Task taskForThisMonth = new Task();
        taskForThisMonth.setTitle("This Month's Task");
        taskForThisMonth.setDescription("Task for this month.");
        taskForThisMonth.setDate(thisMonth);
        taskForThisMonth.setCompleted(false);
        taskRepository.save(taskForThisMonth);
        mockMvc.perform(get("/api/v1/tasks/filtered")
                        .param("interval", "month")
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("This Month's Task")))
                .andExpect(jsonPath("$[*].description", hasItem("Task for this month.")));
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task task = new Task();
        task.setTitle("Original Title");
        task.setDescription("Original Description");
        task.setDate(LocalDateTime.now());
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        String jsonRequest = """
                {
                    "title": "Updated Title",
                    "description": "Updated Description"
                }
                """;

        mockMvc.perform(patch("/api/v1/tasks/" + savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    public void testToggleTaskCompletion() throws Exception {
        Task task = new Task();
        task.setTitle("Task to Toggle");
        task.setDescription("Description");
        task.setDate(LocalDateTime.now());
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        mockMvc.perform(post("/api/v1/tasks/" + savedTask.getId() + "/toggle-completion"))
                .andExpect(status().isNoContent());

        Optional<Task> updatedTask = taskRepository.findById(savedTask.getId());
        assertTrue(updatedTask.isPresent());
        assertTrue(updatedTask.get().isCompleted());
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task();
        task.setTitle("Task to Delete");
        task.setDescription("Description");
        task.setDate(LocalDateTime.now());
        task.setCompleted(false);
        Task savedTask = taskRepository.save(task);

        mockMvc.perform(delete("/api/v1/tasks/" + savedTask.getId()))
                .andExpect(status().isNoContent());

        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertFalse(deletedTask.isPresent());
    }
}
