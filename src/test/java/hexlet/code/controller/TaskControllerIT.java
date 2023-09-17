package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.ID;
import static hexlet.code.utils.TestUtils.TASK_CONTROLLER_URL;
import static hexlet.code.utils.TestUtils.TEST_EMAIL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class TaskControllerIT {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    public void before() throws Exception {
        testUtils.addDefaultUser();
        testUtils.addTaskStatus("First status");
        testUtils.addLabel("First label");
    }

    @AfterEach
    public void clear() {
        testUtils.clearDB();
    }

    @Test
    public void testCreateTask() throws Exception {
        assertEquals(0, taskRepository.count());

        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto).andExpect(status().isCreated());

        assertEquals(1, taskRepository.count());

        final Task task = taskRepository.findAll().get(0);
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());
    }

    @Test
    public void testGetTaskById() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = testUtils.perform(
                get(TASK_CONTROLLER_URL + ID, expectedTask.getId()),
                        TEST_EMAIL
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getAuthor().getId(), task.getAuthor().getId());
    }

    @Test
    public void testGetTaskByIdNotExist() throws Exception {
        final int notExistedTaskId = 1;

        testUtils.perform(
                        get(TASK_CONTROLLER_URL + ID, notExistedTaskId),
                        TEST_EMAIL
                ).andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllTasks() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        final var response = testUtils.perform(get(TASK_CONTROLLER_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(1);
    }

    @Test
    public void testUpdateTask() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        Task task = taskRepository.findAll().get(0);

        TaskDto newTaskDto = new TaskDto(
                "Updated task 1",
                "Updated description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );

        testUtils.perform(put(TASK_CONTROLLER_URL + ID, task.getId())
                        .content(asJson(newTaskDto))
                        .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andExpect(status().isOk());

        task = taskRepository.findAll().get(0);
        assertTrue(taskRepository.existsById(task.getId()));
        assertThat(task.getName()).isEqualTo("Updated task 1");
        assertThat(task.getDescription()).isEqualTo("Updated description 1");
    }

    @Test
    public void testUpdateTaskNotExist() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        final int notExistedTaskId = 2;

        TaskDto newTaskDto = new TaskDto(
                "Updated task 1",
                "Updated description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );

        testUtils.perform(put(TASK_CONTROLLER_URL + ID, notExistedTaskId)
                        .content(asJson(newTaskDto))
                        .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andExpect(status().isNotFound());

        final Task task = taskRepository.findAll().get(0);
        assertTrue(taskRepository.existsById(task.getId()));
        assertThat(task.getName()).isNotEqualTo("Updated task 1");
        assertThat(task.getDescription()).isNotEqualTo("Updated description 1");
        assertThat(task.getName()).isEqualTo("Task 1");
        assertThat(task.getDescription()).isEqualTo("Description 1");
    }

    @Test
    public void testUpdateTaskInfoMissing() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        Task task = taskRepository.findAll().get(0);

        TaskDto newTaskDto = new TaskDto(
                "",
                "Updated description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );

        testUtils.perform(put(TASK_CONTROLLER_URL + ID, task.getId())
                        .content(asJson(newTaskDto))
                        .contentType(APPLICATION_JSON), TEST_EMAIL)
                .andExpect(status().isUnprocessableEntity());

        task = taskRepository.findAll().get(0);
        assertTrue(taskRepository.existsById(task.getId()));
        assertThat(task.getName()).isNotEqualTo("Updated task 1");
        assertThat(task.getDescription()).isNotEqualTo("Updated description 1");
        assertThat(task.getName()).isEqualTo("Task 1");
        assertThat(task.getDescription()).isEqualTo("Description 1");
    }

    @Test
    public void testDeleteTask() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        final long taskId = taskRepository.findAll().get(0).getId();

        testUtils.perform(delete(TASK_CONTROLLER_URL + ID, taskId),
                        TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }

    @Test
    public void testDeleteTaskNotExist() throws Exception {
        final long userId = userRepository.findAll().get(0).getId();
        final long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        final long labelId = labelRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "Task 1",
                "Description 1",
                taskStatusId,
                userId,
                List.of(labelId)
        );
        testUtils.addTask(taskDto);

        final int notExistedTaskId = 2;

        testUtils.perform(delete(TASK_CONTROLLER_URL + ID, notExistedTaskId),
                        TEST_EMAIL)
                .andExpect(status().isNotFound());

        assertEquals(1, taskRepository.count());
    }
}
