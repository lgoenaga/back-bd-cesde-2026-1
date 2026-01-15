package com.cesde.studentinfo.controller;

import com.cesde.studentinfo.dto.ApiResponse;
import com.cesde.studentinfo.dto.PagedResponse;
import com.cesde.studentinfo.dto.UserDTO;
import com.cesde.studentinfo.dto.UserResponseDTO;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Professor;
import com.cesde.studentinfo.model.Student;
import com.cesde.studentinfo.model.User;
import com.cesde.studentinfo.repository.ProfessorRepository;
import com.cesde.studentinfo.repository.StudentRepository;
import com.cesde.studentinfo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Users retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getActiveUsers() {
        log.info("GET /users/active - Fetching active users");
        List<User> users = userService.getActiveUsers();
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Active users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Fetching user by ID", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
        log.info("GET /users/username/{} - Fetching user by username", username);
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByEmail(@PathVariable String email) {
        log.info("GET /users/email/{} - Fetching user by email", email);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchUsers(@RequestParam String username) {
        log.info("GET /users/search?username={} - Searching users by username", username);
        List<User> users = userService.searchUsersByUsername(username);
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsersByRole(@PathVariable String roleName) {
        log.info("GET /users/role/{} - Fetching users by role", roleName);
        List<User> users = userService.getUsersByRole(roleName);
        List<UserResponseDTO> response = users.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response, "Users by role retrieved successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countUsers() {
        log.info("GET /users/count - Counting total users");
        long count = userService.countUsers();
        return ResponseEntity.ok(ApiResponse.success(count, "Total users counted successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("POST /users - Creating new user: {}", userDTO.getUsername());

        User user = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .email(userDTO.getEmail())
                .isActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : true)
                .build();

        // Set student if provided
        if (userDTO.getStudentId() != null) {
            Student student = studentRepository.findById(userDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", userDTO.getStudentId()));
            user.setStudent(student);
        }

        // Set professor if provided
        if (userDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(userDTO.getProfessorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor", userDTO.getProfessorId()));
            user.setProfessor(professor);
        }

        User savedUser = userService.createUser(user, userDTO.getRoleIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserResponseDTO.fromEntity(savedUser), "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("PUT /users/{} - Updating user", id);

        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        }
        if (userDTO.getIsActive() != null) {
            user.setIsActive(userDTO.getIsActive());
        }

        // Update student if provided
        if (userDTO.getStudentId() != null) {
            Student student = studentRepository.findById(userDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", userDTO.getStudentId()));
            user.setStudent(student);
        } else {
            user.setStudent(null);
        }

        // Update professor if provided
        if (userDTO.getProfessorId() != null) {
            Professor professor = professorRepository.findById(userDTO.getProfessorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor", userDTO.getProfessorId()));
            user.setProfessor(professor);
        } else {
            user.setProfessor(null);
        }

        User updatedUser = userService.updateUser(user, userDTO.getRoleIds());
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(updatedUser), "User updated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deactivateUser(@PathVariable Long id) {
        log.info("PATCH /users/{}/deactivate - Deactivating user", id);
        userService.deactivateUser(id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user), "User deactivated successfully"));
    }

    @PatchMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        log.info("PATCH /users/{}/roles/{} - Assigning role to user", userId, roleId);
        User user = userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user), "Role assigned successfully"));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        log.info("DELETE /users/{}/roles/{} - Removing role from user", userId, roleId);
        User user = userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromEntity(user), "Role removed successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    // ==================== PAGINATED ENDPOINTS ====================

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {

        log.info("GET /users/paged - Fetching users page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<User> userPage = userService.getAllUsersPaginated(pageable);
        PagedResponse<UserResponseDTO> response = PagedResponse.from(
                userPage.map(UserResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Users retrieved successfully"));
    }

    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getActiveUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {

        log.info("GET /users/active/paged - Fetching active users page={}, size={}", page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<User> userPage = userService.getActiveUsersPaginated(pageable);
        PagedResponse<UserResponseDTO> response = PagedResponse.from(
                userPage.map(UserResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Active users retrieved successfully"));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> searchUsersPaginated(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {

        log.info("GET /users/search/paged - Searching users username={}, page={}, size={}", username, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<User> userPage = userService.searchUsersByUsernamePaginated(username, pageable);
        PagedResponse<UserResponseDTO> response = PagedResponse.from(
                userPage.map(UserResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Search completed successfully"));
    }

    @GetMapping("/role/{roleName}/paged")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getUsersByRolePaginated(
            @PathVariable String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username,asc") String[] sort) {

        log.info("GET /users/role/{}/paged - Fetching users by role page={}, size={}", roleName, page, size);
        Pageable pageable = createPageable(page, size, sort);
        Page<User> userPage = userService.getUsersByRolePaginated(roleName, pageable);
        PagedResponse<UserResponseDTO> response = PagedResponse.from(
                userPage.map(UserResponseDTO::fromEntity));
        return ResponseEntity.ok(ApiResponse.success(response, "Users by role retrieved successfully"));
    }

    private Pageable createPageable(int page, int size, String[] sort) {
        String sortBy = sort.length > 0 ? sort[0] : "id";
        String direction = sort.length > 1 ? sort[1] : "asc";
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
