package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    public Task getTaskById(long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    public Iterable<Task> getTasks(Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        transferDataFromDtoToEntity(taskDto, task);
        return taskRepository.save(task);
    }

    public Task updateTask(long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow();
        transferDataFromDtoToEntity(taskDto, task);
        return taskRepository.save(task);
    }

    public void deleteTask(long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
    }

    private void transferDataFromDtoToEntity(TaskDto taskDto, Task task) {
        User author = userService.getCurrentUser();
        User executor = userRepository.findById(taskDto.getExecutorId()).orElse(null);
        TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).orElseThrow();
        List<Label> labels = new ArrayList<>(labelRepository.findAllById(taskDto.getLabelIds()));
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setLabels(labels);
    }
}
