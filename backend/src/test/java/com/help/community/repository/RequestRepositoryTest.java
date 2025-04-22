package com.help.community.repository;

import com.help.community.model.Request;
import com.help.community.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

/**
 * Pruebas para el repositorio de solicitudes
 * Usa una base de datos H2 en memoria para simular el entorno JPA
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    public void whenFindByCategory_thenReturnRequests() {
        // Datos ya cargados via data.sql

        // When
        List<Request> found = requestRepository.findByCategory("Emergencia");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Ayuda urgente");
    }

    @Test
    public void whenCreateNewRequest_thenPersistedCorrectly() throws InterruptedException {
        // Given
        User user = new User("new@example.com", "New User", "password");
        entityManager.persist(user);

        Request newRequest = new Request("Nueva ayuda", "Descripción", "Otros", user);

        // When
        Request saved = requestRepository.save(newRequest);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(requestRepository.count()).isEqualTo(2); // 1 de data.sql + este nuevo
    }

}
