package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.GameSession;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioSession;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;

@Repository
public class RepositorioSessionImpl implements RepositorioSession {

    private final JdbcTemplate jdbc;
    private final RepositorioUsuario usuarioRepo;
    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioSessionImpl(
            JdbcTemplate jdbc,
            RepositorioUsuario usuarioRepo, SessionFactory sessionFactory) {
        this.jdbc = jdbc;
        this.usuarioRepo = usuarioRepo;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GameSession startNew() {
        Usuario u = usuarioRepo.buscarUsuarioPorId(1L);
        if (u == null) throw new IllegalStateException("Usuario 1 no encontrado");
        return startNew(u);
    }

    @Override
    public GameSession findActive() {
        Usuario u = usuarioRepo.buscarUsuarioPorId(1L);
        if (u == null) return null;
        return findActive(u);
    }

    /**
     * Inicia una nueva sesión para el usuario dado o retorna la activa si existe.
     */
    @Override
    public GameSession startNew(Usuario u) {
        // Asegurar que el usuario exista en BD
        Usuario existente = usuarioRepo.buscarUsuarioPorId(u.getId());
        if (existente == null) {
            usuarioRepo.guardar(u);
        } else {
            u = existente;
        }
        // Crear sesión
        jdbc.update(
                "INSERT INTO game_session (usuario_id, turno, nivel, active, finished, started_at) " +
                        "VALUES (?, 1, 1, 1, 0, CURRENT_TIMESTAMP)",
                u.getId()
        );
        Long newId = jdbc.queryForObject("CALL IDENTITY()", Long.class);
        GameSession s = new GameSession();
        s.setId(newId);
        s.setUsuario(u);
        s.setTurno(1);
        s.setNivel(1);
        s.setActive(true);
        s.setFinished(false);
        return s;
    }

    /**
     * Busca la sesión activa del usuario dado, o null.
     */
    @Override
    public GameSession findActive(Usuario u) {
        try {
            return jdbc.queryForObject(
                    "SELECT id, usuario_id, turno, nivel, active, finished " +
                            "FROM game_session WHERE usuario_id = ? AND active = TRUE",
                    (ResultSet rs, int rn) -> {
                        GameSession s = new GameSession();
                        s.setId(rs.getLong("id"));
                        s.setUsuario(u);
                        s.setTurno(rs.getInt("turno"));
                        s.setNivel(rs.getInt("nivel"));
                        s.setActive(rs.getInt("active") == 1);
                        s.setFinished(rs.getInt("finished") == 1);
                        return s;
                    },
                    u.getId()
            );
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void save(GameSession s) {
        jdbc.update(
                "UPDATE game_session SET turno = ?, nivel = ?, active = ?, finished = ?, ended_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?",
                s.getTurno(),
                s.getNivel(),
                s.isActive()   ? 1 : 0,
                s.isFinished() ? 1 : 0,
                s.getId()
        );
    }

    @Override
    public void update(GameSession s) {
        jdbc.update(
                "UPDATE game_session " +
                        "   SET turno = ?, nivel = ?, active = ?, finished = ?, ended_at = CURRENT_TIMESTAMP " +
                        " WHERE id = ?",
                s.getTurno(),
                s.getNivel(),
                s.isActive()   ? 1 : 0,
                s.isFinished() ? 1 : 0,
                s.getId()
        );
    }

    @Override
    public void delete(GameSession s) {
        jdbc.update("DELETE FROM session_monster WHERE session_id = ?", s.getId());
        jdbc.update("DELETE FROM session_hero    WHERE session_id = ?", s.getId());
        jdbc.update("DELETE FROM game_session    WHERE id = ?", s.getId());
    }

    @Override
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
}
