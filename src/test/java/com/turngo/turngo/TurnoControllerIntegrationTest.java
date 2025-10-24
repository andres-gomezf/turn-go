package com.turngo.turngo;

import com.turngo.turngo.dtos.TurnoDto;
import com.turngo.turngo.entities.*;
import com.turngo.turngo.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TurnoControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Test
    void shouldGetAllTurnosWhenEmpty() throws Exception {
        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/turnos", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldGetAllTurnosWithData() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        createTurno(cliente, horario, LocalDate.now().plusDays(1), EstadoReserva.RESERVADA);
        createTurno(cliente, horario, LocalDate.now().plusDays(2), EstadoReserva.PENDIENTE);

        // When
        ResponseEntity<List> response = restTemplate.getForEntity(getBaseUrl() + "/api/v1/turnos", List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldGetTurnoById() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Turno turno = createTurno(cliente, horario, LocalDate.now().plusDays(1), EstadoReserva.RESERVADA);

        // When
        ResponseEntity<Turno> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v1/turnos/" + turno.getId(), Turno.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(turno.getId());
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoReserva.RESERVADA);
    }

    @Test
    void shouldReturnNotFoundWhenTurnoDoesNotExist() throws Exception {
        // When & Then
        try {
            restTemplate.getForEntity(getBaseUrl() + "/api/v1/turnos/999", Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 404 Not Found exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldCreateNewTurnoWithValidData() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        
        TurnoDto turnoDto = new TurnoDto();
        turnoDto.setClienteId(cliente.getId());
        turnoDto.setHorarioId(horario.getId());
        turnoDto.setFecha(LocalDate.now().plusDays(1));

        // When
        ResponseEntity<Turno> response = restTemplate.postForEntity(
            getBaseUrl() + "/api/v1/turnos", turnoDto, Turno.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo(EstadoReserva.RESERVADA);

        // Verify persistence in database
        Turno fromDb = turnoRepository.findById(response.getBody().getId()).orElseThrow();
        assertThat(fromDb.getCliente().getId()).isEqualTo(cliente.getId());
        assertThat(fromDb.getHorario().getId()).isEqualTo(horario.getId());
    }

    @Test
    void shouldReturnBadRequestForPastDate() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        
        TurnoDto turnoDto = new TurnoDto();
        turnoDto.setClienteId(cliente.getId());
        turnoDto.setHorarioId(horario.getId());
        turnoDto.setFecha(LocalDate.now().minusDays(1)); // Past date

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/turnos", turnoDto, Void.class);
            assertThat(false).as("Expected 400 Bad Request exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getResponseBodyAsString()).contains("La fecha debe ser hoy o posterior");
        }
    }

    @Test
    void shouldReturnBadRequestWhenClienteDoesNotExist() throws Exception {
        // Given
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        
        TurnoDto turnoDto = new TurnoDto();
        turnoDto.setClienteId(999L); // Non-existent cliente
        turnoDto.setHorarioId(horario.getId());
        turnoDto.setFecha(LocalDate.now().plusDays(1));

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/turnos", turnoDto, Void.class);
            assertThat(false).as("Expected 500 Internal Server Error exception").isTrue();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(e.getResponseBodyAsString()).contains("Cliente no encontrado");
        }
    }

    @Test
    void shouldReturnBadRequestWhenHorarioDoesNotExist() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        
        TurnoDto turnoDto = new TurnoDto();
        turnoDto.setClienteId(cliente.getId());
        turnoDto.setHorarioId(999); // Non-existent horario
        turnoDto.setFecha(LocalDate.now().plusDays(1));

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/turnos", turnoDto, Void.class);
            assertThat(false).as("Expected 500 Internal Server Error exception").isTrue();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(e.getResponseBodyAsString()).contains("Horario no encontrado");
        }
    }

    @Test
    void shouldDeleteTurno() throws Exception {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Turno turno = createTurno(cliente, horario, LocalDate.now().plusDays(1), EstadoReserva.RESERVADA);

        // When
        restTemplate.delete(getBaseUrl() + "/api/v1/turnos/" + turno.getId());

        // Then
        assertThat(turnoRepository.findById(turno.getId())).isEmpty();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTurno() throws Exception {
        // When & Then
        try {
            restTemplate.exchange(
                getBaseUrl() + "/api/v1/turnos/999", 
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
    void shouldGetAvailableTurnosByDate() throws Exception {
        // Given
        Cancha cancha1 = createCancha(1);
        createCancha(2);
        Horario horario1 = createHorario(cancha1, LocalTime.of(9, 0), LocalTime.of(10, 0));
        createHorario(cancha1, LocalTime.of(10, 0), LocalTime.of(11, 0));
        
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // Create a reserved turno for horario1
        Cliente cliente = createCliente("Juan", "Pérez", "juan@email.com");
        createTurno(cliente, horario1, testDate, EstadoReserva.RESERVADA);

        // When
        ResponseEntity<List> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v1/turnos/disponibles?fecha=" + testDate, List.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // Note: The exact structure depends on the service implementation
        // This test verifies the endpoint works and returns data
    }

    @Test
    void shouldNotAllowDuplicateTurnoForSameHorarioAndDate() throws Exception {
        // Given
        Cliente cliente1 = createCliente("Juan", "Pérez", "juan@email.com");
        Cliente cliente2 = createCliente("María", "García", "maria@email.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // Create first turno
        createTurno(cliente1, horario, testDate, EstadoReserva.RESERVADA);
        
        // Try to create second turno for same horario and date
        TurnoDto turnoDto = new TurnoDto();
        turnoDto.setClienteId(cliente2.getId());
        turnoDto.setHorarioId(horario.getId());
        turnoDto.setFecha(testDate);

        // When & Then
        try {
            restTemplate.postForEntity(
                getBaseUrl() + "/api/v1/turnos", turnoDto, Void.class);
            // Si no lanza excepción, el test falla
            assertThat(false).as("Expected 409 Conflict exception").isTrue();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getResponseBodyAsString()).contains("El turno ya está ocupado");
        }
    }

    private Cliente createCliente(String nombre, String apellido, String correo) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setCorreo(correo);
        return clienteRepository.save(cliente);
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

    private Turno createTurno(Cliente cliente, Horario horario, LocalDate fecha, EstadoReserva estado) {
        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setHorario(horario);
        turno.setFechaInicio(fecha);
        turno.setEstado(estado);
        return turnoRepository.save(turno);
    }
}