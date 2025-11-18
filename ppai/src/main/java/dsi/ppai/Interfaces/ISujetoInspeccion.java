package dsi.ppai.Interfaces;

import dsi.ppai.entities.OrdenDeInspeccion;

import java.util.List;

public interface ISujetoInspeccion {
    void suscribir(List<IObservadorInspeccion> observadores);
    void notificar(OrdenDeInspeccion orden, String estado, List<String> motivos, List<String> comentarios, List<String> mails);
    //void quitar(IObservadorInspeccion observador);
}