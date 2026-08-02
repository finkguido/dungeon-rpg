package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.SessionHero;

import java.util.List;

public interface RepositorioHeroSession {
    void add(GameSession session, Heroe hero, int i);
    List<SessionHero> findBySession(GameSession session);
    void update(SessionHero sessionHero);
    void deleteBySession(GameSession session);
}
