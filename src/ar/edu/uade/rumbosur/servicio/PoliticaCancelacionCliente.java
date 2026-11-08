package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.Reserva;

import java.time.LocalDate;

/**
 * Cancelación pedida por el cliente: el porcentaje depende de los días de
 * anticipación respecto del inicio de la salida.
 *   30 días o más: 90 %  |  29 a 15: 50 %  |  14 a 7: 20 %  |  menos de 7: 0 %
 */
public class PoliticaCancelacionCliente implements PoliticaCancelacion {

    // ---------- Día 2 ----------

    @Override
    public double calcularDevolucion(Reserva reserva, LocalDate fechaCancelacion) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }
}
