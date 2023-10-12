import org.example.taskService.ApplicationRunner;
import org.example.taskService.exception.TaskNotFoundException;
import org.example.taskService.model.Task;
import org.example.taskService.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ApplicationRunner.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    public TaskControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testGetTasksForToday() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task 1");
        task.setDescription("This is a test task.");
        task.setDate(LocalDate.now());
        task.setCompleted(false);

        when(taskService.getTasksByDateAndCompletionStatus(LocalDate.now(), false)).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks")
                        .param("date", LocalDate.now().toString())
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test Task 1")))
                .andExpect(jsonPath("$[0].description", is("This is a test task.")));

        verify(taskService).getTasksByDateAndCompletionStatus(LocalDate.now(), false);
    }


    @Test
    public void testUpdateTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("This is an updated task.");
        updatedTask.setDate(LocalDate.now());
        updatedTask.setCompleted(true);
        when(taskService.updateTask(any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"title\":\"Updated Task\",\"description\":\"This is an updated task.\",\"date\":\"" + LocalDate.now() + "\",\"isCompleted\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("This is an updated task.")));

        verify(taskService).updateTask(any(Task.class));
    }


    @Test
    public void testInvalidIntervalException() throws Exception {
        mockMvc.perform(get("/tasks/filtered")
                        .param("interval", "yearly")
                        .param("completed", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid interval value: yearly"));
    }


    @Test
    public void testToggleTaskCompletion() throws Exception {
        mockMvc.perform(put("/tasks/1/toggle-completion"))
                .andExpect(status().isNoContent());

        verify(taskService).toggleTaskCompletion(1L);
    }

    @Test
    public void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    public void testInvalidIntervalExceptionHandling() throws Exception {
        mockMvc.perform(get("/tasks/filtered")
                        .param("interval", "invalid_interval")
                        .param("completed", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string("Invalid interval value: invalid_interval"));
    }


    @Test
    public void testTaskNotFoundExceptionHandling() throws Exception {
        when(taskService.updateTask(any(Task.class))).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(put("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":999, \"title\":\"Not Existing Task\",\"description\":\"This task does not exist.\",\"date\":\"" + LocalDate.now() + "\",\"isCompleted\":false}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Task with ID 999 not found."));
    }


}
