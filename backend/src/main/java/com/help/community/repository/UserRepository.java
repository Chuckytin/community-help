package com.help.community.repository;

import com.help.community.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(Long id);

    /**
     * Busca un usuario por su email (si existe).
     */
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.volunteeredRequests WHERE u.email = :email")
    Optional<User> findByEmailWithVolunteeredRequests(@Param("email") String email);
}
