package com.tallerwebi.infraestructura.impl;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioInventario;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private final JdbcTemplate jdbc;

    @Autowired
    public RepositorioUsuarioImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Autowired
    private RepositorioInventario repositorioInventario;

    @Override
    public Usuario buscarUsuario(String email, String password) {
        try {
            return jdbc.queryForObject(
                    "SELECT id, email, password, rol, activo, nombre, oro, inventario_id " +
                            "FROM Usuario WHERE email = ? AND password = ?",
                    this::mapRowToUsuario,
                    email, password
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void guardar(Usuario u) {
        jdbc.update(
                "INSERT INTO Usuario " +
                        "(email, password, rol, activo, nombre, oro, expedicionActual, mazmorraActual, inventario_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                u.getEmail(),
                u.getPassword(),
                u.getRol(),
                u.getActivo(),
                u.getNombre(),
                u.getOro(),
                1,
                1,
                u.getInventario() != null ? u.getInventario().getId() : null
        );
        Long newId = jdbc.queryForObject("CALL IDENTITY()", Long.class);
        u.setId(newId);
    }

    @Override
    public Usuario buscar(String email) {
        try {
            return jdbc.queryForObject(
                    "SELECT id, email, password, rol, activo, nombre, oro, inventario_id " +
                            "FROM Usuario WHERE email = ?",
                    this::mapRowToUsuario,
                    email
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public void modificar(Usuario u) {
        jdbc.update(
                "UPDATE Usuario SET " +
                        " email        = ?, " +
                        " password     = ?, " +
                        " rol          = ?, " +
                        " activo       = ?, " +
                        " nombre       = ?, " +
                        " oro          = ?, " +
                        " inventario_id= ?  " +
                        "WHERE id = ?",
                u.getEmail(),
                u.getPassword(),
                u.getRol(),
                u.getActivo(),
                u.getNombre(),
                u.getOro(),
                u.getInventario() != null ? u.getInventario().getId() : null,
                u.getId()
        );
    }

    @Override
    public Usuario buscarUsuarioPorId(Long id) {
        try {
            return jdbc.queryForObject(
                    "SELECT id, email, password, rol, activo, nombre, oro, inventario_id FROM Usuario WHERE id = ?",
                    this::mapRowToUsuario,
                    id
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Usuario> obtenerRankingJugadores() {
        String sql =
                "SELECT u.*, gs.nivel as mazmorra_actual, e.number as expedicion_actual " +
                        "FROM Usuario u " +
                        "JOIN game_session gs ON u.id = gs.usuario_id " +
                        "JOIN expedition e ON gs.id = e.session_id " +
                        "WHERE gs.active = TRUE AND e.completed = FALSE " +
                        "ORDER BY e.number DESC, gs.nivel DESC, u.oro DESC";
        return jdbc.query(sql, (rs, rowNum) -> {
            Usuario u = mapRowToUsuario(rs, rowNum);
            u.setExpedicionActual(rs.getInt("expedicion_actual"));
            u.setMazmorraActual(rs.getInt("mazmorra_actual"));
            return u;
        });
    }

    // Mapea un ResultSet a la entidad Usuario.
    // convierte cada fila del ResultSet (el resultado de la consulta SQL)
    // en una instancia de entidad de dominio Usuario.


    private Usuario mapRowToUsuario(ResultSet rs, int rowNum) throws SQLException {
        Usuario u = new Usuario();
        u.setId    (rs.getLong("id"));
        u.setEmail (rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRol   (rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        u.setNombre(rs.getString("nombre"));
        u.setOro   (rs.getInt("oro"));

        Long inventarioId = rs.getLong("inventario_id");
        if (!rs.wasNull()) {
            u.setInventario(repositorioInventario.buscarPorId(inventarioId));
        }

        return u;
    }
}

