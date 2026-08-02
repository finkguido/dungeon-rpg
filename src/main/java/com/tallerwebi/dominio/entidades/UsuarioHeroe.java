package com.tallerwebi.dominio.entidades;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class UsuarioHeroe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Heroe heroe;

    private Integer nivel;

    public UsuarioHeroe(Usuario usuario, Heroe heroe) {
        this.usuario = usuario;
        this.heroe = heroe;
        this.nivel = heroe.getNivel();
    }

    public UsuarioHeroe() {

    }
}
