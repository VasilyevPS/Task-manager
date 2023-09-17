package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatus getTaskStatusById(long id) {
        return taskStatusRepository.findById(id).orElseThrow();
    }

    public List<TaskStatus> getTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    public TaskStatus updateTaskStatus(long id, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow();
        taskStatus.setName(taskStatusDto.getName());
        return taskStatusRepository.save(taskStatus);
    }

    public void deleteTaskStatus(long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow();
        taskStatusRepository.delete(taskStatus);
    }
}
