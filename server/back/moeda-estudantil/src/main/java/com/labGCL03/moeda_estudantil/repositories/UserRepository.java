package com.labGCL03.moeda_estudantil.repositories;

import com.labGCL03.moeda_estudantil.entities.User;
import com.labGCL03.moeda_estudantil.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    // TODO: Adicionar campo emailVerified na entidade User para habilitar esses métodos
    // @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    // List<User> findVerifiedUsers();
    
    // @Query("SELECT u FROM User u WHERE u.emailVerified = false")
    // List<User> findUnverifiedUsers();
}