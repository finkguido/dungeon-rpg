package com.tallerwebi.dominio.servicios;

import com.tallerwebi.dominio.entidades.*;

import java.util.List;

public interface ServicioJuego {

    GameSession iniciarPartida(Usuario u);
    GameSession crearNuevaMazmorra(Usuario u);
    void reiniciarMazmorra(Usuario u);
    GameSession getSession(Usuario u);
    Usuario getUsuario(Usuario u);

    GameSession getSession();

    Usuario getUsuario();

    List<SessionMonster> getMonstruos(Usuario u);
    List<SessionHero>    getHeroesDeSesion(Usuario u);
    List<Item>           getItemsDeUsuario(Usuario usuario);
    String atacar(Usuario u, int heroOrden, int monsterOrden);
    String defender(Usuario u, int heroOrden);
    String usarPocion(Usuario u, int heroOrden);
    void siguienteMazmorra(Usuario u);
    void endSession(GameSession current);


    Expedition getExpedicionActiva(Usuario usuario);

    void terminarExpedicion(Usuario u);

    boolean tieneSesionActiva(Usuario usuario);

    String usarEspada(Usuario u, int heroOrden);
}