package com.tallerwebi.infraestructura;

import com.tallerwebi.config.HibernateConfig;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;

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

public class RepositorioUsuarioTest {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnUsuarioPorId() {
        Usuario u1 = new Usuario();
        u1.setEmail("u1@fake.com");
        u1.setPassword("pw1");

        Usuario u2 = new Usuario();
        u2.setEmail("u2@fake.com");
        u2.setPassword("pw2");

        Usuario u3 = new Usuario();
        u3.setEmail("u3@fake.com");
        u3.setPassword("pw3");


        repositorioUsuario.guardar(u1);
        repositorioUsuario.guardar(u2);
        repositorioUsuario.guardar(u3);

        Long idU2 = u2.getId();

        Usuario usuario = repositorioUsuario.buscarUsuarioPorId(idU2);

        assertThat(usuario, notNullValue());
        assertThat(usuario.getId(), equalTo(idU2));
        assertThat(usuario.getEmail(), equalTo("u2@fake.com"));
    }

}
