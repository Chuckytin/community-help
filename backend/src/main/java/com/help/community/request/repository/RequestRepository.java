package com.help.community.request.repository;

import com.help.community.request.model.Request;
import com.help.community.user.model.User;
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

    @Query("SELECT r FROM Request r WHERE r.status = 'PENDIENTE' AND (r.deadline IS NULL OR r.deadline > CURRENT_TIMESTAMP)")
    List<Request> findAllPendingRequests();

    @Query(value = """
    SELECT * FROM requests
    WHERE ST_DWithin(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radius)
    AND status = 'PENDIENTE'
    AND (deadline IS NULL OR deadline > NOW())""", nativeQuery = true)
    List<Request> findNearbyPendingRequests(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radiusInMeters);

    List<Request> findByCreatorOrderByCreatedAtDesc(User creator);

}
