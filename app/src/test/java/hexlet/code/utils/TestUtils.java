package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String ID = "/{id}";
    public static final String BASE_URL = "/api";
    public static final String USER_CONTROLLER_URL = BASE_URL + USER_CONTROLLER_PATH;
    public static final String LOGIN_URL = BASE_URL + LOGIN;
    public static final String TASK_STATUS_CONTROLLER_URL = BASE_URL + TASK_STATUS_CONTROLLER_PATH;
    public static final String TASK_CONTROLLER_URL = BASE_URL + TASK_CONTROLLER_PATH;
    public static final String TEST_EMAIL = "test@email.com";
    public static final String TEST_EMAIL_2 = "test2@email.com";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    private final UserDto testUserDto = new UserDto(
            TEST_EMAIL,
            "John",
            "Snow",
            "WinterIsComing"
    );

    public UserDto getTestUserDto() {
        return testUserDto;
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTHelper jwtHelper;

    public void clearDB() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email).get();
    }

    public ResultActions addDefaultUser() throws Exception {
        return addUser(testUserDto);
    }

    public ResultActions addUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_URL)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions addTaskStatus(final String name) throws Exception {
        TaskStatusDto taskStatusDto = new TaskStatusDto(name);
        return perform(post(TASK_STATUS_CONTROLLER_URL)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON), TEST_EMAIL);
    }

    public ResultActions addTask() throws Exception {
        long userId = userRepository.findAll().get(0).getId();
        addTaskStatus("New status");
        long taskStatusId = taskStatusRepository.findAll().get(0).getId();
        TaskDto taskDto = new TaskDto("Task 1", "Description 1", taskStatusId, userId);
        return perform(post(TASK_CONTROLLER_URL)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON), TEST_EMAIL);
    }


    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
