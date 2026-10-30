package com.marsmars.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReqDto {
    @NotEmpty(message = "Username should be not empty")
    private String username;

    @NotEmpty(message = "Password should be not empty")
    private String password;

    @NotEmpty(message = "Email should be not empty")
    @Email(message = "Email field should be in email format")
    private String email;
}
