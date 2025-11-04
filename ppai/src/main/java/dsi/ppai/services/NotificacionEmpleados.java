package dsi.ppai.services;// En dsi.ppai.services.NotificacionEmpleados.java

import dsi.ppai.Interfaces.IObservadorInspeccion;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.services.MonitorCCRS;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// ESTA CLASE AHORA GESTIONA LA NOTIFICACIÓN INTERNA
public class NotificacionEmpleados implements IObservadorInspeccion {

    // Lista de observadores internos (MonitorCCRS)
    private final List<MonitorCCRS> monitores = List.of(new MonitorCCRS("Monitor1"), new MonitorCCRS("Monitor2"));

    public NotificacionEmpleados(RepositorioEmpleados repoEmpleados) {
    }

    // ... (Constructor)

    @Override
    public void actualizar(int ident, String estado, LocalDate fecha, LocalTime hora, List<String> motivos, List<String> comentarios, List<String> mails) {

        // Simulación del método generarMails()
        generarMails(ident, estado, mails);

        // --- BUCLE DE NOTIFICACIÓN (Loop del Diagrama) ---
        for (MonitorCCRS monitor : monitores) {
            // [Diagrama de Secuencia: Bucle de Actualizar]
            monitor.actualizar(ident, estado, fecha, hora, motivos, comentarios, mails);
        }
        // -------------------------------------------------
    }

    private void generarMails(int ident, String estado, List<String> mails) {
        // ... (Lógica de generación de mails)
    }
}