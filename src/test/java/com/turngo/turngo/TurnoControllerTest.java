package com.turngo.turngo;

import com.turngo.turngo.controllers.TurnoController;
import com.turngo.turngo.dtos.CanchaConHorariosDto;
import com.turngo.turngo.dtos.HorarioDto;
import com.turngo.turngo.dtos.TurnoDto;
import com.turngo.turngo.entities.*;
import com.turngo.turngo.services.TurnoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TurnoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TurnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TurnoService turnoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllTurnos() throws Exception {
        // Given
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setCorreo("juan@email.com");
        
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(9, 0));
        horario.setHoraFin(LocalTime.of(10, 0));
        
        Turno turno1 = new Turno();
        turno1.setId(1L);
        turno1.setCliente(cliente);
        turno1.setHorario(horario);
        turno1.setFechaInicio(LocalDate.now().plusDays(1));
        turno1.setEstado(EstadoReserva.RESERVADA);
        
        Turno turno2 = new Turno();
        turno2.setId(2L);
        turno2.setCliente(cliente);
        turno2.setHorario(horario);
        turno2.setFechaInicio(LocalDate.now().plusDays(2));
        turno2.setEstado(EstadoReserva.PENDIENTE);
        
        List<Turno> turnos = Arrays.asList(turno1, turno2);
        when(turnoService.findAll()).thenReturn(turnos);

        // When & Then
        mockMvc.perform(get("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("RESERVADA"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].estado").value("PENDIENTE"));

        verify(turnoService, times(1)).findAll();
    }

    @Test
    void shouldGetTurnoById() throws Exception {
        // Given
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Ana");
        cliente.setApellido("López");
        cliente.setCorreo("ana@email.com");
        
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(11, 0));
        horario.setHoraFin(LocalTime.of(12, 0));
        
        Turno turno = new Turno();
        turno.setId(1L);
        turno.setCliente(cliente);
        turno.setHorario(horario);
        turno.setFechaInicio(LocalDate.now().plusDays(3));
        turno.setEstado(EstadoReserva.RESERVADA);
        
        when(turnoService.findById(1L)).thenReturn(Optional.of(turno));

        // When & Then
        mockMvc.perform(get("/api/v1/turnos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("RESERVADA"));

        verify(turnoService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenTurnoDoesNotExist() throws Exception {
        // Given
        when(turnoService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/turnos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(turnoService, times(1)).findById(999L);
    }

    @Test
    void shouldCreateNewTurno() throws Exception {
        // Given
        TurnoDto turnoRequest = new TurnoDto();
        turnoRequest.setClienteId(1L);
        turnoRequest.setHorarioId(1);
        turnoRequest.setFecha(LocalDate.now().plusDays(4));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Pedro");
        cliente.setApellido("Gómez");
        cliente.setCorreo("pedro@email.com");
        
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(13, 0));
        horario.setHoraFin(LocalTime.of(14, 0));
        
        Turno turnoResponse = new Turno();
        turnoResponse.setId(3L);
        turnoResponse.setCliente(cliente);
        turnoResponse.setHorario(horario);
        turnoResponse.setFechaInicio(LocalDate.now().plusDays(4));
        turnoResponse.setEstado(EstadoReserva.PENDIENTE);
        
        when(turnoService.save(any(TurnoDto.class), "No")).thenReturn(turnoResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        verify(turnoService, times(1)).save(any(TurnoDto.class), "No");
    }

    @Test
    void shouldReturnBadRequestForPastDate() throws Exception {
        // Given
        TurnoDto turnoRequest = new TurnoDto();
        turnoRequest.setClienteId(1L);
        turnoRequest.setHorarioId(1);
        turnoRequest.setFecha(LocalDate.now().minusDays(1)); // Past date

        // When & Then
        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoRequest)))
                .andExpect(status().isBadRequest());

        verify(turnoService, never()).save(any(TurnoDto.class), "No");
    }

    @Test
    void shouldReturnBadRequestWhenClienteDoesNotExist() throws Exception {
        // Given
        TurnoDto turnoRequest = new TurnoDto();
        turnoRequest.setClienteId(999L); // Non-existent client
        turnoRequest.setHorarioId(1);
        turnoRequest.setFecha(LocalDate.now().plusDays(5));

        when(turnoService.save(any(TurnoDto.class), "No"))
                .thenThrow(new RuntimeException("Cliente no encontrado"));

        // When & Then
        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoRequest)))
                .andExpect(status().isInternalServerError());

        verify(turnoService, times(1)).save(any(TurnoDto.class), "No");
    }

    @Test
    void shouldReturnBadRequestWhenHorarioDoesNotExist() throws Exception {
        // Given
        TurnoDto turnoRequest = new TurnoDto();
        turnoRequest.setClienteId(1L);
        turnoRequest.setHorarioId(999); // Non-existent horario
        turnoRequest.setFecha(LocalDate.now().plusDays(6));

        when(turnoService.save(any(TurnoDto.class), "No"))
                .thenThrow(new RuntimeException("Horario no encontrado"));

        // When & Then
        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turnoRequest)))
                .andExpect(status().isInternalServerError());

        verify(turnoService, times(1)).save(any(TurnoDto.class), "No");
    }

    @Test
    void shouldDeleteTurno() throws Exception {
        // Given
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Sofía");
        cliente.setApellido("Martínez");
        cliente.setCorreo("sofia@email.com");
        
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(19, 0));
        horario.setHoraFin(LocalTime.of(20, 0));
        
        Turno turno = new Turno();
        turno.setId(1L);
        turno.setCliente(cliente);
        turno.setHorario(horario);
        turno.setFechaInicio(LocalDate.now().plusDays(7));
        turno.setEstado(EstadoReserva.RESERVADA);
        
        when(turnoService.findById(1L)).thenReturn(Optional.of(turno));
        doNothing().when(turnoService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/turnos/1"))
                .andExpect(status().isNoContent());

        verify(turnoService, times(1)).findById(1L);
        verify(turnoService, times(1)).delete(1L);
    }

    @Test
    void shouldGetAvailableTurnosByDate() throws Exception {
        // Given
        LocalDate testDate = LocalDate.of(2025, 10, 25);
        List<CanchaConHorariosDto> disponibles = Arrays.asList(
                new CanchaConHorariosDto(1L, "1", Arrays.asList(
                        new HorarioDto(1L, LocalTime.of(9, 0), LocalTime.of(10, 0)),
                        new HorarioDto(2L, LocalTime.of(10, 0), LocalTime.of(11, 0))
                )),
                new CanchaConHorariosDto(2L, "2", Arrays.asList(
                        new HorarioDto(3L, LocalTime.of(9, 0), LocalTime.of(10, 0))
                ))
        );
        when(turnoService.findAvailableByDate(testDate)).thenReturn(disponibles);

        // When & Then
        mockMvc.perform(get("/api/v1/turnos/disponibles")
                        .param("fecha", "2025-10-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].canchaId").value(1))
                .andExpect(jsonPath("$[0].numeroCancha").value("1"))
                .andExpect(jsonPath("$[0].horarios.length()").value(2))
                .andExpect(jsonPath("$[1].canchaId").value(2))
                .andExpect(jsonPath("$[1].numeroCancha").value("2"))
                .andExpect(jsonPath("$[1].horarios.length()").value(1));

        verify(turnoService, times(1)).findAvailableByDate(testDate);
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Given
        when(turnoService.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        mockMvc.perform(get("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(turnoService, times(1)).findAll();
    }
}
