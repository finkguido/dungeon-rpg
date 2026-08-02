package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Usuario;
import org.hibernate.SessionFactory;

public interface RepositorioSession {

    GameSession startNew();
    GameSession findActive();

    GameSession startNew(Usuario u);
    GameSession findActive(Usuario u);

    void save(GameSession s);
    void delete(GameSession s);

    SessionFactory getSessionFactory();

    void update(GameSession session);
}