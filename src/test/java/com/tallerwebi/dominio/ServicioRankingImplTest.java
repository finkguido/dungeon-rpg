package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.Impl.ServicioRankingImpl;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.ServicioRanking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioRankingImplTest {

    private RepositorioUsuario usuarioRepoMock;
    private ServicioRanking servicioRanking;

    @BeforeEach
    public void setUp() {
        usuarioRepoMock = mock(RepositorioUsuario.class);
        servicioRanking = new ServicioRankingImpl(usuarioRepoMock);
    }

    @Test
    public void obtenerRanking_deberiaRetornarListaDeUsuarios() {
        Usuario u1 = new Usuario();
        u1.setEmail("a@b.com");
        Usuario u2 = new Usuario();
        u2.setEmail("c@d.com");
        List<Usuario> lista = Arrays.asList(u1, u2);

        when(usuarioRepoMock.obtenerRankingJugadores()).thenReturn(lista);

        List<Usuario> resultado = servicioRanking.obtenerRanking();

        assertThat(resultado, hasSize(2));
        assertThat(resultado, contains(u1, u2));
        verify(usuarioRepoMock, times(1)).obtenerRankingJugadores();
    }

    @Test
    public void obtenerRanking_deberiaRetornarListaVaciaSiNoHayUsuarios() {
        when(usuarioRepoMock.obtenerRankingJugadores()).thenReturn(Arrays.asList());

        List<Usuario> resultado = servicioRanking.obtenerRanking();

        assertThat(resultado, is(empty()));
        verify(usuarioRepoMock, times(1)).obtenerRankingJugadores();
    }
}
