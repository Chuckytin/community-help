package com.help.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusDTO {
    @NotBlank
    private String status;
}