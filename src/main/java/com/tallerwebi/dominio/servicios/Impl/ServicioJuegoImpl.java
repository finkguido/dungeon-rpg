package com.tallerwebi.dominio.servicios.Impl;

import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import com.tallerwebi.infraestructura.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicioJuegoImpl implements com.tallerwebi.dominio.servicios.ServicioJuego {

    private final RepositorioSession sessionRepo;
    private final RepositorioSessionMonster smRepo;
    private final RepositorioHeroSession shRepo;
    private final RepositorioMonster monsterRepo;
    private final RepositorioUsuario usuarioRepo;
    private final RepositorioExpedition expeditionRepo;
    private final ServicioRecluta servicioRecluta;
    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioItem repositorioItem;

    @Autowired
    public ServicioJuegoImpl(
            RepositorioSession sessionRepo,
            RepositorioSessionMonster smRepo,
            RepositorioHeroSession shRepo,
            RepositorioMonster monsterRepo,
            RepositorioUsuario usuarioRepo,
            RepositorioExpedition expeditionRepo,
            ServicioRecluta servicioRecluta, RepositorioHeroe repositorioHeroe, RepositorioItem repositorioItem
    ) {
        this.sessionRepo     = sessionRepo;
        this.smRepo          = smRepo;
        this.shRepo          = shRepo;
        this.monsterRepo     = monsterRepo;
        this.usuarioRepo     = usuarioRepo;
        this.expeditionRepo  = expeditionRepo;
        this.servicioRecluta = servicioRecluta;
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioItem = repositorioItem;
    }

    @Override
    public GameSession iniciarPartida(Usuario u) {
        if (u == null) throw new IllegalArgumentException("Usuario nulo");

        GameSession session = sessionRepo.findActive(u);
        if (session == null) {
            session = sessionRepo.startNew(u);
            session.setNivel(1);
            sessionRepo.update(session);
        }

        GameSession finalSession = session;
        GameSession finalSession1 = session;
        Expedition exp = expeditionRepo
                .findBySessionAndCompletedFalse(session)
                .orElseGet(() -> crearExpedicion(finalSession, finalSession1.getNivel()));

        // Sembrar héroes si no existen (mantienen su vida entre sesiones/mazmorras)
        if (shRepo.findBySession(session).isEmpty()) {
            seedHeroes(session);
        }

        // Sembrar monstruos si no existen para esta mazmorra
        if (smRepo.findBySessionAndDungeonNumber(session, session.getNivel()).isEmpty()) {
            seedMonsters(session, session.getNivel(), exp.getNumber());
        }

        return session;
    }

    @Override
    public void siguienteMazmorra(Usuario u) {
        GameSession session = sessionRepo.findActive(u);
        if (session == null) {
            session = sessionRepo.startNew(u);
            session.setNivel(1);
            sessionRepo.update(session);
        }

        int nivelAnterior = session.getNivel();
        if (nivelAnterior < 3) {
            int nuevoNivel = nivelAnterior + 1;
            session.setNivel(nuevoNivel);
            sessionRepo.update(session);

            // Borrar monstruos de la mazmorra anterior y la nueva (por seguridad)
            smRepo.deleteBySessionAndDungeonNumber(session, nivelAnterior);
            smRepo.deleteBySessionAndDungeonNumber(session, nuevoNivel);

            // Obtener número de expedición actual
            Expedition exp = expeditionRepo
                    .findBySessionAndCompletedFalse(session)
                    .orElseThrow(() -> new IllegalStateException("No hay expedición activa"));

            // Poblar con monstruos normales o buffeados según expedición
            seedMonsters(session, nuevoNivel, exp.getNumber());

        } else {
            throw new IllegalStateException("Usá terminarExpedicion() en mazmorra 3");
        }
    }

    @Override
    public GameSession crearNuevaMazmorra(Usuario u) {
        GameSession session = iniciarPartida(u);

        // Limpia héroes y monstruos
        smRepo.deleteBySession(session);
        shRepo.deleteBySession(session);

        // Nueva expedición (forzada) a mazmorra 1, poblar héroes y monstruos
        seedHeroes(session);
        seedMonsters(session, 1, 1);

        return session;
    }

    @Override
    public void reiniciarMazmorra(Usuario u) {
        GameSession session = iniciarPartida(u);
        smRepo.deleteBySession(session);
        shRepo.deleteBySession(session);
        seedHeroes(session);
        seedMonsters(session, 1, 1);
    }

    @Override
    public List<SessionMonster> getMonstruos(Usuario u) {
        GameSession session = iniciarPartida(u);
        // Devuelve solo los monstruos de la mazmorra actual
        return smRepo.findBySessionAndDungeonNumber(session, session.getNivel());
    }

    @Override
    public List<SessionHero> getHeroesDeSesion(Usuario u) {
        GameSession session = iniciarPartida(u);
        return shRepo.findBySession(session);
    }

    @Override
    public String atacar(Usuario u, int heroOrden, int monsterOrden) {
        GameSession session = iniciarPartida(u);

        // 1) Localiza héroe y monstruo
        SessionHero sh = getHeroesDeSesion(u).stream()
                .filter(h -> h.getOrden() == heroOrden)
                .findFirst().orElse(null);
        SessionMonster sm = getMonstruos(u).stream()
                .filter(m -> m.getOrden() == monsterOrden)
                .findFirst().orElse(null);

        if (sh == null) return "Héroe no encontrado.";
        if (sm == null) return "Monstruo no encontrado.";

        // Buscar la expedición activa para el buff
        Expedition expedicionActual = expeditionRepo
                .findBySessionAndCompletedFalse(session)
                .orElse(null);
        int numeroExpedicion = (expedicionActual != null) ? expedicionActual.getNumber() : 1;

        // Héroe golpea con daño calculado
        sm.takeDamage(sh.damageOutput(numeroExpedicion));
        smRepo.update(sm);

        // 3) Monstruos vivos contraatacan
        List<SessionMonster> vivos = getMonstruos(u).stream()
                .filter(m -> m.getVidaActual() > 0)
                .collect(Collectors.toList());
        for (SessionMonster m : vivos) {
            sh.takeDamage(m.getMonster().getAtk());
            shRepo.update(sh);
        }

        // 4) Arma el mensaje final
        String atacantes = vivos.isEmpty()
                ? "Nadie"
                : vivos.stream()
                .map(x -> x.getMonster().getNombre())
                .collect(Collectors.joining(", "));
        return String.format(
                "Has atacado. Contraatacan: %s. Tu héroe %s ahora tiene %d/%d HP.",
                atacantes,
                sh.getHero().getNombre(),
                sh.getVidaActual(),
                sh.getHero().getMaxVida()
        );
    }

    @Override
    public String defender(Usuario u, int heroOrden) {
        GameSession session = iniciarPartida(u);
        SessionHero sh = getHeroesDeSesion(u).stream()
                .filter(h -> h.getOrden() == heroOrden)
                .findFirst().orElse(null);
        if (sh == null) return "Héroe no encontrado.";
        sh.defend();
        shRepo.update(sh);
        return "Tu héroe " + sh.getHero().getNombre() + " se defiende.";
    }

    @Override
    public String usarPocion(Usuario u, int heroOrden) {

        Usuario usuarioHibernate = usuarioRepo.buscarUsuarioPorId(u.getId());
        if (usuarioHibernate == null) return "Usuario no encontrado.";

        GameSession session = iniciarPartida(usuarioHibernate);
        SessionHero sh = getHeroesDeSesion(usuarioHibernate).stream()
                .filter(h -> h.getOrden() == heroOrden)
                .findFirst().orElse(null);
        if (sh == null) return "Héroe no encontrado.";

        Inventario inventario = usuarioHibernate.getInventario();
        List<Item> items = inventario.getItems();

        Item pocion = items.stream()
                .filter(i -> i.getNombre().equals("Poción de Vida"))
                .findFirst().orElse(null);

        if (pocion == null) return "No tienes pociones de vida en el inventario.";

        Item pocionHibernate = repositorioItem.buscarPorId(pocion.getId());

        sh.heal(80);
        shRepo.update(sh);

        items.remove(pocion);

        repositorioItem.eliminarItem(pocionHibernate);

        return String.format(
                "Tu héroe %s recupera vida: ahora %d/%d.",
                sh.getHero().getNombre(),
                sh.getVidaActual(),
                sh.getHero().getMaxVida()
        );
    }


    @Override
    public Expedition getExpedicionActiva(Usuario u) {
        GameSession s = iniciarPartida(u);
        return expeditionRepo
                .findBySessionAndCompletedFalse(s)
                .orElseThrow(() -> new IllegalStateException("No hay expedición activa"));
    }

    @Override
    public void endSession(GameSession session) {
        if (session != null) {
            smRepo.deleteBySession(session);
            shRepo.deleteBySession(session);
            sessionRepo.delete(session);
        }
    }

    @Override
    public void terminarExpedicion(Usuario u) {
        GameSession session = sessionRepo.findActive(u);
        if (session == null) throw new IllegalStateException("No hay sesión activa");

        Expedition exp = expeditionRepo
                .findBySessionAndCompletedFalse(session)
                .orElseThrow(() -> new IllegalStateException("No hay expedición activa"));

        List<SessionHero> sessionHeroes = shRepo.findBySession(session);
        for (SessionHero sh : sessionHeroes) {
            Heroe h = sh.getHero();
            h.setVidaActual(sh.getVidaActual());
            repositorioHeroe.modificar(h);
            // Remover buff espada al finalizar expedición
            sh.setEspadaBuff(false);
            sh.setNumeroExpedicionBuff(null);
            shRepo.update(sh);
        }

        int oldNumber = exp.getNumber();
        exp.setCompleted(true);
        expeditionRepo.save(exp);

        // Recompensa
        u.setOro(u.getOro() + 250);
        usuarioRepo.modificar(u);

        // Nueva expedición N+1
        Expedition next = crearExpedicion(session, oldNumber + 1);

        // Reset mazmorra a 1 y poblar con monstruos buffeados si corresponde
        session.setNivel(1);
        sessionRepo.update(session);

        // Limpia cualquier monstruo previo del nivel 1
        smRepo.deleteBySessionAndDungeonNumber(session, 1);

        // Poblar monstruos de nivel 1, usando buff si corresponde
        seedMonsters(session, 1, next.getNumber());
    }

    // ----------- Métodos auxiliares -----------

    private Expedition crearExpedicion(GameSession session, int numero) {
        Expedition e = new Expedition();
        e.setSession(session);
        e.setNumber(numero);
        e.setCompleted(false);
        return expeditionRepo.save(e);
    }

    private void seedMonsters(GameSession session, int dungeonNumber, int expedicionNumber) {
        if (expedicionNumber > 1) {
            seedMonstersBuffed(session, expedicionNumber, dungeonNumber);
        } else {
            List<Monster> all = new ArrayList<>(monsterRepo.obtenerTodosLosMonstruos());
            Collections.shuffle(all);
            all.stream().limit(3)
                    .forEach(m -> smRepo.add(session, m, dungeonNumber));
        }
    }

    private void seedHeroes(GameSession session) {
        List<Heroe> heroes = servicioRecluta.getHeroesEnCarruaje(session.getUsuario().getId());
        int orden = 1;
        for (Heroe h : heroes) {
            shRepo.add(session, h, orden++);
        }
    }

    private void seedMonstersBuffed(GameSession session, int expeditionNumber, int dungeonNumber) {
        // factor de buff: +10% por cada expedición pasada
        double factor = 1.0 + (expeditionNumber - 1) * 0.10;

        List<Monster> todos = new ArrayList<>(monsterRepo.obtenerTodosLosMonstruos());
        Collections.shuffle(todos);

        int orden = 1;
        for (Monster base : todos.subList(0, 3)) {
            Monster buffed = new Monster();
            buffed.setId     (base.getId());
            buffed.setNombre (base.getNombre());
            buffed.setImagen (base.getImagen());
            buffed.setVida   ((int) Math.round(base.getVida() * factor));
            buffed.setAtk    ((int) Math.round(base.getAtk()  * factor));
            smRepo.add(session, buffed, dungeonNumber);
            orden++;
        }
    }

    public List<Item> getItemsDeUsuario(Usuario usuario) {
        if (usuario.getInventario() == null) {

            return Collections.emptyList();
        }
        return usuario.getInventario().getItems();
    }

    @Override
    public boolean tieneSesionActiva(Usuario u) {
        return sessionRepo.findActive(u) != null;
    }

    @Override
    public String usarEspada(Usuario u, int heroOrden) {
        // RECARGA desde Hibernate, igual que usarPocion:
        Usuario usuarioHibernate = usuarioRepo.buscarUsuarioPorId(u.getId());
        if (usuarioHibernate == null) return "Usuario no encontrado.";

        GameSession session = iniciarPartida(usuarioHibernate);

        // Buscar la expedición activa:
        Expedition expedicionActual = expeditionRepo
                .findBySessionAndCompletedFalse(session)
                .orElse(null);

        if (expedicionActual == null) return "No hay expedición activa.";

        int numeroExpedicion = expedicionActual.getNumber();

        // Buscá el héroe correspondiente:
        SessionHero sh = getHeroesDeSesion(usuarioHibernate).stream()
                .filter(h -> h.getOrden() == heroOrden)
                .findFirst().orElse(null);

        if (sh == null) return "Héroe no encontrado.";

        // Buscá una espada en el inventario:
        Inventario inventario = usuarioHibernate.getInventario();
        List<Item> items = inventario.getItems();

        Item espada = items.stream()
                .filter(i -> i.getNombre().equals("Espada Corta"))
                .findFirst().orElse(null);

        if (espada == null) return "No tienes espadas cortas en el inventario.";

        // Recargá la espada con Hibernate para evitar conflictos
        Item espadaHibernate = repositorioItem.buscarPorId(espada.getId());

        // Activar el buff solo si no está ya activo en esta expedición
        if (sh.isEspadaBuff() && sh.getNumeroExpedicionBuff() != null && sh.getNumeroExpedicionBuff() == numeroExpedicion) {
            return "Este héroe ya tiene el buff de espada en esta expedición.";
        }

        sh.setEspadaBuff(true);
        sh.setNumeroExpedicionBuff(numeroExpedicion);
        shRepo.update(sh);

        items.remove(espada);
        repositorioItem.eliminarItem(espadaHibernate);

        return String.format("¡%s ahora inflige un 30%% más de daño hasta terminar la expedición!", sh.getHero().getNombre());
    }


    @Override
    public GameSession getSession() {
        throw new UnsupportedOperationException("Use getSession(Usuario) en su lugar");
    }

    @Override
    public Usuario getUsuario() {
        throw new UnsupportedOperationException("Use getUsuario(Usuario) en su lugar");
    }

    @Override
    public GameSession getSession(Usuario u) {
        return iniciarPartida(u);
    }

    @Override
    public Usuario getUsuario(Usuario u) {
        return iniciarPartida(u).getUsuario();
    }
}
