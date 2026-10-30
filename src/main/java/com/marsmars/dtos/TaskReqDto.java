package com.marsmars.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskReqDto {
    @NotEmpty(message = "Title should be not empty")
    private String title;

    @NotEmpty(message = "Description should be not empty")
    private String description;

    @JsonIgnore
    private LocalDateTime createdAt;
}
