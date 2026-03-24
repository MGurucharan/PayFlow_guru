package com.payflow.payflow_api.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record CustomerDTO(
        Long id,
        @NotBlank(message = "Name cannot be empty") String name,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email cannot be empty")
        String email) {
}
