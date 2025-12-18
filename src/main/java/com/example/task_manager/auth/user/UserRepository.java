package com.example.task_manager.auth.user;

import com.example.task_manager.auth.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserName(String userName);
    Optional<User> findByUserName(String userName);
    List<User> findByRoles(Role role);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userName = :username")
    Optional<User> findByUseNameWithRoles(@Param("userName") String username);
    Page<User> findAll(Pageable pageable);
    List<User> findByUserNameContainingIgnoreCase(String query);
}
