package com.cesde.studentinfo.dto;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponseDTO {
    private boolean valid;
    private String username;
    private String message;
}
