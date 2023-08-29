package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.utils.TestUtils.ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.TEST_USERNAME_2;
import static hexlet.code.utils.TestUtils.USER_CONTROLLER_URL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfigForIT.class)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void clear() {
        testUtils.clearDB();
    }

    @Test
    public void testCreateUser() throws Exception {
        assertEquals(0, userRepository.count());
        testUtils.addDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testCreateUserTwiceFail() throws Exception {
        testUtils.addDefaultUser().andExpect(status().isCreated());
        testUtils.addDefaultUser().andExpect(status().isUnprocessableEntity());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testGetUserById() throws Exception {
        testUtils.addDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(USER_CONTROLLER_URL + ID, expectedUser.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }

    @Test
    public void testGetUserByIdFail() throws Exception {
        testUtils.addDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(USER_CONTROLLER_URL + ID, expectedUser.getId() + 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        testUtils.addDefaultUser();
        final var response = testUtils.perform(get(USER_CONTROLLER_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users).hasSize(1);
    }


    @Test
    public void testUpdateUser() throws Exception {
        testUtils.addDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        final var userDto = new UserDto(TEST_USERNAME_2, "new name", "new last name", "new pwd");

        final var updateRequest = put(USER_CONTROLLER_URL + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        testUtils.perform(updateRequest).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(TEST_USERNAME_2).orElse(null));
    }

    @Test
    public void testDeleteUser() throws Exception {
        testUtils.addDefaultUser();

        final Long userId = userRepository.findByEmail(TEST_USERNAME).get().getId();

        testUtils.perform(delete(USER_CONTROLLER_URL + ID, userId))
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }
}
