package dsi.ppai.repositories;

import dsi.ppai.entities.MotivoFueraServicio;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RepositorioMotivosFueraServicio {

    private final Map<String, MotivoFueraServicio> motivosFueraServicio = new HashMap<>();


}
