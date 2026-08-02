package com.tallerwebi.dominio.entidades;

import javax.persistence.*;

import com.tallerwebi.dominio.entidades.Usuario;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_session")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private int turno = 1;

    @Column(nullable = false)
    private int nivel = 1;

    @OneToMany(mappedBy="session",
            cascade=CascadeType.ALL,
            orphanRemoval=true)
    private List<Expedition> expeditions = new ArrayList<>();

    @Column(name="active", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active = true;

    @Column(name="finished", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean finished = false;

    @CreationTimestamp
    @Column(name="started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @UpdateTimestamp
    @Column(name="ended_at")
    private LocalDateTime endedAt;

    public GameSession() {}

    // === getters & setters ===

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getTurno() {
        return this.turno;
    }
    public void setTurno(int turno) {
        this.turno = turno;
    }

    public int getNivel() { return this.nivel; }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }
    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
