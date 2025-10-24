package com.turngo.turngo;

import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.repositories.CanchaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CanchaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CanchaRepository canchaRepository;

    @Test
    void shouldGetAllCanchasWhenEmpty() throws Exception {
        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/canchas", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldGetAllCanchasWithData() throws Exception {
        // Given
        Cancha cancha1 = new Cancha();
        cancha1.setNumero(1);
        canchaRepository.save(cancha1);

        Cancha cancha2 = new Cancha();
        cancha2.setNumero(2);
        canchaRepository.save(cancha2);

        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/canchas", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetCanchaById() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setNumero(1);
        Cancha savedCancha = canchaRepository.save(cancha);

        // When
        ResponseEntity<Cancha> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v1/canchas/" + savedCancha.getId(), Cancha.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(savedCancha.getId());
        assertThat(response.getBody().getNumero()).isEqualTo(1);
    }

    @Test
    void shouldReturnNotFoundWhenCanchaDoesNotExist() throws Exception {
        // When & Then
        try {
            restTemplate.getForEntity(getBaseUrl() + "/api/v1/canchas/999", Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 404 Not Found exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldCreateNewCancha() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setNumero(1);

        // When
        ResponseEntity<Cancha> response = restTemplate.postForEntity(
            getBaseUrl() + "/api/v1/canchas", cancha, Cancha.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getNumero()).isEqualTo(1);

        // Verify persistence in database
        Cancha fromDb = canchaRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(fromDb.getNumero()).isEqualTo(1);
    }

    @Test
    void shouldDeleteCancha() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setNumero(1);
        Cancha savedCancha = canchaRepository.save(cancha);

        // When
        restTemplate.delete(getBaseUrl() + "/api/v1/canchas/" + savedCancha.getId());

        // Then
        assertThat(canchaRepository.findById(savedCancha.getId())).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCancha() throws Exception {
        // When & Then
        try {
            restTemplate.exchange(
                getBaseUrl() + "/api/v1/canchas/999", 
                org.springframework.http.HttpMethod.DELETE, 
                null, 
                Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 404 Not Found exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldNotAllowDuplicateNumeroCancha() throws Exception {
        // Given
        Cancha cancha1 = new Cancha();
        cancha1.setNumero(1);
        canchaRepository.save(cancha1);

        Cancha cancha2 = new Cancha();
        cancha2.setNumero(1); // Same number

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/canchas", cancha2, Void.class);
            assertThat(false).as("Expected 500 Internal Server Error exception").isTrue();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(e.getResponseBodyAsString()).contains("duplicate key value violates unique constraint");
        }
    }
}