package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoTipo; // Asegúrate de importar MotivoTipo
import dsi.ppai.entities.MotivoFueraServicio; // Asegúrate de importar MotivoFueraServicio
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
// @Data // No es necesario para un repositorio, @Data genera getters/setters para los atributos
// del repositorio mismo, no para los elementos que contiene.
public class RepositorioMotivoTipo {

    // Esta lista debe almacenar *todos* los MotivoTipo disponibles en el sistema.
    // Incluirá los definidos en DataInitializerService.
    private final List<MotivoTipo> motivosTipos = new ArrayList<>();

    // La lista de MotivoFueraServicio es más para instancias específicas de motivos aplicados,
    // quizás no necesite un repositorio de esto si solo se crean y no se buscan como "tipos".
    // Si tuvieras que buscar MotivoFueraServicio, podrías mantenerla.
    // Para la funcionalidad actual (obtener tipos para la UI), esta lista no es directamente relevante aquí.
    // private final List<MotivoFueraServicio> motivosFueraServicio = new ArrayList<>();


    // ELIMINAR O REVISAR: El constructor del repositorio NO debe inicializar datos de esta forma.
    // La inicialización debe hacerse en DataInitializerService.
    // Este constructor está creando MotivoTipo con solo una descripción, mientras tu DataInitializerService
    // crea MotivoTipo con ID, descripción y detalle. Esto causará inconsistencias.
    /*
    public RepositorioMotivoTipo() {
        // En un caso real, estos datos vendrían de la DB. Aquí los "hardcodeamos" para la simulación.
        MotivoTipo correcto = new MotivoTipo("Correcto"); // ESTO ES INCONSISTENTE CON TU DataInitializerService
        MotivoTipo incorrecto = new MotivoTipo("Incorrecto");

        insertar(correcto);
        insertar(incorrecto);

        // MotivoFueraServicio no deberían ser inicializados aquí como "tipos"
        // MotivoFueraServicio m1 = new MotivoFueraServicio("Sin novedades", correcto);
        // MotivoFueraServicio m2 = new MotivoFueraServicio("Fuga detectada", incorrecto);
        // MotivoFueraServicio m3 = new MotivoFueraServicio("Faltan datos", incorrecto);
        // motivosFueraServicio.add(m1);
        // motivosFueraServicio.add(m2);
        // motivosFueraServicio.add(m3);

        // La lógica de agregar MotivoFueraServicio a MotivoTipo.agregarMotivo()
        // es solo si MotivoTipo tiene una relación de composición con MotivoFueraServicio.
        // Si no es el caso, estas líneas pueden ser eliminadas.
        // correcto.agregarMotivo(m1);
        // incorrecto.agregarMotivo(m2);
        // incorrecto.agregarMotivo(m3);
    }
    */

    /**
     * Guarda un MotivoTipo en la lista en memoria.
     * Si ya existe un motivo con el mismo ID, lo actualiza.
     * Si no existe, lo añade a la lista.
     * @param motivo El objeto MotivoTipo a guardar.
     */
    public void guardar(MotivoTipo motivo) {
        // Busca si ya existe un motivo con el mismo ID
        motivosTipos.stream()
                .filter(mt -> mt.getId().equals(motivo.getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingMotivo -> {
                            // Si existe, lo actualiza (reemplaza en la misma posición)
                            int index = motivosTipos.indexOf(existingMotivo);
                            motivosTipos.set(index, motivo);
                            System.out.println("DEBUG (RepositorioMotivoTipo): MotivoTipo actualizado en memoria: " + motivo.getDescripcion());
                        },
                        () -> {
                            // Si no existe, lo añade a la lista
                            motivosTipos.add(motivo);
                            System.out.println("DEBUG (RepositorioMotivoTipo): Nuevo MotivoTipo guardado en memoria: " + motivo.getDescripcion());
                        }
                );
    }

    // Método para insertar un MotivoTipo (delegado a guardar)
    public void insertar(MotivoTipo motivo) {
        guardar(motivo);
    }

    /**
     * Retorna una lista con todos los objetos MotivoTipo almacenados.
     * Este es el método que necesita el GestorInspeccion para cargar las opciones de la UI.
     * @return Una lista de todos los MotivoTipo.
     */
    public List<MotivoTipo> findAll() { // <--- ¡Añadido/Renombrado a findAll()!
        return new ArrayList<>(motivosTipos); // Devuelve una copia para evitar modificaciones externas
    }

    /**
     * Busca un MotivoTipo por su ID.
     * @param id El ID del MotivoTipo a buscar.
     * @return El objeto MotivoTipo si se encuentra, o null si no existe.
     */
    public MotivoTipo buscarPorId(Integer id) {
        return motivosTipos.stream()
                .filter(mt -> mt.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca un MotivoTipo por su descripción.
     * @param descripcion La descripción del MotivoTipo a buscar.
     * @return El objeto MotivoTipo si se encuentra, o null si no existe.
     */
    public MotivoTipo buscarMotivoTipo(String descripcion) {
        return motivosTipos.stream()
                .filter(mt -> mt.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElse(null);
    }

    // Si tu aplicación necesita gestionar y buscar específicamente MotivoFueraServicio,
    // puedes mantener estos métodos, pero es crucial entender que no son "tipos".
    // public void insertar(MotivoFueraServicio motivoFS) {
    //     motivosFueraServicio.add(motivoFS);
    // }
    // public List<MotivoFueraServicio> getMotivosPorTipo(String tipoDescripcion) {
    //     return motivosFueraServicio.stream()
    //             .filter(m -> m.getMotivoTipo().getDescripcion().equalsIgnoreCase(tipoDescripcion))
    //             .collect(Collectors.toList());
    // }
}