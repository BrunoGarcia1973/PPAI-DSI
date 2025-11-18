package dsi.ppai.services;

import dsi.ppai.entities.*;
import dsi.ppai.repositories.RepositorioEmpleados;
import dsi.ppai.repositories.RepositorioEstados;
import dsi.ppai.repositories.RepositorioMotivoTipo;
import dsi.ppai.repositories.RepositorioOrdenes;
import dsi.ppai.repositories.RepositorioSismografos;
import dsi.ppai.repositories.RepositorioUsuarios;
import dsi.ppai.Interfaces.IObservadorInspeccion;
import dsi.ppai.Interfaces.ISujetoInspeccion;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Data
public class GestorInspeccion implements ISujetoInspeccion {

    private final RepositorioOrdenes repoOrdenes;
    private final RepositorioEstados repoEstados;
    private final Sesion sesion;
    private final RepositorioMotivoTipo repoMotivos;
    private final RepositorioUsuarios repoUsuarios;
    private final RepositorioSismografos repoSismografos;
    private final RepositorioEmpleados repoEmpleados;
    private final List<IObservadorInspeccion> observadores;

    public GestorInspeccion(RepositorioOrdenes repoOrdenes, RepositorioEstados repoEstados, 
                           Sesion sesion, RepositorioMotivoTipo repoMotivos, 
                           RepositorioUsuarios repoUsuarios, RepositorioSismografos repoSismografos,
                           RepositorioEmpleados repoEmpleados) {
        this.repoOrdenes = repoOrdenes;
        this.repoEstados = repoEstados;
        this.sesion = sesion;
        this.repoMotivos = repoMotivos;
        this.repoUsuarios = repoUsuarios;
        this.repoSismografos = repoSismografos;
        this.repoEmpleados = repoEmpleados;
        this.observadores = new java.util.ArrayList<>();
        
        // Suscribir observadores al inicializar el Gestor
        this.suscribir(Arrays.asList(new NotificacionEmpleados(repoEmpleados)));
    }


    // --- IMPLEMENTACIÓN DEL SUJETO (Observer) ---
    @Override
    public void suscribir(List<IObservadorInspeccion> nuevosObservadores) {
        for (IObservadorInspeccion observador : nuevosObservadores) {
            if (!observadores.contains(observador)) {
                observadores.add(observador);
            }
        }
    }

    @Override
    public void notificar(OrdenDeInspeccion orden, String estado, List<String> motivos, List<String> comentarios, List<String> mails) {
        LocalDate fecha = orden.getFechaHoraCierre().toLocalDate();
        LocalTime hora = orden.getFechaHoraCierre().toLocalTime();
        int ident = orden.getNumOrden().intValue();

        System.out.println(">>> INICIANDO NOTIFICACIÓN DE CIERRE DE ORDEN " + ident + " <<<");

        for (IObservadorInspeccion observador : this.observadores) {
            observador.actualizar(ident, estado, fecha, hora, motivos, comentarios, mails);
        }
    }

    //@Override
    //public void quitar(IObservadorInspeccion observador) {
    //}

    // --- LÓGICA DE CIERRE ---
    @Transactional
    public void cerrarOrden(Long numeroOrden, String observacion, List<MotivoFueraServicio> motivosSeleccionados) {
        Empleado empleado = sesion.obtenerEmpleadoLogueado();
        if (empleado == null) {
            throw new IllegalStateException("No hay Responsable de Inspección logueado en la sesión.");
        }

        Optional<OrdenDeInspeccion> ordenOpt = buscarOrdenDeInspeccion(numeroOrden);

        if (ordenOpt.isEmpty()) {
            throw new IllegalArgumentException("La orden no existe: " + numeroOrden);
        }

        OrdenDeInspeccion orden = ordenOpt.get();
        // Validaciones
        if (!orden.sosDeEmpleado(empleado)) { throw new IllegalStateException("La orden no pertenece al empleado logueado."); }
        if (!orden.sosCompletamenteRealizada()) { throw new IllegalStateException("La orden no está totalmente realizada y no puede ser cerrada."); }
        if (observacion == null || observacion.isBlank()) { throw new IllegalArgumentException("Debe ingresar una observación para el cierre."); }

        // 11) Completar datos de cierre de la ORDEN
        Estado estadoAnteriorOrden = orden.getEstado();
        orden.setFechaHoraCierre(OffsetDateTime.now().toLocalDateTime());
        orden.setObservacionCierre(observacion);

        // 12) Poner sismógrafo fuera de servicio (si aplica)
        if (motivosSeleccionados != null && !motivosSeleccionados.isEmpty()) {
            Estado estadoFueraDeServicio = repoEstados.buscarEstado("FUERA_DE_SERVICIO");
            EstacionSismologica estacion = orden.getEstacionSismologica();
            Sismografo sismografo = estacion.getSismografo();

            sismografo.marcarFueraDeServicio(motivosSeleccionados, empleado, estadoFueraDeServicio);
            repoSismografos.save(sismografo);
        }

        // 11) Cambiar el estado de la ORDEN a CERRADA y registrar el cambio
        Estado estadoCerrada = repoEstados.buscarEstado("CERRADA");
        orden.setEstado(estadoCerrada);
        CambioEstado cambioOrden = new CambioEstado(
                empleado, estadoAnteriorOrden, estadoCerrada, OffsetDateTime.now(), null, null
        );
        orden.registrarCambioEstado(cambioOrden);

        repoOrdenes.insertar(orden);

        // ENVÍO DE NOTIFICACIONES
        List<String> mailsSimulados = List.of("reparacion1@empresa.com", "reparacion2@empresa.com");
        List<String> motivosDesc = new ArrayList<>();
        List<String> comentarios = new ArrayList<>();

        if (motivosSeleccionados != null) {
            for (MotivoFueraServicio mf : motivosSeleccionados) {
                motivosDesc.add(mf.getMotivoTipo().getDescripcion());
                if (mf.getComentario() != null) {
                    comentarios.add(mf.getComentario());
                } else {
                    comentarios.add("");
                }
            }
        }
        notificar(orden, "CERRADA", motivosDesc, comentarios, mailsSimulados);
    }

    //MÉTODOS AUXILIARES
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorNombre(String nombreUsuario) {
        Optional<Usuario> usuarioOpt = repoUsuarios.findByNombreUsuario(nombreUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            Empleado empleado = usuario.getEmpleado();
            if (empleado != null) {
                empleado.getId();
            }
        }
        return usuarioOpt;
    }

    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI() {
        Empleado empleadoLogueado = sesion.obtenerEmpleadoLogueado();
        if (empleadoLogueado == null) return List.of();
        return buscarOrdenesDeInspeccionDeRI(empleadoLogueado);
    }


    @Transactional(readOnly = true)
    public List<OrdenDeInspeccion> buscarOrdenesDeInspeccionDeRI(Empleado empleado) {
        if (empleado == null) return List.of();

        final String NOMBRE_ESTADO_REQUERIDO = "COMPLETAMENTE_REALIZADA";
        Estado estadoRequerido = repoEstados.buscarEstado(NOMBRE_ESTADO_REQUERIDO);

        if (estadoRequerido == null || estadoRequerido.getId() == null) return List.of();

        List<OrdenDeInspeccion> ordenesIniciales = repoOrdenes
                .findByEmpleado_IdAndEstado_Id(empleado.getId(), estadoRequerido.getId());

        List<OrdenDeInspeccion> ordenesFiltradas = new ArrayList<>();

        for (OrdenDeInspeccion orden : ordenesIniciales) {
            if (orden.sosDeEmpleado(empleado) && orden.sosCompletamenteRealizada()) {
                ordenesFiltradas.add(orden);
            }
        }

        Collections.sort(ordenesFiltradas, new Comparator<OrdenDeInspeccion>() {
            @Override
            public int compare(OrdenDeInspeccion o1, OrdenDeInspeccion o2) {
                if (o1.getFechaHoraFinalizacion() == null && o2.getFechaHoraFinalizacion() == null) return 0;
                if (o1.getFechaHoraFinalizacion() == null) return 1;
                if (o2.getFechaHoraFinalizacion() == null) return -1;
                return o1.getFechaHoraFinalizacion().compareTo(o2.getFechaHoraFinalizacion());
            }
        });

        return ordenesFiltradas;
    }

    private Optional<OrdenDeInspeccion> buscarOrdenDeInspeccion(Long numeroOrden) {
        return Optional.ofNullable(repoOrdenes.buscarOrdenDeInspeccion(numeroOrden));
    }

    public List<MotivoTipo> buscarTiposMotivosFueraDeServicios() {

        return repoMotivos.findAll();
    }
}