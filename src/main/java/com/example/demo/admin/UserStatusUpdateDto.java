package com.example.demo.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateDto {
    @NotNull(message = "Enabled status cannot be null")
    private Boolean enabled;
}
