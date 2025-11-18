package dsi.ppai.services;

import dsi.ppai.Interfaces.IObservadorInspeccion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MonitorCCRS implements IObservadorInspeccion {

    private String idMonitor = "DEFAULT"; 
    private String estado;
    private LocalDate fecha;

    public MonitorCCRS() {
        System.out.println("Monitor CCRS creado (ID: " + this.idMonitor + ").");
    }

    public MonitorCCRS(String idMonitor) {
        this.idMonitor = idMonitor;
        System.out.println("Monitor CCRS creado (ID: " + idMonitor + ").");
    }
    
    @Override
    public void actualizar(int ident, String estado, LocalDate fecha, LocalTime hora, List<String> motivos, List<String> comentarios, List<String> mails) {
        System.out.println("MONITOR CCRS [" + this.idMonitor + "]: Recibiendo actualización por Orden N° " + ident);

        buscarSismografo(ident);
        setEstadoSismografo(estado);
        setFecha(fecha);
        setHora(hora);
        setMotivos(motivos);
        setComentarios(comentarios);

        System.out.println("   - Monitor actualizado. Estado Sismógrafo: " + estado);
    }

    private void buscarSismografo(int ident) { System.out.println("      -> Buscando Sismógrafo asociado a Orden " + ident); }
    private void setEstadoSismografo(String estado) { this.estado = estado; }
    private void setFecha(LocalDate fecha) { this.fecha = fecha; }
    private void setHora(LocalTime hora) {}
    private void setMotivos(List<String> motivos) {}
    private void setComentarios(List<String> comentarios) {}
}