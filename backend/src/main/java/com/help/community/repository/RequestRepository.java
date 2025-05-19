package com.help.community.repository;

import com.help.community.model.Request;
import com.help.community.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Proporciona métodos para acceder a las solicitudes en la BBDD.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByCategory(String category);

    List<Request> findByStatus(String status);

    @Query("SELECT r FROM Request r JOIN FETCH r.volunteer v WHERE v.id = :userId")
    List<Request> findByVolunteerId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"creator", "volunteer"})
    List<Request> findByVolunteer(User volunteer);
}
