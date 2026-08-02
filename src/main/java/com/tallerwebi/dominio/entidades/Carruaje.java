package com.tallerwebi.dominio.entidades;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Carruaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer nivel;
    private Integer semana;
    private Integer cantidadDeHeroesSemanales;
    //private Integer cantHeroesDisponibles;Depende de la tabla intermedia carruaje_heroe

    @OneToOne
    private Usuario usuario;


    public Carruaje() {
        this.nivel = 0;
        this.semana = 0;
        this.cantidadDeHeroesSemanales = 2;
    }

    public Carruaje(Integer nivel,Integer semana,Integer cantidadDeHeroesSemanales) {
        this.nivel = nivel;
        this.semana = semana;
        this.cantidadDeHeroesSemanales = cantidadDeHeroesSemanales;
    }

    public Carruaje(Long id,Integer nivel,Integer semana,Integer cantidadDeHeroesSemanales) {
        this.id = id;
        this.nivel = nivel;
        this.semana = semana;
        this.cantidadDeHeroesSemanales = cantidadDeHeroesSemanales;
    }


    public void aumentarNivel() {
        this.nivel++;
    }

    public Integer getCantidadDeHeroesSemanales() {
        return this.cantidadDeHeroesSemanales + this.nivel;
    }


    public void pasaUnaSemana() {
        this.semana++;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
