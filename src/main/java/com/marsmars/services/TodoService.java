package com.marsmars.services;

import com.marsmars.dtos.TaskReqDto;
import com.marsmars.dtos.TaskRespDto;
import com.marsmars.exceptions.TaskNotFound;
import com.marsmars.models.Task;
import com.marsmars.repositories.TaskRepository;
import com.marsmars.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TodoService {
    private final TaskRepository taskRepository;

    public TodoService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<TaskRespDto> findAll(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Task> page = taskRepository.findAllByUser(userDetails.getUser().getId(), pageable);
        return page.map(this::convertToResp);
    }

    public TaskRespDto findOne(Long taskId, UserDetailsImpl userDetails) {
        return convertToResp(taskRepository.findByTaskIdAndUserId(taskId, userDetails.getUser().getId())
                .orElseThrow(() -> new TaskNotFound("Task not found")));
    }

    public TaskRespDto create(TaskReqDto taskReqDto, UserDetailsImpl userDetails) {
        Task task = new Task();
        task.setTitle(taskReqDto.getTitle());
        task.setDescription(taskReqDto.getDescription());
        task.setCreatedAt(LocalDateTime.now());
        task.setUser(userDetails.getUser());
        return convertToResp(taskRepository.save(task));
    }

    public TaskRespDto update(Long taskId, TaskReqDto taskReqDto, UserDetailsImpl userDetails) {
        Task task = getUserTask(taskId, userDetails);
        task.setTitle(taskReqDto.getTitle());
        task.setDescription(taskReqDto.getDescription());
        return convertToResp(taskRepository.save(task));
    }

    public void delete(Long taskId, UserDetailsImpl userDetails) {
        taskRepository.delete(getUserTask(taskId, userDetails));
    }

    private Task getUserTask(Long taskId, UserDetailsImpl userDetails) {
        return taskRepository.findByTaskIdAndUserId(taskId, userDetails.getUser().getId())
                .orElseThrow(() -> new TaskNotFound("Task not found"));
    }

    private TaskRespDto convertToResp(Task task) {
        TaskRespDto taskRespDto = new TaskRespDto();
        taskRespDto.setId(task.getId());
        taskRespDto.setCreatedAt(task.getCreatedAt());
        taskRespDto.setTitle(task.getTitle());
        taskRespDto.setDescription(task.getDescription());
        return taskRespDto;
    }
}
