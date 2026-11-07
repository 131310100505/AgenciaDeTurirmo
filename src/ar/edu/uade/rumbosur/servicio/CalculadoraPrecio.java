package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Promocion;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Tarifa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SOLID - SRP: única responsabilidad, calcular el importe de una reserva.
 * Si cambian las reglas de precios o promociones, solo se toca esta clase
 * y no Reserva.
 *
 * Usa Pasajero, Tarifa y Reserva solo como parámetros: relación de dependencia.
 */
public class CalculadoraPrecio {

    private List<Promocion> promociones;

    public CalculadoraPrecio(List<Promocion> promociones) {
        this.promociones = new ArrayList<>(promociones);
    }

    // ---------- Día 2 ----------

    /** Tarifa base de la temporada × porcentaje según el tipo de pasajero (100 %, 60 % o 0 %). */
    public double calcularTarifaPasajero(Pasajero pasajero, Tarifa tarifa, LocalDate fechaSalida) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** Descuento de la promoción más favorable para ese pasajero (no se acumulan). */
    public double obtenerMejorDescuento(Reserva reserva, Pasajero pasajero) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** Suma de los adicionales contratados en la reserva. */
    public double calcularAdicionales(Reserva reserva) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** Total = tarifas con descuento de cada pasajero + adicionales. */
    public double calcularTotal(Reserva reserva) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }
}
