package com.tallerwebi.dominio.entidades;

import com.tallerwebi.dominio.enums.TipoDeItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoDeItem tipo;
    private Integer precio;
    private String imagenUrl;


    @ManyToOne
    private Inventario inventario;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Item() {}

}
