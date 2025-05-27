package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
public class Estado {
    private String nombre;

    public Estado(String nombre) {
        this.nombre = nombre;
    }

    public boolean esAbierta() {
        return "ABIERTA".equals(this.nombre);
    }
    public boolean esCompletamenteRealizada() {
        return "COMPLETAMENTE_REALIZADA".equals(this.nombre);
    }
    public boolean esFueraDeServicio() {
        return "FUERA_DE_SERVICIO".equals(this.nombre);
    }
    public boolean esEnMantenimiento() {
        return "EN_MANTENIMIENTO".equals(this.nombre);
    }
    public boolean esCerrada() {
        return "CERRADA".equals(this.nombre);
    }
}