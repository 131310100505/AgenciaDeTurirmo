package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.Reserva;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Cancelación pedida por el cliente: el porcentaje depende de los días de
 * anticipación respecto del inicio de la salida.
 *   30 días o más: 90 %  |  29 a 15: 50 %  |  14 a 7: 20 %  |  menos de 7: 0 %
 */
public class PoliticaCancelacionCliente implements PoliticaCancelacion {

    @Override
    public double calcularDevolucion(Reserva reserva, LocalDate fechaCancelacion) {
        long diasAnticipacion = ChronoUnit.DAYS.between(fechaCancelacion, reserva.getSalida().getFechaInicio());
        return reserva.calcularTotalPagado() * obtenerPorcentaje(diasAnticipacion);
    }

    private double obtenerPorcentaje(long diasAnticipacion) {
        if (diasAnticipacion >= 30) {
            return 0.90;
        }
        if (diasAnticipacion >= 15) {
            return 0.50;
        }
        if (diasAnticipacion >= 7) {
            return 0.20;
        }
        return 0;
    }
}
