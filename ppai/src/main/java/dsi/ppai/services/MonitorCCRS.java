package dsi.ppai.services;

import dsi.ppai.Interfaces.IObservadorInspeccion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MonitorCCRS implements IObservadorInspeccion {

    // Atributos internos (simulados)
    private String idMonitor = "DEFAULT"; // ID por defecto si se usa el constructor vacío
    private String estado;
    private LocalDate fecha;

    // --- CONSTRUCTOR VACÍO AÑADIDO ---
    // Este constructor permite que Spring (y otras clases) creen la instancia sin argumentos.
    public MonitorCCRS() {
        // Inicialización simple para el monitoreo
        System.out.println("Monitor CCRS creado (ID: " + this.idMonitor + ").");
    }

    // Opcional: Constructor para dar un ID si se usa la creación manual de listas
    public MonitorCCRS(String idMonitor) {
        this.idMonitor = idMonitor;
        System.out.println("Monitor CCRS creado (ID: " + idMonitor + ").");
    }
    // ----------------------------------

    @Override
    public void actualizar(int ident, String estado, LocalDate fecha, LocalTime hora, List<String> motivos, List<String> comentarios, List<String> mails) {
        System.out.println("🖥️ MONITOR CCRS [" + this.idMonitor + "]: Recibiendo actualización por Orden N° " + ident);

        // Simulación de los métodos del diagrama de secuencia
        buscarSismografo(ident);
        setEstadoSismografo(estado);
        setFecha(fecha);
        setHora(hora);
        setMotivos(motivos);
        setComentarios(comentarios);

        System.out.println("   - Monitor actualizado. Estado Sismógrafo: " + estado);
    }

    // Métodos simulados (setters y búsqueda)
    private void buscarSismografo(int ident) { System.out.println("      -> Buscando Sismógrafo asociado a Orden " + ident); }
    private void setEstadoSismografo(String estado) { this.estado = estado; }
    private void setFecha(LocalDate fecha) { this.fecha = fecha; }
    private void setHora(LocalTime hora) { /* Lógica pendiente */ }
    private void setMotivos(List<String> motivos) { /* Lógica pendiente */ }
    private void setComentarios(List<String> comentarios) { /* Lógica pendiente */ }
}