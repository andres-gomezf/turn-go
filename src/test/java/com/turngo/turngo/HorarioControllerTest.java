package com.turngo.turngo;

import com.turngo.turngo.controllers.HorarioController;
import com.turngo.turngo.dtos.HorarioPostDto;
import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.entities.Horario;
import com.turngo.turngo.services.HorarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HorarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HorarioService horarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllHorarios() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario1 = new Horario();
        horario1.setId(1);
        horario1.setCancha(cancha);
        horario1.setHoraInicio(LocalTime.of(9, 0));
        horario1.setHoraFin(LocalTime.of(10, 0));
        
        Horario horario2 = new Horario();
        horario2.setId(2);
        horario2.setCancha(cancha);
        horario2.setHoraInicio(LocalTime.of(10, 0));
        horario2.setHoraFin(LocalTime.of(11, 0));
        
        List<Horario> horarios = Arrays.asList(horario1, horario2);
        when(horarioService.findAll()).thenReturn(horarios);

        // When & Then
        mockMvc.perform(get("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].horaInicio").value("09:00"))
                .andExpect(jsonPath("$[0].horaFin").value("10:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].horaInicio").value("10:00"))
                .andExpect(jsonPath("$[1].horaFin").value("11:00"));

        verify(horarioService, times(1)).findAll();
    }

    @Test
    void shouldGetHorarioById() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(9, 0));
        horario.setHoraFin(LocalTime.of(10, 0));
        when(horarioService.findById(1)).thenReturn(Optional.of(horario));

        // When & Then
        mockMvc.perform(get("/api/v1/horarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.horaInicio").value("09:00"))
                .andExpect(jsonPath("$.horaFin").value("10:00"));

        verify(horarioService, times(1)).findById(1);
    }

    @Test
    void shouldReturnNotFoundWhenHorarioDoesNotExist() throws Exception {
        // Given
        when(horarioService.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/horarios/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(horarioService, times(1)).findById(999);
    }

    @Test
    void shouldCreateNewHorario() throws Exception {
        // Given
        HorarioPostDto horarioRequest = new HorarioPostDto();
        horarioRequest.setCanchaId(1L);
        horarioRequest.setHoraInicio(LocalTime.of(11, 0));
        horarioRequest.setHoraFin(LocalTime.of(12, 0));

        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horarioResponse = new Horario();
        horarioResponse.setId(3);
        horarioResponse.setCancha(cancha);
        horarioResponse.setHoraInicio(LocalTime.of(11, 0));
        horarioResponse.setHoraFin(LocalTime.of(12, 0));
        when(horarioService.save(any(HorarioPostDto.class))).thenReturn(horarioResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.horaInicio").value("11:00"))
                .andExpect(jsonPath("$.horaFin").value("12:00"));

        verify(horarioService, times(1)).save(any(HorarioPostDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenCanchaDoesNotExist() throws Exception {
        // Given
        HorarioPostDto horarioRequest = new HorarioPostDto();
        horarioRequest.setCanchaId(999L); // Non-existent cancha
        horarioRequest.setHoraInicio(LocalTime.of(13, 0));
        horarioRequest.setHoraFin(LocalTime.of(14, 0));

        when(horarioService.save(any(HorarioPostDto.class)))
                .thenThrow(new RuntimeException("Cancha no encontrada"));

        // When & Then
        mockMvc.perform(post("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioRequest)))
                .andExpect(status().isInternalServerError());

        verify(horarioService, times(1)).save(any(HorarioPostDto.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidHorarioData() throws Exception {
        // Given - Horario with null horaInicio
        HorarioPostDto horarioRequest = new HorarioPostDto();
        horarioRequest.setCanchaId(1L);
        horarioRequest.setHoraInicio(null); // Invalid data
        horarioRequest.setHoraFin(LocalTime.of(15, 0));

        // When & Then
        mockMvc.perform(post("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioRequest)))
                .andExpect(status().isBadRequest());

        verify(horarioService, never()).save(any(HorarioPostDto.class));
    }

    @Test
    void shouldDeleteHorario() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        
        Horario horario = new Horario();
        horario.setId(1);
        horario.setCancha(cancha);
        horario.setHoraInicio(LocalTime.of(16, 0));
        horario.setHoraFin(LocalTime.of(17, 0));
        when(horarioService.findById(1)).thenReturn(Optional.of(horario));
        doNothing().when(horarioService).delete(1);

        // When & Then
        mockMvc.perform(delete("/api/v1/horarios/1"))
                .andExpect(status().isNoContent());

        verify(horarioService, times(1)).findById(1);
        verify(horarioService, times(1)).delete(1);
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Given
        when(horarioService.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        mockMvc.perform(get("/api/v1/horarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(horarioService, times(1)).findAll();
    }
}
