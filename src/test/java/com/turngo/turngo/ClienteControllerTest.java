package com.turngo.turngo;

import com.turngo.turngo.controllers.ClienteController;
import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.services.ClienteService;
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


@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllClients() throws Exception {
        // Given
        List<Cliente> clientes = Arrays.asList(
                new Cliente(1L, "Juan", "Pérez", "juan@email.com"),
                new Cliente(2L, "María", "García", "maria@email.com")
        );

        when(clienteService.findAll()).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].apellido").value("Pérez"))
                .andExpect(jsonPath("$[0].correo").value("juan@email.com"))
                .andExpect(jsonPath("$[1].nombre").value("María"))
                .andExpect(jsonPath("$[1].apellido").value("García"));

        verify(clienteService, times(1)).findAll();
    }

    @Test
    void shouldGetClientById() throws Exception {
        // Given
        Cliente cliente = new Cliente(1L, "Juan", "Pérez", "juan@email.com");
        when(clienteService.findById(1L)).thenReturn(Optional.of(cliente));

        // When & Then
        mockMvc.perform(get("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.correo").value("juan@email.com"));

        verify(clienteService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        // Given
        when(clienteService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(clienteService, times(1)).findById(999L);
    }

    @Test
    void shouldCreateNewClient() throws Exception {
        // Given
        Cliente clienteRequest = new Cliente(null, "Ana", "López", "ana@email.com");
        Cliente clienteResponse = new Cliente(3L, "Ana", "López", "ana@email.com");

        when(clienteService.save(any(Cliente.class))).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("López"))
                .andExpect(jsonPath("$.correo").value("ana@email.com"));

        verify(clienteService, times(1)).save(any(Cliente.class));
    }
/* Actualmente no tenemos /PUT
    @Test
    void shouldUpdateClient() throws Exception {
        // Given
        Cliente clienteExistente = new Cliente(1L, "Juan", "Pérez", "juan@email.com");
        Cliente clienteActualizado = new Cliente(1L, "Juan Carlos","Pérez", "juancarlos@email.com");

        when(clienteService.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteService.save(any(Cliente.class))).thenReturn(clienteActualizado);

        // When & Then
        mockMvc.perform(put("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.correo").value("juancarlos@email.com"));

        verify(clienteService, times(1)).findById(1L);
        verify(clienteService, times(1)).save(any(Cliente.class));
    }
*/
    @Test
    void shouldDeleteClient() throws Exception {
        // Given
        when(clienteService.findById(1L)).thenReturn(Optional.of(new Cliente(1L, "Juan", "Pérez", "juan@email.com")));
        doNothing().when(clienteService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).delete(1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidData() throws Exception {
        // Given - Client without name
        Cliente clienteInvalido = new Cliente(null, "", "invalid-email", "123");

        // When & Then
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).save(any(Cliente.class));
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Given
        when(clienteService.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        mockMvc.perform(get("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(clienteService, times(1)).findAll();
    }
}
