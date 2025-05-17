package dsi.ppai.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Sismografo {


    private Integer identificadorSismografo;
    private Date fechaAdquisicion;
    private Integer numeroSerie;
    private EstacionSismologica estacionSismologica; //Relacion con la estacion
    private List<CambioEstado> cambiosDeEstados; //RELACION CON CAMBIO DE ESTADO



    // Métodos adicionales según el diagrama

    // El método new() sería el constructor, que ya está cubierto por @NoArgsConstructor y @AllArgsConstructor

    // Método serEstadoActual() - necesitarías implementar la lógica específica
    public String serEstadoActual() {
        // Implementación del estado actual
        return "Estado actual del sismógrafo";
    }

    ////METODO GET
    
    public Integer getIdentificador() {
        return this.identificadorSismografo;
    }


}