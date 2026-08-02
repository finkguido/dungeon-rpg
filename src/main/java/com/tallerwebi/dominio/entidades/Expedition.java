package com.tallerwebi.dominio.entidades;

import javax.persistence.*;

@Entity
@Table(name="expedition")
public class Expedition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="session_id", nullable=false,
            foreignKey=@ForeignKey(name="fk_expedition_session"))
    private GameSession session;

    @Column(nullable=false)
    private int number;           // 1, 2 o 3

    @Column(nullable=false)
    private boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameSession getSession() {
        return session;
    }

    public void setSession(GameSession session) {
        this.session = session;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}