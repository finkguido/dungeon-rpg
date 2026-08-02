package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioGameSessionJPA
        extends JpaRepository<GameSession, Long> {

    GameSession findByActiveTrue();
}