package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Optional;

import com.tallerwebi.dominio.entidades.*;
import com.tallerwebi.dominio.servicios.Impl.ServicioJuegoImpl;
import com.tallerwebi.dominio.servicios.ServicioRecluta;
import com.tallerwebi.infraestructura.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class ServicioJuegoImplTest {

    @Mock
    RepositorioUsuario usuarioRepo;
    @Mock
    RepositorioSession sessionRepo;
    @Mock
    RepositorioMonster monsterRepo;
    @Mock
    RepositorioSessionMonster smRepo;
    @Mock
    RepositorioHeroSession shRepo;
    @Mock
    RepositorioExpedition expeditionRepo;
    @Mock
    ServicioRecluta servicioRecluta;
    @Mock //
    RepositorioHeroe repositorioHeroe;
    @Mock
    RepositorioItem repositorioItem;

    @InjectMocks
    ServicioJuegoImpl servicio;

    private Usuario usuario;
    private GameSession session;
    private Expedition expedition;
    private Monster m1, m2, m3;
    private SessionMonster sm1, sm2, sm3;
    private SessionHero sh1, sh2;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1L);

        session = new GameSession();
        session.setUsuario(usuario);
        session.setNivel(1);

        expedition = new Expedition();
        expedition.setSession(session);
        expedition.setNumber(1);
        expedition.setCompleted(false);

        m1 = new Monster(); m1.setId(1L); m1.setNombre("Esqueleto"); m1.setVida(30); m1.setAtk(5);
        m2 = new Monster(); m2.setId(2L); m2.setNombre("Goblin");    m2.setVida(40); m2.setAtk(8);
        m3 = new Monster(); m3.setId(3L); m3.setNombre("Araña");     m3.setVida(20); m3.setAtk(3);

        sm1 = new SessionMonster(); sm1.setOrden(1); sm1.setVidaActual(30); sm1.setMonster(m1);
        sm2 = new SessionMonster(); sm2.setOrden(2); sm2.setVidaActual(40); sm2.setMonster(m2);
        sm3 = new SessionMonster(); sm3.setOrden(3); sm3.setVidaActual(20); sm3.setMonster(m3);

        Heroe baseHero = new Heroe();
        baseHero.setId(1L); baseHero.setNombre("HéroeTest"); baseHero.setMaxVida(100); baseHero.setAtk(10);
        sh1 = new SessionHero(); sh1.setOrden(1); sh1.setHero(baseHero); sh1.setVidaActual(100);
        sh2 = new SessionHero(); sh2.setOrden(2); sh2.setHero(baseHero); sh2.setVidaActual(100);

        servicio = new ServicioJuegoImpl(
                sessionRepo, smRepo, shRepo, monsterRepo,
                usuarioRepo, expeditionRepo, servicioRecluta, repositorioHeroe,repositorioItem
        );
    }

    @Test
    void iniciarPartida_nuevaSesion_siembraHeroesYMonstruos() {
        when(sessionRepo.findActive(usuario)).thenReturn(null);
        when(sessionRepo.startNew(usuario)).thenReturn(session);
        when(expeditionRepo.findBySessionAndCompletedFalse(session)).thenReturn(Optional.empty());

        // mockeo el save de Expedition para evitar el nullpointer exception
        when(expeditionRepo.save(any(Expedition.class))).thenAnswer(invocation -> {
            Expedition exp = invocation.getArgument(0);
            exp.setNumber(1);
            exp.setCompleted(false);
            return exp;
        });
        when(servicioRecluta.getHeroesEnCarruaje(usuario.getId())).thenReturn(List.of(sh1.getHero(), sh2.getHero()));
        when(shRepo.findBySession(session)).thenReturn(List.of());
        when(smRepo.findBySessionAndDungeonNumber(session, session.getNivel())).thenReturn(List.of());
        when(monsterRepo.obtenerTodosLosMonstruos()).thenReturn(List.of(m1, m2, m3));

        GameSession s = servicio.iniciarPartida(usuario);

        //verifico que siembra heroes y monster
        verify(shRepo, atLeastOnce()).add(eq(session), any(Heroe.class), anyInt());
        verify(smRepo, times(3)).add(eq(session), any(Monster.class), eq(1));
        assertThat(s, notNullValue());
    }

    @Test
    void atacar_heroeYMonstruoInexistente_devuelveMensajeMonstruoNoEncontrado() {
        when(sessionRepo.findActive(usuario)).thenReturn(session);
        when(expeditionRepo.findBySessionAndCompletedFalse(session)).thenReturn(Optional.of(expedition));
        when(shRepo.findBySession(session)).thenReturn(List.of(sh1));
        when(smRepo.findBySessionAndExpeditionNumber(session, expedition.getNumber()))
                .thenReturn(List.of(sm1, sm2));

        // orden que no existe (monster 99)
        String msg = servicio.atacar(usuario, 1, 99);

        assertThat(msg, containsString("Monstruo no encontrado."));
    }

    @Test
    void atacar_realizaDañoYContraataque() {
        when(sessionRepo.findActive(usuario)).thenReturn(session);
        when(expeditionRepo.findBySessionAndCompletedFalse(session)).thenReturn(Optional.of(expedition));

        // buscar heroes y monsters
        when(shRepo.findBySession(session)).thenReturn(List.of(sh1));
        when(smRepo.findBySessionAndDungeonNumber(session, session.getNivel())).thenReturn(List.of(sm1));
        when(smRepo.findBySessionAndExpeditionNumber(session, expedition.getNumber())).thenReturn(List.of(sm1));

        // when heroe 1 ataca a monster 1
        String msg = servicio.atacar(usuario, 1, 1);

        verify(smRepo).update(argThat(sm -> sm.getVidaActual() == 20));
        verify(shRepo).update(argThat(h -> h.getVidaActual() == 95));
        assertThat(msg, containsString("Has atacado"));
        assertThat(msg, containsString("Héroe"));
    }


    @Test
    void reiniciarMazmorra_borraYSemilla3Nuevos() {
        when(sessionRepo.findActive(usuario)).thenReturn(session);
        when(monsterRepo.obtenerTodosLosMonstruos()).thenReturn(List.of(m1, m2, m3));
        when(shRepo.findBySession(session)).thenReturn(List.of(sh1, sh2));
        when(expeditionRepo.findBySessionAndCompletedFalse(session)).thenReturn(Optional.of(expedition));

        when(smRepo.findBySessionAndDungeonNumber(eq(session), anyInt())).thenReturn(List.of());

        servicio.reiniciarMazmorra(usuario);

        verify(smRepo).deleteBySession(session);
        verify(smRepo, atLeast(3)).add(eq(session), any(Monster.class), eq(1));
    }


    @Test
    void terminarExpedicion_marcaComoCompletadaYRecompensa() {
        when(sessionRepo.findActive(usuario)).thenReturn(session);
        when(expeditionRepo.findBySessionAndCompletedFalse(session)).thenReturn(Optional.of(expedition));
        when(monsterRepo.obtenerTodosLosMonstruos()).thenReturn(List.of(m1, m2, m3));
        when(shRepo.findBySession(session)).thenReturn(List.of(sh1, sh2));
        when(expeditionRepo.save(any(Expedition.class))).thenAnswer(invocation -> {
            Expedition exp = invocation.getArgument(0);
            exp.setNumber(expedition.getNumber() + 1);
            exp.setCompleted(false);
            return exp;
        });

        usuario.setOro(0);
        servicio.terminarExpedicion(usuario);

        assertThat(usuario.getOro(), is(250));
        verify(usuarioRepo).modificar(usuario);
        verify(expeditionRepo).save(expedition);
        verify(smRepo, atLeast(1)).add(eq(session), any(Monster.class), eq(1));
    }
}
