package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Expedition;
import com.tallerwebi.dominio.entidades.GameSession;

import java.util.Optional;

public interface RepositorioExpedition {
    Expedition save(Expedition expedition);
    Optional<Expedition> findBySessionAndCompletedFalse(GameSession session);
}