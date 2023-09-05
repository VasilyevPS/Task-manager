package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TaskStatusRepository taskStatusRepository;

    @Override
    public Task getTaskById(long id) {
        return taskRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        User author = userService.getCurrentUser();
        User executor = userRepository.findById(taskDto.getExecutorId()).orElse(null);
        TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).orElseThrow();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(long id, TaskDto taskDto) {
        Task task = taskRepository.findById(id).orElseThrow();
        User author = userService.getCurrentUser();
        User executor = userRepository.findById(taskDto.getExecutorId()).orElse(null);
        TaskStatus taskStatus = taskStatusRepository.findById(taskDto.getTaskStatusId()).orElseThrow();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.delete(task);
    }
}
