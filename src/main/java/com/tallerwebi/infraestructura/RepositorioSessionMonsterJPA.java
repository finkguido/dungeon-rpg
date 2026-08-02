package com.tallerwebi.infraestructura;
import com.tallerwebi.dominio.entidades.SessionMonster;
import com.tallerwebi.dominio.entidades.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioSessionMonsterJPA
        extends JpaRepository<SessionMonster, Long> {

    // Busca por entidad GameSession y ordena
    List<SessionMonster> findBySessionOrderByOrden(GameSession session);

    // Borra todos los SessionMonster de una sesion
    void deleteBySession(GameSession session);
}