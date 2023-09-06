package hexlet.code.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelService labelService;

    @Operation(summary = "Get specific label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label found"),
        @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @GetMapping(path = ID)
    public Label getLabel(@PathVariable final long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get list of all labels")
    @ApiResponse(responseCode = "200", description = "List of all labels")
    @GetMapping
    public List<Label> getAllLabels() {
        return labelService.getLabels();
    }

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Label created"),
        @ApiResponse(responseCode = "422", description = "Some info is missing")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label updated"),
        @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @PutMapping(path = ID)
    public Label updateLabel(@PathVariable final long id, @RequestBody @Valid final LabelDto labelDto) {
        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Label deleted"),
        @ApiResponse(responseCode = "404", description = "Label not found")
    })
    @DeleteMapping(path = ID)
    public void deleteLabel(@PathVariable final long id) {
        labelService.deleteLabel(id);
    }
}
