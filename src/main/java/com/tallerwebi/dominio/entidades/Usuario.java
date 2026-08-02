package com.tallerwebi.dominio.entidades;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private int expedicionActual;
    private int mazmorraActual;
    private Boolean activo = false;
    @Column(nullable = false)
    private Integer oro = 1000;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> itemsComprados = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "inventario_id")
    Inventario inventario;

    public Usuario(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Usuario(){}


    public boolean activo() {
        return activo;
    }

    public void activar() {
        activo = true;
    }

    public Integer getOro() {
        return oro;
    }

    public void setOro(Integer oro) {
        this.oro = oro;
    }

    public boolean isActivo() {
        return Boolean.TRUE.equals(activo);
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getExpedicionActual() {
        return expedicionActual;
    }

    public void setExpedicionActual(int expedicionActual) {
        this.expedicionActual = expedicionActual;
    }

    public int getMazmorraActual() {
        return mazmorraActual;
    }

    public void setMazmorraActual(int mazmorraActual) {
        this.mazmorraActual = mazmorraActual;
    }


    public void setId(long l) { this.id = l;
    }

    public void setInventario(Inventario inventario) { this.inventario = inventario;
    }

    public Long getId() { return this.id;
    }

}
