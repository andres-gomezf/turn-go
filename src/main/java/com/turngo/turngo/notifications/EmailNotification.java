package com.turngo.turngo.notifications;

import com.turngo.turngo.entities.Cliente;
import com.turngo.turngo.entities.Horario;
import com.turngo.turngo.entities.Turno;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailNotification {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${turngo.mail.from:no-reply@turngo.local}")
    private String fromAddress;

    public void sendNotification(Turno t) {

            Cliente cliente = t.getCliente();
            Horario horario = t.getHorario();

            String hora = (horario.getHoraInicio() != null && horario.getHoraFin() != null)
                    ? horario.getHoraInicio().toString() + " - " + horario.getHoraFin().toString()
                    : "N/A";

            // Construimos HTML
            String body = """
                    <html lang="es">
                    <head>
                      <meta charset="UTF-8">
                      <title>Confirmación de Reserva</title>
                    </head>
                    <body style="margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f4f4f7;">
                      <table width="100%%" cellpadding="0" cellspacing="0" border="0" bgcolor="#f4f4f7">
                        <tr>
                          <td align="center" style="padding: 20px;">
                            <!-- Contenedor principal -->
                            <table width="600" cellpadding="0" cellspacing="0" border="0" bgcolor="#ffffff" style="border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                              <tr>
                                <td style="padding: 20px;">
                                  <h2 style="color: #1a73e8; margin-top:0;">Confirmación de tu reserva</h2>
                                  <p>Hola <strong>%s %s</strong>,</p>
                                  <p>Tu reserva se ha creado correctamente. Aquí tienes los detalles:</p>
                    
                                  <table cellpadding="5" cellspacing="0" border="0" width="100%%" style="border-collapse: collapse;">
                                    <tr>
                                      <td style="font-weight: bold; width: 150px;">Código de reserva:</td>
                                      <td>%d</td>
                                    </tr>
                                    <tr>
                                      <td style="font-weight: bold;">Fecha:</td>
                                      <td>%s</td>
                                    </tr>
                                    <tr>
                                      <td style="font-weight: bold;">Horario:</td>
                                      <td>%s</td>
                                    </tr>
                                  </table>
                    
                                  <p style="margin-top: 20px;">¡Gracias por reservar con nosotros!</p>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </body>
                    </html>
                    """
                    .formatted(
                    cliente.getNombre() != null ? convertToPascalCase(cliente.getNombre()) : "",
                    cliente.getApellido() != null ? convertToPascalCase(cliente.getApellido()) : "",
                    t.getId() != null ? t.getId() : 0,
                    t.getFechaInicio() != null ? t.getFechaInicio() : "N/A",
                    hora);

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
                helper.setFrom(fromAddress);
                helper.setTo(cliente.getCorreo());
                helper.setSubject("Confirmación de reserva de turno");
                helper.setText(body, true); // true = HTML

                mailSender.send(message);
                System.out.println("Correo enviado correctamente a " + cliente.getCorreo());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        public String convertToPascalCase(String s) {
            String lowercase = s.toLowerCase();

            return lowercase.substring(0, 1).toUpperCase() + lowercase.substring(1);
        }
}
