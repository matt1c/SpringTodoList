package com.marsmars.controllers;

import com.marsmars.dtos.TaskReqDto;
import com.marsmars.dtos.TaskRespDto;
import com.marsmars.security.UserDetailsImpl;
import com.marsmars.services.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public Page<TaskRespDto> findAll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     Pageable pageable) {
        return todoService.findAll(userDetails, pageable);
    }

    @GetMapping("/{id}")
    public TaskRespDto findOne(@PathVariable("id") Long taskId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.findOne(taskId, userDetails);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskRespDto create(@Valid @RequestBody TaskReqDto taskReqDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.create(taskReqDto, userDetails);
    }

    @PutMapping("/{id}")
    public TaskRespDto update(@PathVariable("id") Long taskId,
                              @Valid @RequestBody TaskReqDto taskReqDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return todoService.update(taskId, taskReqDto, userDetails);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long taskId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.delete(taskId, userDetails);
    }

}
