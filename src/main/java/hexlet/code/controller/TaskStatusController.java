package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";
    private final TaskStatusService taskStatusService;

    @Operation(summary = "Get task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status found"),
        @ApiResponse(responseCode = "404", description = "Task status not found")
    })
    @GetMapping(path = ID)
    public TaskStatus getTaskStatus(@PathVariable final long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @Operation(summary = "Get list of all task statuses")
    @ApiResponse(responseCode = "200", description = "List of all task statuses")
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusService.getTaskStatuses();
    }

    @Operation(summary = "Create new task status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Task status created"),
        @ApiResponse(responseCode = "422", description = "Task status exists or some info is missing")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatus createTaskStatus(@RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.createTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Update task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status updated"),
        @ApiResponse(responseCode = "404", description = "Task status not found")
    })
    @PutMapping(path = ID)
    public TaskStatus updateTaskStatus(@PathVariable final long id,
                                       @RequestBody @Valid final TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete task status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status deleted"),
        @ApiResponse(responseCode = "404", description = "Task status not found")
    })
    @DeleteMapping(path = ID)
    public void deleteTaskStatus(@PathVariable final long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}
