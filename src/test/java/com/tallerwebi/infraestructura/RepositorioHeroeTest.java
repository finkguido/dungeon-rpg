package com.tallerwebi.infraestructura;

import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
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
public class RepositorioHeroeTest {

    private Heroe heroe1;
    private Heroe heroe2;

    @Autowired
    private RepositorioHeroe repositorioHeroe;

    @Autowired
    private SessionFactory sessionFactory;

    @BeforeEach
    public void init(){
        heroe1 = new Heroe(1L,"h1");
        heroe2 = new Heroe(2L,"h2");
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnHeroePorId() {
        repositorioHeroe.guardar(heroe1);
        repositorioHeroe.guardar(heroe2);

        Heroe h = repositorioHeroe.buscarHeroePorId(heroe2.getId());

        assertThat(h,notNullValue());
        assertThat(h.getId(),notNullValue());
        assertThat(h.getNombre(),equalTo("h2"));

    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaObtenerUnaListaDeHeroes() {

        sessionFactory.getCurrentSession().save(heroe1);
        sessionFactory.getCurrentSession().save(heroe2);


        List<Heroe> heroes = repositorioHeroe.getListaDeHeroes();

        assertThat(heroes,notNullValue());
        assertThat(heroes.get(0).getId(),notNullValue());
        assertThat(heroes.get(1).getId(),notNullValue());

    }

}
