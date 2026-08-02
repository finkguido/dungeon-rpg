package com.tallerwebi.infraestructura;

import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        HibernateConfig.class
})
public class RepositorioCarruajeTest {

    private Carruaje carruajeMock1;
    private Carruaje carruajeMock2;
    private Usuario usuarioMock1;
    private Usuario usuarioMock2;

    @Autowired
    private RepositorioCarruaje repositorioCarruaje;

    @Autowired
    private RepositorioUsuario repositorioUsuario;


    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        usuarioMock1 = new Usuario(1L,"u1");
        usuarioMock2 = new Usuario(2L,"u2");
        carruajeMock1 = new Carruaje(1L,0,0,2);
        carruajeMock2 = new Carruaje(2L,0,0,2);
    }

    @Test
    @Transactional
    @Rollback
    public void queSeGenereIdAutomaticamenteAlGuardarCarruaje() {
        Carruaje nuevoCarruaje = new Carruaje();
        nuevoCarruaje.setNivel(0);
        nuevoCarruaje.setSemana(0);
        nuevoCarruaje.setCantidadDeHeroesSemanales(2);

        repositorioCarruaje.guardar(nuevoCarruaje);

        assertThat(nuevoCarruaje.getId(), notNullValue());
        assertThat(nuevoCarruaje.getId(), greaterThan(0L));
    }

//    @Test
//    @Transactional
//    @Rollback
//    public void queNoSePuedaGuardarCarruajeConIdExistente() {
//        repositorioCarruaje.guardar(carruajeMock1); // Guarda el primer carruaje
//
//        Carruaje nuevoCarruaje = new Carruaje();
//        nuevoCarruaje.setId(carruajeMock1.getId()); // Usa el mismo id
//        nuevoCarruaje.setNivel(1);
//        nuevoCarruaje.setSemana(1);
//        nuevoCarruaje.setCantidadDeHeroesSemanales(3);
//        repositorioCarruaje.guardar(nuevoCarruaje);
//
//        List<Carruaje> lc = repositorioCarruaje.getListaDeCarruajes();
//
//        assertThat(lc.isEmpty(), equalTo(false));
//        System.out.println(lc.get(0).getId());
//        System.out.println(lc.get(1).getId());
//
//        assertThat(lc.size(), equalTo(1));
//        assertThat(lc.get(0).getId(), notNullValue());
//
//    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnCarruajePorId() {

        repositorioCarruaje.guardar(carruajeMock1);
        repositorioCarruaje.guardar(carruajeMock2);

        Carruaje carruaje = repositorioCarruaje.buscarCarruajePorId(carruajeMock2.getId());

        assertThat(carruaje,notNullValue());
        assertThat(carruaje.getId(),notNullValue());
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnCarruajeAsignadoAUnUsuario() {

        repositorioUsuario.guardar(usuarioMock1);
        repositorioUsuario.guardar(usuarioMock2);

        repositorioCarruaje.guardar(carruajeMock1);
        repositorioCarruaje.guardar(carruajeMock2);


        repositorioCarruaje.asignarUsuarioAUnCarruje(carruajeMock2,usuarioMock2);

        Carruaje carruaje = repositorioCarruaje.buscarCarruajeAsignadoAUnUsuario(usuarioMock2);

        assertThat(carruaje,notNullValue());
        assertThat(carruaje.getId(),notNullValue());
        assertThat(carruaje.getUsuario(),notNullValue());
        assertThat(carruaje.getUsuario().getEmail(),equalTo(usuarioMock2.getEmail()));
    }

}
