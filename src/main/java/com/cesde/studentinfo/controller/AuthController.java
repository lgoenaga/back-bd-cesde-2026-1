package com.cesde.studentinfo.controller;
import com.cesde.studentinfo.dto.*;
import com.cesde.studentinfo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            log.info("POST /auth/login - Login attempt for: {}", loginRequest.getUsernameOrEmail());
            LoginResponseDTO response = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.<LoginResponseDTO>builder()
                    .success(true)
                    .message("Login exitoso")
                    .data(response)
                    .build());
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message("Error en el proceso de autenticación")
                            .build());
        }
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            log.info("POST /auth/register - Registration attempt for: {}", registerRequest.getUsername());
            LoginResponseDTO response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(true)
                            .message("Usuario registrado exitosamente")
                            .data(response)
                            .build());
        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message("Error en el proceso de registro")
                            .build());
        }
    }
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponseDTO>> validateToken(@RequestBody String token) {
        try {
            log.info("POST /auth/validate-token - Token validation request");
            String cleanToken = token.replace("\"", "").trim();
            TokenValidationResponseDTO response = authService.validateToken(cleanToken);
            return ResponseEntity.ok(ApiResponse.<TokenValidationResponseDTO>builder()
                    .success(response.isValid())
                    .message(response.getMessage())
                    .data(response)
                    .build());
        } catch (Exception e) {
            log.error("Token validation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<TokenValidationResponseDTO>builder()
                            .success(false)
                            .message("Error al validar el token")
                            .build());
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("POST /auth/refresh-token - Token refresh request");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.<LoginResponseDTO>builder()
                                .success(false)
                                .message("Authorization header inválido")
                                .build());
            }
            String oldToken = authHeader.substring(7);
            LoginResponseDTO response = authService.refreshToken(oldToken);
            return ResponseEntity.ok(ApiResponse.<LoginResponseDTO>builder()
                    .success(true)
                    .message("Token renovado exitosamente")
                    .data(response)
                    .build());
        } catch (BadCredentialsException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Token refresh error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<LoginResponseDTO>builder()
                            .success(false)
                            .message("Error al refrescar el token")
                            .build());
        }
    }
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Authentication service is running")
                .data("OK")
                .build());
    }
}
