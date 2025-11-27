package dsi.ppai.services;

import dsi.ppai.Interfaces.IObservadorInspeccion;
import dsi.ppai.repositories.RepositorioEmpleados;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class NotificacionEmpleados implements IObservadorInspeccion {

    private final List<MonitorCCRS> monitores = List.of(new MonitorCCRS("Monitor1"), new MonitorCCRS("Monitor2"));

    public NotificacionEmpleados(RepositorioEmpleados repoEmpleados) {
    }

    @Override
    public void actualizar(int ident, String estado, LocalDate fecha, LocalTime hora, List<String> motivos, List<String> comentarios, List<String> mails) {

        generarMails(ident, estado, mails);

        for (MonitorCCRS monitor : monitores) {
            monitor.actualizar(ident, estado, fecha, hora, motivos, comentarios, mails);
        }
    }

    private void generarMails(int ident, String estado, List<String> mails) {
        // Simulación de generación de mails
    }
}