package ar.edu.uade.rumbosur.excepciones;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Comprueba que JUnit está bien configurado y que las excepciones propias
 * conservan el mensaje, que es lo que muestra el menú con e.getMessage().
 */
class ExcepcionesTest {

    @Test
    void elMensajeLlegaAGetMessage() {
        PagoInvalidoException e = new PagoInvalidoException("El importe debe ser mayor que cero.");
        assertEquals("El importe debe ser mayor que cero.", e.getMessage());
    }

    @Test
    void seLanzaYSeCapturaComoException() {
        Exception e = assertThrows(ReservaNoCancelableException.class, () -> {
            throw new ReservaNoCancelableException("La reserva ya está anulada.");
        });
        assertEquals("La reserva ya está anulada.", e.getMessage());
    }
}
