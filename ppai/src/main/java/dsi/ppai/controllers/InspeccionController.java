package dsi.ppai.controllers;

import dsi.ppai.services.GestorInspeccion;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/inspecciones")


public class InspeccionController {

    private final GestorInspeccion gestor;

    public InspeccionController(GestorInspeccion gestor) {
        this.gestor = gestor;
    }

    @PostMapping("/cerrar")
    public String cerrarOrden(@RequestBody CierreRequest dto) {
        gestor.cerrarOrden(dto.numeroOrden, dto.observacion, dto.motivosSeleccionados());
        return "Orden cerrada correctamente.";
    }
}
