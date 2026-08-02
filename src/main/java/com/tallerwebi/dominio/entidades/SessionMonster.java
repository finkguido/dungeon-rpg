package com.tallerwebi.dominio.entidades;

import javax.persistence.*;

@Entity
@Table(name = "session_monster")
public class SessionMonster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion a la sesion de juego
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    // Plantilla de monstruo
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "monster_id", nullable = false)
    private Monster monster;

    @Column(name = "vida_actual", nullable = false)
    private int vidaActual;

    // Para saber en que expedicion y mazmorra esta este monstruo:
    @Column(name = "expedition_number", nullable = false)
    private int expeditionNumber;

    @Column(name = "dungeon_number", nullable = false)
    private int dungeonNumber;

    @Column(nullable = false)
    private int orden;

    public SessionMonster() {}

    // --- getters y setters ---

    public Long getId() {
        return id;
    }

    public GameSession getSession() {
        return session;
    }
    public void setSession(GameSession session) {
        this.session = session;
    }

    public Monster getMonster() {
        return monster;
    }
    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public int getVidaActual() {
        return vidaActual;
    }
    public void setVidaActual(int vidaActual) {
        this.vidaActual = vidaActual;
    }

    public int getExpeditionNumber() {
        return expeditionNumber;
    }
    public void setExpeditionNumber(int expeditionNumber) {
        this.expeditionNumber = expeditionNumber;
    }

    public int getDungeonNumber() {
        return dungeonNumber;
    }
    public void setDungeonNumber(int dungeonNumber) {
        this.dungeonNumber = dungeonNumber;
    }

    public int getOrden() {
        return orden;
    }
    public void setOrden(int orden) {
        this.orden = orden;
    }

    // Delegados a Monster
    public String getNombre() {
        return monster.getNombre();
    }
    public String getImagen() {
        return monster.getImagen();
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void takeDamage(int damage) {
        this.vidaActual = Math.max(0, this.vidaActual - damage);
    }

    public int getMaxVida() {
        return monster.getVida();
    }
}
