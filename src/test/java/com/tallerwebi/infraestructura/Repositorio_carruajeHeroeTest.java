package com.tallerwebi.infraestructura;
import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Carruaje;
import com.tallerwebi.dominio.entidades.CarruajeHeroe;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
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

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        HibernateConfig.class
})
public class Repositorio_carruajeHeroeTest {

    private Usuario usuarioMock;
    private Heroe heroeMock1;
    private Heroe heroeMock2;
    private Carruaje carruajeMock1;
    private Carruaje carruajeMock2;
    private Carruaje carruajeMock3;

    @Autowired
    private RepositorioHeroe repositorioHeroe;

    @Autowired
    private RepositorioCarruaje repositorioCarruaje;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private Repositorio_carruajeHeroe repositorio_carruajeHeroe;

    @BeforeEach
    public void init(){
        usuarioMock = new Usuario(1L,"us1");

        heroeMock1 = new Heroe(1L,"h1");
        heroeMock2 = new Heroe(2L,"h2");

        carruajeMock1 = new Carruaje(1L,0,0,2);
        carruajeMock2 = new Carruaje(2L,0,0,2);
        carruajeMock3 = new Carruaje(0,0,2);
    }

    //queSePuedaAsignarUnCarruajeCon2Heroes_AUnUsuarioNuevo

    @Test
    @Transactional
    @Rollback
    public void queSePuedaAsignarUnCarruajeCon2Heroes_AUnUsuarioNuevo() {

        repositorioUsuario.guardar(usuarioMock);

        repositorioHeroe.guardar(heroeMock1);
        repositorioHeroe.guardar(heroeMock2);

        Usuario usuarioBus = repositorioUsuario.buscarUsuarioPorId(usuarioMock.getId());

        Carruaje carruajeGuardado = repositorioCarruaje.guardar(carruajeMock3);
        repositorioCarruaje.asignarUsuarioAUnCarruje(carruajeGuardado,usuarioBus);


        repositorio_carruajeHeroe.agregarRelacion(carruajeGuardado,heroeMock1);
        repositorio_carruajeHeroe.agregarRelacion(carruajeGuardado,heroeMock2);


        List<Heroe> listaHeroes =  repositorio_carruajeHeroe.getListaDeHeroes(carruajeGuardado.getId());

        assertThat(carruajeGuardado.getId(), notNullValue());
        assertThat(listaHeroes,notNullValue());
        assertThat(listaHeroes.isEmpty(),equalTo(false));
        assertThat(listaHeroes.size(),equalTo(2));
        assertThat(listaHeroes.get(0),notNullValue());
        assertThat(listaHeroes.get(0).getId(),notNullValue());
        assertThat(listaHeroes.get(0).getId(),equalTo(heroeMock1.getId()));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaPuedaObtenerLaListaDeHeroesDeUnCarruaje() {

        repositorioHeroe.guardar(heroeMock1);
        repositorioHeroe.guardar(heroeMock2);

        repositorioCarruaje.guardar(carruajeMock1);
        repositorioCarruaje.guardar(carruajeMock2);

        repositorio_carruajeHeroe.agregarRelacion(carruajeMock2,heroeMock1);
        repositorio_carruajeHeroe.agregarRelacion(carruajeMock2,heroeMock2);


        List<Heroe> listaHeroes =  repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock2.getId());

        assertThat(listaHeroes,notNullValue());
        assertThat(listaHeroes.isEmpty(),equalTo(false));
        assertThat(listaHeroes.size(),equalTo(2));
        assertThat(listaHeroes.get(0),notNullValue());
        assertThat(listaHeroes.get(0).getId(),notNullValue());
        assertThat(listaHeroes.get(0).getId(),equalTo(heroeMock1.getId()));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaPuedaRemoverLaRelacion_carruajeHeroe() {

        repositorioHeroe.guardar(heroeMock1);
        repositorioHeroe.guardar(heroeMock2);

        repositorioCarruaje.guardar(carruajeMock1);
        repositorioCarruaje.guardar(carruajeMock2);

        repositorio_carruajeHeroe.agregarRelacion(carruajeMock2,heroeMock1);
        repositorio_carruajeHeroe.agregarRelacion(carruajeMock2,heroeMock2);


        CarruajeHeroe relacion  = repositorio_carruajeHeroe.buscarRelacion(carruajeMock2,heroeMock1);
        repositorio_carruajeHeroe.removerRelacion(relacion);

        List<Heroe> listaHeroes =  repositorio_carruajeHeroe.getListaDeHeroes(carruajeMock2.getId());
        assertThat(listaHeroes,notNullValue());
        assertThat(listaHeroes.isEmpty(),equalTo(false));
        assertThat(listaHeroes.size(),equalTo(1));
        assertThat(listaHeroes.get(0),notNullValue());
        assertThat(listaHeroes.get(0).getId(),notNullValue());
        assertThat(listaHeroes.get(0).getId(),equalTo(heroeMock2.getId()));
    }





}
