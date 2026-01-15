package com.cesde.studentinfo.dto;
import lombok.*;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private Long expiresIn;
}
