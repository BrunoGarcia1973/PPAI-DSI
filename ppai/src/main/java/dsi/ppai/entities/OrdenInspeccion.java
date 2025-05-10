package dsi.ppai.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "ordenes_inspeccion")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrdenInspeccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_inspeccion")
    private Integer ordenId;

}

