package com.cesde.studentinfo.repository;

import com.cesde.studentinfo.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.id = :roleId")
    Optional<UserRole> findByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.assignedBy.id = :assignedByUserId")
    List<UserRole> findByAssignedByUserId(@Param("assignedByUserId") Long assignedByUserId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.assignedAt >= :fromDate")
    List<UserRole> findRecentAssignments(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.username = :username")
    List<UserRole> findByUsername(@Param("username") String username);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.name = :roleName")
    List<UserRole> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
    long countByRoleId(@Param("roleId") Long roleId);
}

