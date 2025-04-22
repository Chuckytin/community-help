package com.help.community.repository;

import com.help.community.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Proporciona métodos para acceder a las solicitudes en la BBDD.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByCategory(String category);

    List<Request> findByStatus(String status);

}
