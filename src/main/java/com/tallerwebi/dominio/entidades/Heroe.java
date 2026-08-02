package com.tallerwebi.dominio.entidades;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Heroe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer nivel;
    @Column(name="url_imagen")
    private String urlImagen;
    private int precio;
    @Column(name="max_vida", nullable=false)
    private int maxVida;
    @Column(name = "vida_actual")
    private Integer vidaActual;

    @Column(name="atk", nullable=false)
    private int atk;

    @Column(name="defensa_base", nullable=false)
    private int defensaBase;

    public Heroe(Long id, String nombre, int precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.nivel = 1;
    }

    public Heroe(Long id, String nombre,Integer nivel,Integer precio,String urlImagen) {
        this.id = id;
        this.nombre = nombre;
        this.urlImagen = urlImagen;
        this.nivel = nivel;
        this.precio = precio;
    }

    public Heroe(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Heroe(int precio) {

        this.precio = precio;
    }
    public Heroe() {}

/*
    public void mostrarHeroe() {
        System.out.print("Heroe\n");
        System.out.printf("ID: %d,   Nom: %s,   Niv: %d\n", getId(),getNombre(),getNivel());
    }
*/

    public int getMaxVida() {
        return maxVida;
    }

    public void setMaxVida(int maxVida) {
        this.maxVida = maxVida;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public int getDefensaBase() {
        return defensaBase;
    }

    public void setDefensaBase(int defensaBase) {
        this.defensaBase = defensaBase;
    }

    public Object getVida() {
        return this.maxVida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getVidaActual() {
        return vidaActual;
    }
    public void setVidaActual(Integer vidaActual) {
        this.vidaActual = vidaActual;
    }
}
