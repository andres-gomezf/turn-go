package com.turngo.turngo;

import com.turngo.turngo.dtos.HorarioPostDto;
import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.entities.Horario;
import com.turngo.turngo.repositories.CanchaRepository;
import com.turngo.turngo.repositories.HorarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class HorarioControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Test
    void shouldGetAllHorariosWhenEmpty() throws Exception {
        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/horarios", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldGetAllHorariosWithData() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        createHorario(cancha, LocalTime.of(10, 0), LocalTime.of(11, 0));

        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/horarios", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetHorarioById() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));

        // When
        ResponseEntity<Horario> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v1/horarios/" + horario.getId(), Horario.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(horario.getId());
        assertThat(response.getBody().getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getBody().getHoraFin()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void shouldReturnNotFoundWhenHorarioDoesNotExist() throws Exception {
        // When & Then
        try {
            restTemplate.getForEntity(getBaseUrl() + "/api/v1/horarios/999", Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 404 Not Found exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldCreateNewHorarioWithValidCancha() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        HorarioPostDto horarioDto = new HorarioPostDto();
        horarioDto.setCanchaId(cancha.getId());
        horarioDto.setHoraInicio(LocalTime.of(9, 0));
        horarioDto.setHoraFin(LocalTime.of(10, 0));

        // When
        ResponseEntity<Horario> response = restTemplate.postForEntity(
            getBaseUrl() + "/api/v1/horarios", horarioDto, Horario.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(response.getBody().getHoraFin()).isEqualTo(LocalTime.of(10, 0));

        // Verify persistence in database
        Horario fromDb = horarioRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(fromDb.getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(fromDb.getHoraFin()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void shouldReturnBadRequestWhenCanchaDoesNotExist() throws Exception {
        // Given
        HorarioPostDto horarioDto = new HorarioPostDto();
        horarioDto.setCanchaId(999L); // Non-existent cancha
        horarioDto.setHoraInicio(LocalTime.of(9, 0));
        horarioDto.setHoraFin(LocalTime.of(10, 0));

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/horarios", horarioDto, Void.class);
            assertThat(false).as("Expected 500 Internal Server Error exception").isTrue();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(e.getResponseBodyAsString()).contains("Cancha no encontrada");
        }
    }

    @Test
    void shouldReturnBadRequestForInvalidHorarioData() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        HorarioPostDto horarioDto = new HorarioPostDto();
        horarioDto.setCanchaId(cancha.getId());
        horarioDto.setHoraInicio(null); // Invalid data
        horarioDto.setHoraFin(LocalTime.of(10, 0));

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/horarios", horarioDto, Void.class);
            assertThat(false).as("Expected 400 Bad Request exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getResponseBodyAsString()).contains("La hora de inicio no puede estar vacía");
        }
    }

    @Test
    void shouldDeleteHorario() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));

        // When
        restTemplate.delete(getBaseUrl() + "/api/v1/horarios/" + horario.getId());

        // Then
        assertThat(horarioRepository.findById(horario.getId())).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentHorario() throws Exception {
        // When & Then
        try {
            restTemplate.exchange(
                getBaseUrl() + "/api/v1/horarios/999", 
                org.springframework.http.HttpMethod.DELETE, 
                null, 
                Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 404 Not Found exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    private Cancha createCancha(Integer numero) {
        Cancha cancha = new Cancha();
        cancha.setNumero(numero);
        return canchaRepository.save(cancha);
    }

    private Horario createHorario(Cancha cancha, LocalTime horaInicio, LocalTime horaFin) {
        Horario horario = new Horario();
        horario.setCancha(cancha);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        return horarioRepository.save(horario);
    }
}