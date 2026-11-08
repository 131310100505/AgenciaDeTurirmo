
package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SalidaTest {

    // Tour de prueba para crear salidas válidas
    private Tour crearTour() {
        return new Tour(
                1,
                "Bariloche",
                "Tour de prueba",
                5,
                5,
                20
        ) {
            @Override
            public CategoriaTour obtenerCategoria() {
                return CategoriaTour.NATURALEZA;
            }

            @Override
            public boolean validarPasajero(
                    Pasajero pasajero,
                    LocalDate fechaSalida) {
                return true;
            }
        };
    }

    @Test
    void testEstadoInicial() {
        Salida salida = new Salida(
                1,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15),
                5,
                20,
                crearTour()
        );

        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
        assertEquals(0, salida.calcularOcupacion());
        assertFalse(salida.isListaPasajerosGenerada());
    }

    @Test
    void testVerificarCupo() {
        Salida salida = new Salida(
                2,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15),
                5,
                20,
                crearTour()
        );

        assertTrue(salida.verificarCupo(10));
        assertFalse(salida.verificarCupo(21));
    }

    @Test
    void testOrdenarPorFecha() {
        Salida salida1 = new Salida(
                1,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15),
                5,
                20,
                crearTour()
        );

        Salida salida2 = new Salida(
                2,
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 12, 25),
                5,
                20,
                crearTour()
        );

        assertTrue(salida1.compareTo(salida2) < 0);
    }
}
