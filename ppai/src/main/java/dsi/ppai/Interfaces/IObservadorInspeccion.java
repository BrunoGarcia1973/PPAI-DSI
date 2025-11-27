package dsi.ppai.Interfaces;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public interface IObservadorInspeccion {
    void actualizar(int ident, String estado, LocalDate fecha, LocalTime hora, List<String> motivos, List<String> comentarios, List<String> mails);
}