package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Override
    List<User> findAll();

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Override
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActive();

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsernameContaining(String username);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u WHERE u.student.id = :studentId")
    Optional<User> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u WHERE u.professor.id = :professorId")
    Optional<User> findByProfessorId(Long professorId);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(String roleName);

    // ==================== PAGINATION METHODS ====================

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    Page<User> findByIsActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"roles", "student", "professor"})
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findByRoleName(String roleName, Pageable pageable);
}
