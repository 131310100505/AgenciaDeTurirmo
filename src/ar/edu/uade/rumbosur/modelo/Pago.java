package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pago registrado sobre una reserva (seña, pago parcial o saldo).
 *
 * Los datos del pago (importe, fecha, medio, cuotas y recargo) no se modifican:
 * son final y no tienen setters, y la clase es final para que nadie la herede y
 * cambie ese comportamiento. Encaja con la regla de negocio de que un pago
 * registrado no se modifica ni se borra (supuesto S23).
 *
 * La clase no es inmutable del todo: las correcciones se agregan como ajustes con
 * agregarAjuste(), y la lista de ajustes solo admite agregar, así queda el historial
 * completo de movimientos (composición: los ajustes pertenecen al pago).
 * El ejemplo de clase completamente inmutable es AjustePago.
 */
public final class Pago implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    // static final: constante de la regla de negocio, compartida por todos los pagos.
    private static final double PORCENTAJE_RECARGO_CUOTAS = 0.07;

    private final int codigo;
    private final LocalDate fecha;
    private final double importe;
    private final MedioPago medio;
    private final int cantidadCuotas;
    private final double recargo;

    // Composición 1 a 0..*: notas de ajuste del pago. Solo se agregan, nunca se borran.
    private final List<AjustePago> ajustes;

    public Pago(int codigo, LocalDate fecha, double importe, MedioPago medio, int cantidadCuotas) {
        if (fecha == null || medio == null) {
            throw new IllegalArgumentException("El pago necesita fecha y medio de pago.");
        }
        if (importe <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor que cero.");
        }
        if (cantidadCuotas < 1) {
            throw new IllegalArgumentException("La cantidad de cuotas debe ser al menos 1.");
        }
        this.codigo = codigo;
        this.fecha = fecha;
        this.importe = importe;
        this.medio = medio;
        this.cantidadCuotas = cantidadCuotas;
        // Como el atributo es final, el recargo se calcula una sola vez, al crear el pago.
        this.recargo = calcularRecargo();
        this.ajustes = new ArrayList<>();
    }

    /** Recargo del 7 % solo para tarjeta en cuotas (supuesto S19). */
    public double calcularRecargo() {
        if (medio == MedioPago.TARJETA && cantidadCuotas > 1) {
            return importe * PORCENTAJE_RECARGO_CUOTAS;
        }
        return 0;
    }

    /** Lo que se le cobra al cliente: importe más el recargo por cuotas. */
    public double obtenerImporteTotal() {
        return importe + recargo;
    }

    /**
     * Importe que cuenta como pagado para la reserva, ya aplicados los ajustes.
     * No incluye el recargo: es el costo de financiar en cuotas, no un pago del tour.
     * Lo usa Reserva.calcularTotalPagado().
     */
    public double getImporteNeto() {
        double neto = importe;
        for (AjustePago ajuste : ajustes) {
            neto += ajuste.obtenerImporteAjuste();
        }
        return neto;
    }

    // ---------- Día 2 ----------

    /**
     * Agrega una nota de ajuste sin modificar los datos originales del pago (CU19).
     * El día 2 se agrega la validación con PagoInvalidoException.
     */
    public void agregarAjuste(AjustePago ajuste) {
        if (ajuste == null) {
            throw new IllegalArgumentException("El ajuste no puede ser nulo.");
        }
        ajustes.add(ajuste);
    }

    // ---------- Getters (sin setters: los datos del pago no se modifican) ----------

    public int getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public MedioPago getMedio() {
        return medio;
    }

    public int getCantidadCuotas() {
        return cantidadCuotas;
    }

    public double getRecargo() {
        return recargo;
    }

    /** Devuelve una vista de solo lectura: desde afuera no se pueden borrar ajustes. */
    public List<AjustePago> getAjustes() {
        return Collections.unmodifiableList(ajustes);
    }

    @Override
    public String toString() {
        String cuotas = cantidadCuotas > 1 ? " en " + cantidadCuotas + " cuotas" : "";
        return "Pago " + codigo + " - " + fecha + " - " + medio + cuotas
                + " - $" + importe + " (neto $" + getImporteNeto() + ")";
    }
}
