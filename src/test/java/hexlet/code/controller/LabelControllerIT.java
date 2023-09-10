package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.ID;
import static hexlet.code.utils.TestUtils.LABEL_CONTROLLER_URL;
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
public class LabelControllerIT {

    @Autowired
    private LabelRepository labelRepository;

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
    public void testCreateLabel() throws Exception {
        assertEquals(0, labelRepository.count());
        testUtils.addLabel("First label").andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void testCreateLabelTwiceFail() throws Exception {
        testUtils.addLabel("First label").andExpect(status().isCreated());
        testUtils.addLabel("First label").andExpect(status().isUnprocessableEntity());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void testGetLabelById() throws Exception {
        testUtils.addLabel("First label");
        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = testUtils.perform(
                        get(LABEL_CONTROLLER_URL + ID, expectedLabel.getId()),
                        TEST_EMAIL
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    public void testGetAllTaskStatuses() throws Exception {
        testUtils.addLabel("First label");

        final var response = testUtils.perform(get(LABEL_CONTROLLER_URL), TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels).hasSize(1);
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {
        testUtils.addLabel("First label");
        final Label expectedLabel = labelRepository.findAll().get(0);
        final var labelDto = new LabelDto("New label");

        final var updateRequest = put(TestUtils.LABEL_CONTROLLER_URL + ID, expectedLabel.getId())
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);

        var response = testUtils.perform(updateRequest, TEST_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(labelRepository.existsById(label.getId()));
        assertThat(label.getName()).isNotEqualTo("First label");
        assertThat(label.getName()).isEqualTo("New label");
    }

    @Test
    public void testDeleteTaskStatus() throws Exception {
        testUtils.addLabel("First label");
        final Label expectedLabel = labelRepository.findAll().get(0);

        testUtils.perform(delete(LABEL_CONTROLLER_URL + ID, expectedLabel.getId()),
                        TEST_EMAIL)
                .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }
}
