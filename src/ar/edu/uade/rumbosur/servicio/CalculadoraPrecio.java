package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.AdicionalContratado;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Promocion;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;

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

    private static final int EDAD_SIN_TARIFA = 3;  // menores de 3 no pagan tarifa base
    private static final int EDAD_MENOR = 12;      // menores de 12 pagan el porcentaje de MENOR

    private List<Promocion> promociones;

    public CalculadoraPrecio(List<Promocion> promociones) {
        this.promociones = new ArrayList<>(promociones);
    }

    /**
     * CU06 pasos 2 y 3: tarifa del pasajero según su categoría.
     * La categoría sale de la edad a la fecha de inicio de la salida (supuesto S08):
     * menor de 3 = 0 %, menor de 12 = porcentaje de MENOR (60 %), resto = ADULTO (100 %).
     */
    public double calcularTarifaPasajero(Pasajero pasajero, Tarifa tarifa, LocalDate fechaSalida) {
        int edad = pasajero.calcularEdad(fechaSalida);
        if (edad < EDAD_SIN_TARIFA) {
            return 0;
        }
        TipoPasajero categoria = edad < EDAD_MENOR ? TipoPasajero.MENOR : TipoPasajero.ADULTO;
        return tarifa.getPrecioBase() * categoria.getPorcentajeTarifa();
    }

    /**
     * CU06 pasos 4 y 5: descuento de la promoción más favorable para ese pasajero.
     * Las promociones no se acumulan (supuesto S13): se queda con el mayor descuento.
     * El descuento se calcula sobre la tarifa del pasajero, antes de los adicionales (S12).
     */
    public double obtenerMejorDescuento(Reserva reserva, Pasajero pasajero) {
        Salida salida = reserva.getSalida();
        double importeBase = calcularTarifaPasajero(pasajero, obtenerTarifaVigente(salida),
                salida.getFechaInicio());
        double mejorDescuento = 0;
        for (Promocion promocion : promociones) {
            if (promocion.esAplicable(reserva, pasajero)) {
                double descuento = promocion.calcularDescuento(importeBase);
                if (descuento > mejorDescuento) {
                    mejorDescuento = descuento;
                }
            }
        }
        return mejorDescuento;
    }

    /** CU06 paso 6: suma de los adicionales contratados en la reserva (incluye seguros). */
    public double calcularAdicionales(Reserva reserva) {
        double totalAdicionales = 0;
        for (AdicionalContratado adicional : reserva.getAdicionales()) {
            totalAdicionales += adicional.calcularSubtotal();
        }
        return totalAdicionales;
    }

    /**
     * CU06 completo (mensaje calcularTotal del diagrama CU05):
     * total = suma de (tarifa - mejor descuento) de cada pasajero + adicionales.
     */
    public double calcularTotal(Reserva reserva) {
        Salida salida = reserva.getSalida();
        Tarifa tarifa = obtenerTarifaVigente(salida);
        double total = 0;
        for (Pasajero pasajero : reserva.getPasajeros()) {
            double tarifaPasajero = calcularTarifaPasajero(pasajero, tarifa, salida.getFechaInicio());
            total += tarifaPasajero - obtenerMejorDescuento(reserva, pasajero);
        }
        total += calcularAdicionales(reserva);
        return redondear(total);
    }

    /** CU06 flujo alternativo: si no hay tarifa vigente para la fecha, se informa el error. */
    private Tarifa obtenerTarifaVigente(Salida salida) {
        Tarifa tarifa = salida.getTour().obtenerTarifaVigente(salida.getFechaInicio());
        if (tarifa == null) {
            throw new IllegalArgumentException("El tour no tiene una tarifa vigente para el "
                    + salida.getFechaInicio() + ".");
        }
        return tarifa;
    }

    // Redondea a centavos para evitar valores como 700.0000000000001.
    private double redondear(double importe) {
        return Math.round(importe * 100) / 100.0;
    }
}
