/*
package com.tallerwebi.dominio;



import com.tallerwebi.dominio.entidades.Heroe;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.RepositorioHeroe;
import com.tallerwebi.infraestructura.RepositorioUsuario;
import com.tallerwebi.dominio.servicios.Impl.ServicioSanatorioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class ServicioSanatorioImplTest {

    private RepositorioHeroe repoHeroe;
    private RepositorioUsuario repoUsuario;
    private ServicioSanatorioImpl servicioSanatorio;

    @BeforeEach
    public void setUp() {
        repoHeroe = mock(RepositorioHeroe.class);
        repoUsuario = mock(RepositorioUsuario.class);
        servicioSanatorio = new ServicioSanatorioImpl(repoHeroe, repoUsuario);
    }

    @Test
    public void curarHeroe_curaHeroeSiUsuarioTieneOro() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setOro(100);

        Heroe heroe = new Heroe();
        heroe.setId(5L);
        heroe.setVidaActual(40);
        heroe.setVidaMaxima(100);

        when(repoHeroe.buscarHeroePorId(5L)).thenReturn(heroe);
        when(repoUsuario.buscarUsuarioPorId(1L)).thenReturn(usuario);

        servicioSanatorio.curarHeroe(usuario, 5L);

        assertThat(heroe.getVidaActual(), equalTo(100));
        assertThat(usuario.getOro(), equalTo(50));
        verify(repoHeroe).modificar(heroe);
        verify(repoUsuario).modificar(usuario);
    }


}
*/