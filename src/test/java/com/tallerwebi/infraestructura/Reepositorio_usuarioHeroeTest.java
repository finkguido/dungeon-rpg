package com.tallerwebi.infraestructura;
import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Heroe;
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
public class Reepositorio_usuarioHeroeTest {

    private Heroe heroeMock1;
    private Heroe heroeMock2;
    private Usuario usuarioMock1;
    private Usuario usuarioMock2;

    @Autowired
    private RepositorioHeroe repositorioHeroe;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private Repositorio_usuarioHeroe repositorio_usuarioHeroe;

    @BeforeEach
    public void init(){
        heroeMock1 = new Heroe(1L,"h1");
        heroeMock2 = new Heroe(2L,"h2");

        usuarioMock1 = new Usuario(1L,"u1");
        usuarioMock2 = new Usuario(2L,"u2");
    }

//    @Test
//    @Transactional
//    @Rollback
//    public void queSePuedaPuedaObtenerLaListaDeHeroesDeUnUsuario() {
//
//        repositorioHeroe.guardar(heroeMock1);
//        repositorioHeroe.guardar(heroeMock2);
//
//        repositorioUsuario.guardar(usuarioMock1);
//        repositorioUsuario.guardar(usuarioMock2);
//
//        repositorio_usuarioHeroe.agregarRelacion(usuarioMock2,heroeMock1);
//        repositorio_usuarioHeroe.agregarRelacion(usuarioMock2,heroeMock2);
//
//
//        List<Heroe> listaHeroes =  repositorio_usuarioHeroe.getListaDeHeroes(usuarioMock2.getId());
//
//        assertThat(listaHeroes,notNullValue());
//        assertThat(listaHeroes.isEmpty(),equalTo(false));
//        assertThat(listaHeroes.size(),equalTo(2));
//        assertThat(listaHeroes.get(0),notNullValue());
//        assertThat(listaHeroes.get(0).getId(),notNullValue());
//        assertThat(listaHeroes.get(0).getId(),equalTo(heroeMock1.getId()));
//    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaPuedaObtenerLaListaDeHeroesDeUnUsuario() {

        repositorioHeroe.guardar(heroeMock1);
        repositorioHeroe.guardar(heroeMock2);

        repositorioUsuario.guardar(usuarioMock1);
        repositorioUsuario.guardar(usuarioMock2);

        repositorio_usuarioHeroe.agregarRelacion(usuarioMock2,heroeMock1);
        repositorio_usuarioHeroe.agregarRelacion(usuarioMock2,heroeMock2);


        List<Heroe> listaHeroes =  repositorio_usuarioHeroe.getListaDeHeroes(usuarioMock2.getId());

        assertThat(listaHeroes,notNullValue());
        assertThat(listaHeroes.isEmpty(),equalTo(false));
        assertThat(listaHeroes.size(),equalTo(2));
        assertThat(listaHeroes.get(0),notNullValue());
        assertThat(listaHeroes.get(0).getId(),notNullValue());
        assertThat(listaHeroes.get(0).getId(),equalTo(heroeMock1.getId()));
    }
    
    
}
