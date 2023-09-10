package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
import static hexlet.code.utils.TestUtils.TASK_STATUS_CONTROLLER_URL;
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
public class TaskStatusControllerIT {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    public void before() throws Exception {
        testUtils.addDefaultUser();
    }

    @AfterEach
    public void clear() {
        testUtils.clearDB();
    }

    @Test
    public void testCreateTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        testUtils.addTaskStatus("First status").andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void testCreateTaskStatusTwiceFail() throws Exception {
        testUtils.addTaskStatus("First status").andExpect(status().isCreated());
        testUtils.addTaskStatus("First status").andExpect(status().isUnprocessableEntity());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void testGetTaskStatusById() throws Exception {
        testUtils.addTaskStatus("First status");
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(TASK_STATUS_CONTROLLER_URL + ID, expectedTaskStatus.getId()),
                        TEST_EMAIL
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    public void testGetAllTaskStatuses() throws Exception {
        testUtils.addTaskStatus("First status");

        final var response = testUtils.perform(get(TASK_STATUS_CONTROLLER_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatuses = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatuses).hasSize(1);
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {
        testUtils.addTaskStatus("First status");
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);
        final var taskStatusDto = new TaskStatusDto("New status");

        final var updateRequest = put(TASK_STATUS_CONTROLLER_URL + ID, expectedTaskStatus.getId())
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);

        var response = testUtils.perform(updateRequest, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(taskStatusRepository.existsById(taskStatus.getId()));
        assertThat(taskStatus.getName()).isNotEqualTo("First status");
        assertThat(taskStatus.getName()).isEqualTo("New status");
    }

    @Test
    public void testDeleteTaskStatus() throws Exception {
        testUtils.addTaskStatus("First status");
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        testUtils.perform(delete(TASK_STATUS_CONTROLLER_URL + ID, expectedTaskStatus.getId()),
                        TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }
}
