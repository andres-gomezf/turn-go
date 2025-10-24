package com.turngo.turngo;

import com.turngo.turngo.dtos.HorarioFlatDto;
import com.turngo.turngo.entities.*;
import com.turngo.turngo.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class TurnoRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private HorarioRepository horarioRepository;

    @Test
    void shouldSaveAndFindTurno() {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@test.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        
        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setHorario(horario);
        turno.setFechaInicio(LocalDate.of(2025, 10, 25));
        turno.setEstado(EstadoReserva.RESERVADA);

        // When
        Turno savedTurno = turnoRepository.save(turno);
        Optional<Turno> foundTurno = turnoRepository.findById(savedTurno.getId());

        // Then
        assertThat(foundTurno).isPresent();
        assertThat(foundTurno.get().getCliente().getNombre()).isEqualTo("Juan");
        assertThat(foundTurno.get().getHorario().getHoraInicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(foundTurno.get().getFechaInicio()).isEqualTo(LocalDate.of(2025, 10, 25));
        assertThat(foundTurno.get().getEstado()).isEqualTo(EstadoReserva.RESERVADA);
    }

    @Test
    void shouldFindAvailableByDate_WhenNoTurnosExist() {
        // Given
        Cancha cancha1 = createCancha(1);
        Cancha cancha2 = createCancha(2);
        
        Horario horario1 = createHorario(cancha1, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Horario horario2 = createHorario(cancha1, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Horario horario3 = createHorario(cancha2, LocalTime.of(9, 0), LocalTime.of(10, 0));

        LocalDate testDate = LocalDate.of(2025, 10, 25);

        // When
        List<HorarioFlatDto> disponibles = turnoRepository.findAvailableByDate(testDate);

        // Then
        assertThat(disponibles).hasSize(3);
        assertThat(disponibles).extracting(HorarioFlatDto::getHorarioId)
                .containsExactlyInAnyOrder(horario1.getId().longValue(), horario2.getId().longValue(), horario3.getId().longValue());
    }

    @Test
    void shouldFindAvailableByDate_WhenSomeTurnosExist() {
        // Given
        Cliente cliente = createCliente("Juan", "Pérez", "juan@test.com");
        Cancha cancha = createCancha(1);
        
        Horario horario1 = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Horario horario2 = createHorario(cancha, LocalTime.of(10, 0), LocalTime.of(11, 0));
        Horario horario3 = createHorario(cancha, LocalTime.of(11, 0), LocalTime.of(12, 0));

        LocalDate testDate = LocalDate.of(2025, 10, 25);

        // Ocupar horario1
        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setHorario(horario1);
        turno.setFechaInicio(testDate);
        turno.setEstado(EstadoReserva.RESERVADA);
        turnoRepository.save(turno);

        // When
        List<HorarioFlatDto> disponibles = turnoRepository.findAvailableByDate(testDate);

        // Then
        assertThat(disponibles).hasSize(2);
        assertThat(disponibles).extracting(HorarioFlatDto::getHorarioId)
                .containsExactlyInAnyOrder(horario2.getId().longValue(), horario3.getId().longValue());
        assertThat(disponibles).extracting(HorarioFlatDto::getHorarioId)
                .doesNotContain(horario1.getId().longValue());
    }

    @Test
    void shouldNotFindAvailableByDate_WhenAllTurnosOccupied() {
        // Given
        Cliente cliente1 = createCliente("Juan", "Pérez", "juan@test.com");
        Cliente cliente2 = createCliente("María", "García", "maria@test.com");
        Cancha cancha = createCancha(1);
        
        Horario horario1 = createHorario(cancha, LocalTime.of(9, 0), LocalTime.of(10, 0));
        Horario horario2 = createHorario(cancha, LocalTime.of(10, 0), LocalTime.of(11, 0));

        LocalDate testDate = LocalDate.of(2025, 10, 25);

        // Ocupar todos los horarios
        Turno turno1 = new Turno();
        turno1.setCliente(cliente1);
        turno1.setHorario(horario1);
        turno1.setFechaInicio(testDate);
        turno1.setEstado(EstadoReserva.RESERVADA);
        turnoRepository.save(turno1);

        Turno turno2 = new Turno();
        turno2.setCliente(cliente2);
        turno2.setHorario(horario2);
        turno2.setFechaInicio(testDate);
        turno2.setEstado(EstadoReserva.RESERVADA);
        turnoRepository.save(turno2);

        // When
        List<HorarioFlatDto> disponibles = turnoRepository.findAvailableByDate(testDate);

        // Then
        assertThat(disponibles).isEmpty();
    }

    @Test
    void shouldDeleteTurno() {
        // Given
        Cliente cliente = createCliente("Carlos", "Ruiz", "carlos@test.com");
        Cancha cancha = createCancha(1);
        Horario horario = createHorario(cancha, LocalTime.of(14, 0), LocalTime.of(15, 0));
        
        Turno turno = new Turno();
        turno.setCliente(cliente);
        turno.setHorario(horario);
        turno.setFechaInicio(LocalDate.of(2025, 10, 25));
        turno.setEstado(EstadoReserva.RESERVADA);
        Turno savedTurno = turnoRepository.save(turno);

        // When
        turnoRepository.deleteById(savedTurno.getId());

        // Then
        Optional<Turno> deletedTurno = turnoRepository.findById(savedTurno.getId());
        assertThat(deletedTurno).isEmpty();
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
}
