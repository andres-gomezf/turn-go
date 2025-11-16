package com.turngo.turngo.services;

import com.turngo.turngo.entities.Turno;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${turngo.mail.from:no-reply@turngo.local}")
    private String fromAddress;

    @Value("${turngo.mail.enabled:true}")
    private boolean mailEnabled;

    public void sendTurnoConfirmation(@NonNull Turno turno) {
        if (!mailEnabled) {
            logger.debug("Envío de correo deshabilitado (turngo.mail.enabled=false), se omite envío para el turno {}", turno.getId());
            return;
        }

        if (turno.getCliente() == null || turno.getCliente().getCorreo() == null) {
            logger.warn("No se puede enviar correo: el turno {} no tiene cliente o correo asociado", turno.getId());
            return;
        }

        String to = turno.getCliente().getCorreo();

        String subject = "Confirmación de reserva de turno";

        String cancha = turno.getHorario() != null
                && turno.getHorario().getCancha() != null
                && turno.getHorario().getCancha().getNumero() != null
                ? String.valueOf(turno.getHorario().getCancha().getNumero())
                : "N/A";

        String horaInicio = turno.getHorario() != null && turno.getHorario().getHoraInicio() != null
                ? turno.getHorario().getHoraInicio().toString()
                : "N/A";

        String horaFin = turno.getHorario() != null && turno.getHorario().getHoraFin() != null
                ? turno.getHorario().getHoraFin().toString()
                : "N/A";

        String fecha = turno.getFechaInicio() != null ? turno.getFechaInicio().toString() : "N/A";

        String body = """
                Hola %s %s,

                Tu reserva se ha creado correctamente.

                Detalles del turno:
                - Código de reserva: %d
                - Fecha: %s
                - Cancha: %s
                - Hora: %s - %s

                ¡Gracias por reservar con nosotros!

                """.formatted(
                turno.getCliente().getNombre() != null ? turno.getCliente().getNombre() : "",
                turno.getCliente().getApellido() != null ? turno.getCliente().getApellido() : "",
                turno.getId(),
                fecha,
                cancha,
                horaInicio,
                horaFin
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            logger.info("Correo de confirmación enviado correctamente a {} para el turno {}", to, turno.getId());
        } catch (MessagingException e) {
            // No rompemos la creación del turno si falla el mail; solo registramos el error
            logger.error("Error enviando correo de confirmación para el turno {} a {}",
                    turno.getId(), to, e);
        }
    }
}



