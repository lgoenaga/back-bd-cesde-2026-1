package com.cesde.studentinfo.service;
import com.cesde.studentinfo.config.JwtUtil;
import com.cesde.studentinfo.dto.LoginRequestDTO;
import com.cesde.studentinfo.dto.LoginResponseDTO;
import com.cesde.studentinfo.dto.RegisterRequestDTO;
import com.cesde.studentinfo.dto.TokenValidationResponseDTO;
import com.cesde.studentinfo.model.Role;
import com.cesde.studentinfo.model.User;
import com.cesde.studentinfo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getUsernameOrEmail());
        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado: " + loginRequest.getUsernameOrEmail()));
        if (!user.getIsActive()) {
            throw new BadCredentialsException("La cuenta está desactivada");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getId());
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        log.info("Login successful for user: {}", user.getUsername());
        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .expiresIn(jwtExpiration)
                .build();
    }
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO registerRequest) {
        log.info("Register attempt for username: {}", registerRequest.getUsername());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .email(registerRequest.getEmail())
                .isActive(true)
                .build();
        User createdUser = userService.createUser(user, registerRequest.getRoleIds());
        UserDetails userDetails = userDetailsService.loadUserByUsername(createdUser.getUsername());
        String token = jwtUtil.generateToken(userDetails, createdUser.getId());
        Set<String> roles = createdUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        log.info("Registration successful for user: {}", createdUser.getUsername());
        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(createdUser.getId())
                .username(createdUser.getUsername())
                .email(createdUser.getEmail())
                .roles(roles)
                .expiresIn(jwtExpiration)
                .build();
    }
    public TokenValidationResponseDTO validateToken(String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                return TokenValidationResponseDTO.builder()
                        .valid(true)
                        .username(username)
                        .message("Token válido")
                        .build();
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
        }
        return TokenValidationResponseDTO.builder()
                .valid(false)
                .message("Token inválido o expirado")
                .build();
    }
    @Transactional
    public LoginResponseDTO refreshToken(String oldToken) {
        if (!jwtUtil.validateToken(oldToken)) {
            throw new BadCredentialsException("Token inválido o expirado");
        }
        String username = jwtUtil.extractUsername(oldToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newToken = jwtUtil.generateToken(userDetails, user.getId());
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return LoginResponseDTO.builder()
                .token(newToken)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .expiresIn(jwtExpiration)
                .build();
    }
}
