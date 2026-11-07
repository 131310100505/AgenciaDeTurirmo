package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.Reserva;

import java.time.LocalDate;

/**
 * Cancelación hecha por la agencia (por ejemplo, salida sin cupo mínimo):
 * se devuelve el 100 % de lo efectivamente pagado (supuesto S22).
 * La usa también ControladorSalidas al cancelar una salida.
 */
public class PoliticaCancelacionAgencia implements PoliticaCancelacion {

    // ---------- Día 2 ----------

    @Override
    public double calcularDevolucion(Reserva reserva, LocalDate fechaCancelacion) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }
}
