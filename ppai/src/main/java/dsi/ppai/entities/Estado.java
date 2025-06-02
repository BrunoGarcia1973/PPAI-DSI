package dsi.ppai.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Estado {
    private String ambito;
    private String nombre;

    public Estado(String ambito, String nombre) {
        this.nombre = nombre;
    }

    public boolean sosCompletamenteRealizada() {
        return "COMPLETAMENTE_REALIZADA".equals(this.nombre);
    }
    public boolean sosFueraDeServicio() {
        return "FUERA_DE_SERVICIO".equals(this.nombre);
    }
    public boolean esEnMantenimiento() {
        return "EN_MANTENIMIENTO".equals(this.nombre);
    }
    public boolean sosCerrada() {
        return "CERRADA".equals(this.nombre);
    }
    public boolean sosAmbitoOI(){
        return "OI".equals(this.ambito);
    }


}