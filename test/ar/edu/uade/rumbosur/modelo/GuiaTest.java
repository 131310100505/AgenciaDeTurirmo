
package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GuiaTest {

    // Crea un guía habilitado para tours de aventura y naturaleza
    private Guia crearGuia() {
        return new Guia(
                1,
                "Juan Perez",
                "12345678",
                "1123456789",
                List.of("Español", "Inglés"),
                Set.of(CategoriaTour.AVENTURA, CategoriaTour.NATURALEZA),
                50000.0
        );
    }

    @Test
    void testGuiaHabilitado() {
        Guia guia = crearGuia();

        assertTrue(guia.estaHabilitado(CategoriaTour.AVENTURA));
        assertTrue(guia.estaHabilitado(CategoriaTour.NATURALEZA));
    }

    @Test
    void testGuiaNoHabilitado() {
        Guia guia = crearGuia();

        assertFalse(guia.estaHabilitado(CategoriaTour.CULTURAL));
    }

    @Test
    void testDatosDelGuia() {
        Guia guia = crearGuia();

        assertEquals(2, guia.getIdiomas().size());
        assertEquals(2, guia.getCategoriasHabilitadas().size());
        assertEquals(50000.0, guia.getHonorarioPorDia());
    }
}
