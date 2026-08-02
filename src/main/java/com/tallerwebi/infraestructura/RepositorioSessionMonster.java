package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.SessionMonster;
import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Monster;

import java.util.List;

public interface RepositorioSessionMonster {
    void add(GameSession s, Monster m);
    void add(GameSession session, Monster monster, int dungeonNumber);
    List<SessionMonster> findBySession(GameSession session);
    void update(SessionMonster sm);
    void deleteBySession(GameSession session);

    void deleteBySessionAndDungeonNumber(GameSession session, int dungeonNumber);

    List<SessionMonster> findBySessionAndExpeditionNumber(GameSession session, int expeditionNumber);
    List<SessionMonster> findBySessionAndDungeonNumber(GameSession session, int dungeonNumber);

    void deleteBySessionAndExpeditionNumber(GameSession session, int i);

}