package com.dropmate.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private Integer id;
    private String name;
    private String username;
    private String email;
    @NotEmpty
    private String password;
    private String phone;
    private LocalDate dob;
    private String gender;
}
