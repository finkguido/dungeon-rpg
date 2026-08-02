package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Monster;
import com.tallerwebi.dominio.entidades.SessionMonster;

import java.util.List;

public interface RepositorioMonster {

    List<Monster> obtenerTodosLosMonstruos();

    @SuppressWarnings("unchecked")
    List<SessionMonster> findBySessionAndExpeditionNumber(GameSession session, int expeditionNumber);

    Monster findById(Long monsterId);

}