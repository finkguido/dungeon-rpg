package com.tallerwebi.dominio;

import com.tallerwebi.config.PasswordHasher;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicios.impl.ServicioLoginImpl;
import com.tallerwebi.infraestructura.RepositorioInventario;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioLoginImplTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioInventario repositorioInventario;
    private PasswordHasher passwordHasher;
    private ServicioLoginImpl servicioLogin;

    @BeforeEach
    void setUp() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioInventario = mock(RepositorioInventario.class);
        passwordHasher = new PasswordHasher();
        servicioLogin = new ServicioLoginImpl(repositorioUsuario, repositorioInventario, passwordHasher);
    }

    @Test
    void loginConContraseniaCorrectaDebeDevolverUsuario() {
        Usuario guardado = new Usuario();
        guardado.setEmail("admin@dungeon.local");
        guardado.setPassword(passwordHasher.hash("test"));

        when(repositorioUsuario.buscar("admin@dungeon.local")).thenReturn(guardado);

        Usuario resultado = servicioLogin.consultarUsuario("admin@dungeon.local", "test");

        assertNotNull(resultado);
        assertEquals("admin@dungeon.local", resultado.getEmail());
    }

    @Test
    void loginConContraseniaIncorrectaDebeDevolverNull() {
        Usuario guardado = new Usuario();
        guardado.setEmail("admin@dungeon.local");
        guardado.setPassword(passwordHasher.hash("test"));

        when(repositorioUsuario.buscar("admin@dungeon.local")).thenReturn(guardado);

        assertNull(servicioLogin.consultarUsuario("admin@dungeon.local", "clave-mala"));
    }

    @Test
    void registrarDebeGuardarHashYNoTextoPlano() throws UsuarioExistente {
        when(repositorioUsuario.buscar("nuevo@mail.com")).thenReturn(null);

        Usuario nuevo = new Usuario();
        nuevo.setEmail("nuevo@mail.com");
        nuevo.setPassword("miPassword");

        servicioLogin.registrar(nuevo);

        assertNotEquals("miPassword", nuevo.getPassword());
        assertTrue(nuevo.getPassword().startsWith("$2a$") || nuevo.getPassword().startsWith("$2b$"));
        verify(repositorioUsuario).guardar(nuevo);
        verify(repositorioInventario).guardar(any());
    }
}
