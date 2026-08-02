package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicios.ServicioRanking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorRankingTest {

    private ServicioRanking servicioRankingMock;
    private Model modelMock;
    private ControladorRanking controladorRanking;

    @BeforeEach
    public void setUp() {
        servicioRankingMock = mock(ServicioRanking.class);
        modelMock = mock(Model.class);
        controladorRanking = new ControladorRanking(servicioRankingMock);
    }

    @Test
    public void mostrarRanking_deberiaAgregarRankingYRetornarVistaRanking() {
        List<Usuario> listaUsuarios = Arrays.asList(new Usuario(), new Usuario());

        when(servicioRankingMock.obtenerRanking()).thenReturn(listaUsuarios);

        String view = controladorRanking.mostrarRanking(modelMock);

        assertThat(view, equalToIgnoringCase("ranking"));
        verify(modelMock).addAttribute(eq("ranking"), eq(listaUsuarios));
    }
}
