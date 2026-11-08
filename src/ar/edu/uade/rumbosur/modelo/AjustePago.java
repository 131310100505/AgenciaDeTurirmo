package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Nota de ajuste sobre un pago ya registrado (CU19).
 * El pago original no se borra ni se modifica: la corrección queda como
 * un movimiento aparte, para conservar el historial (supuesto S23).
 *
 * El importe tiene signo: negativo descuenta del pago y positivo lo aumenta.
 *
 * Clase completamente inmutable: es final, todos sus atributos son final, no tiene
 * setters y ninguno de sus atributos se puede cambiar desde afuera (LocalDate y
 * String también son inmutables). Un ajuste registrado no se cambia nunca.
 */
public final class AjustePago implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private final int codigo;
    private final LocalDate fecha;
    private final double importeAjuste;
    private final String motivo;

    public AjustePago(int codigo, LocalDate fecha, double importeAjuste, String motivo) {
        if (fecha == null) {
            throw new IllegalArgumentException("El ajuste necesita una fecha.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El ajuste necesita un motivo.");
        }
        this.codigo = codigo;
        this.fecha = fecha;
        this.importeAjuste = importeAjuste;
        this.motivo = motivo;
    }

    public double obtenerImporteAjuste() {
        return importeAjuste;
    }

    public int getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return "Ajuste " + codigo + " - " + fecha + " - $" + importeAjuste + " (" + motivo + ")";
    }
}
