package dsi.ppai.repositories;



import dsi.ppai.entities.OrdenDeInspeccion;

import org.springframework.stereotype.Repository; // Asegúrate de que esta importación sea correcta



import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.Optional; // Importación necesaria para Optional

import java.util.stream.Collectors;



@Repository // Anotación de Spring para indicar que es un componente de repositorio

public class RepositorioOrdenes {



// Cambiado a Map para un acceso más eficiente por numOrden, como ya lo tienes

    private final Map<Long, OrdenDeInspeccion> ordenes = new HashMap<>();



    /**

     * Guarda una OrdenDeInspeccion. Si la orden ya existe (por numOrden), la actualiza.

     * Si no existe, la añade.

     * Es una forma más robusta que solo 'insertar'.

     * @param orden La OrdenDeInspeccion a guardar.

     */

    public void guardar(OrdenDeInspeccion orden) {

        ordenes.put(orden.getNumOrden(), orden);

        System.out.println("DEBUG (RepositorioOrdenes): Orden " + orden.getNumOrden() + " guardada/actualizada en memoria.");

    }



    /**

     * Inserta una nueva OrdenDeInspeccion. Es equivalente a 'guardar' en esta implementación de Map.

     * Mantenido por compatibilidad con llamadas existentes.

     * @param orden La OrdenDeInspeccion a insertar.

     */

    public void insertar(OrdenDeInspeccion orden) {

        guardar(orden); // Delega al método guardar para centralizar la lógica

    }



    /**

     * Busca una OrdenDeInspeccion por su número de orden.

     * @param numeroOrden El número de orden a buscar.

     * @return La OrdenDeInspeccion encontrada, o null si no existe.

     */

    public OrdenDeInspeccion buscarOrdenDeInspeccion(Long numeroOrden) {

        return ordenes.get(numeroOrden);

    }



    /**

     * Busca todas las Órdenes de Inspección asociadas a un Empleado Responsable de Inspección (RI).

     * @param legajoEmpleado El legajo del empleado RI.

     * @return Una lista de Órdenes de Inspección.

     */

    public List<OrdenDeInspeccion> buscarOrdenesInspeccionDeRI(String legajoEmpleado) {

        return ordenes.values().stream()

                .filter(o -> o.getEmpleado() != null && o.getEmpleado().getLegajo().equals(legajoEmpleado))

                .collect(Collectors.toList());

    }



    /**

     * Retorna una lista con todas las Órdenes de Inspección almacenadas en el repositorio.

     * Se devuelve una nueva ArrayList para evitar modificaciones directas de la colección interna del Map.

     * @return Una lista de todas las Órdenes de Inspección.

     */

    public List<OrdenDeInspeccion> findAll() {

        return new ArrayList<>(ordenes.values());

    }

}