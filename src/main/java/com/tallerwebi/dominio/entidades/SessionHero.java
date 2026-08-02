package com.tallerwebi.dominio.entidades;

import javax.persistence.*;

@Entity
@Table(name = "session_hero")
public class SessionHero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "hero_id", nullable = false)
    private Heroe hero;

    @Column(name = "vida_actual", nullable = false)
    private int vidaActual;

    @Column(nullable = false)
    private int orden;

    @Column(nullable = false)
    private boolean defending = false;

    @Column(nullable = false)
    private boolean espadaBuff = false; // true si el buff está activo

    @Column(name = "numero_expedicion_buff")
    private Integer numeroExpedicionBuff; // en qué expedición fue buffeado (null si no está activo)

    public SessionHero() {}

    // --- Getters & Setters ---

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

    public Heroe getHero() {
        return hero;
    }

    public void setHero(Heroe hero) {
        this.hero = hero;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public void setVidaActual(int vidaActual) {
        this.vidaActual = vidaActual;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public boolean isEspadaBuff() { return espadaBuff; }
    public void setEspadaBuff(boolean espadaBuff) { this.espadaBuff = espadaBuff; }

    public Integer getNumeroExpedicionBuff() { return numeroExpedicionBuff; }
    public void setNumeroExpedicionBuff(Integer numeroExpedicionBuff) { this.numeroExpedicionBuff = numeroExpedicionBuff; }

    // --- Logica de combate ---

    public int damageOutput(int expedicionActual) {
        int base = hero.getAtk();
        if (espadaBuff && numeroExpedicionBuff != null && numeroExpedicionBuff.intValue() == expedicionActual) {
            return (int) Math.round(base * 1.3); // +30%
        }

        return base;
    }


    public void takeDamage(int damage) {
        // defensa base que el héroe tiene
        int mitigated = hero.getDefensaBase();
        int dmg = Math.max(0, damage - mitigated);
        if (defending) {
            dmg /= 2;
            defending = false;
        }
        vidaActual = Math.max(0, vidaActual - dmg);
    }

    public void defend() {
        this.defending = true;
    }

    public void heal(int amount) {
        int max = hero.getMaxVida();
        vidaActual = Math.min(max, vidaActual + amount);
    }

    public boolean sigueBuffeado(int expedicionActual) {
        return espadaBuff && numeroExpedicionBuff != null && numeroExpedicionBuff.equals(expedicionActual);
    }


    public void quitarBuffEspada() {
        this.espadaBuff = false;
        this.numeroExpedicionBuff = null;
    }
}
