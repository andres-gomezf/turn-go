package com.turngo.turngo;

import com.turngo.turngo.controllers.CanchaController;
import com.turngo.turngo.entities.Cancha;
import com.turngo.turngo.services.CanchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CanchaController.class)
@AutoConfigureMockMvc(addFilters = false)
class CanchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CanchaService canchaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllCanchas() throws Exception {
        // Given
        Cancha cancha1 = new Cancha();
        cancha1.setId(1L);
        cancha1.setNumero(1);
        
        Cancha cancha2 = new Cancha();
        cancha2.setId(2L);
        cancha2.setNumero(2);
        
        List<Cancha> canchas = Arrays.asList(cancha1, cancha2);
        when(canchaService.findAll()).thenReturn(canchas);

        // When & Then
        mockMvc.perform(get("/api/v1/canchas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numero").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].numero").value(2));

        verify(canchaService, times(1)).findAll();
    }

    @Test
    void shouldGetCanchaById() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        when(canchaService.findById(1L)).thenReturn(Optional.of(cancha));

        // When & Then
        mockMvc.perform(get("/api/v1/canchas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value(1));

        verify(canchaService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenCanchaDoesNotExist() throws Exception {
        // Given
        when(canchaService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/canchas/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(canchaService, times(1)).findById(999L);
    }

    @Test
    void shouldCreateNewCancha() throws Exception {
        // Given
        Cancha canchaRequest = new Cancha();
        canchaRequest.setNumero(3);
        
        Cancha canchaResponse = new Cancha();
        canchaResponse.setId(3L);
        canchaResponse.setNumero(3);
        when(canchaService.save(any(Cancha.class))).thenReturn(canchaResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/canchas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(canchaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.numero").value(3));

        verify(canchaService, times(1)).save(any(Cancha.class));
    }

    @Test
    void shouldDeleteCancha() throws Exception {
        // Given
        Cancha cancha = new Cancha();
        cancha.setId(1L);
        cancha.setNumero(1);
        when(canchaService.findById(1L)).thenReturn(Optional.of(cancha));
        doNothing().when(canchaService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/canchas/1"))
                .andExpect(status().isNoContent());

        verify(canchaService, times(1)).findById(1L);
        verify(canchaService, times(1)).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentCancha() throws Exception {
        // Given
        when(canchaService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/v1/canchas/999"))
                .andExpect(status().isNotFound());

        verify(canchaService, times(1)).findById(999L);
        verify(canchaService, never()).delete(any(Long.class));
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Given
        when(canchaService.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        mockMvc.perform(get("/api/v1/canchas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(canchaService, times(1)).findAll();
    }
}
