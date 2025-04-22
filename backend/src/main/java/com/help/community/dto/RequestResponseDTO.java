package com.help.community.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private UserDTO creator;
    private UserDTO volunteer;

}
