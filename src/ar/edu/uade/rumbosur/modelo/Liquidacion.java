package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Liquidación de honorarios del guía al cerrar una salida (CU17).
 * Total = honorario fijo por día trabajado + 5 % de los adicionales
 * vendidos en la salida (supuesto S29).
 */
public class Liquidacion implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    // static final: constante de la regla de negocio, compartida por todas las liquidaciones.
    private static final double PORCENTAJE_COMISION_ADICIONALES = 0.05;

    private int codigo;
    private LocalDate fecha;
    private int diasTrabajados;
    private double importeFijo;
    private double comisionAdicionales;
    private double total;

    // Asociaciones: la liquidación es de una salida y de un guía.
    private Salida salida;
    private Guia guia;

    public Liquidacion(int codigo, LocalDate fecha, Salida salida, Guia guia) {
        if (fecha == null || salida == null || guia == null) {
            throw new IllegalArgumentException("La liquidación necesita fecha, salida y guía.");
        }
        this.codigo = codigo;
        this.fecha = fecha;
        this.salida = salida;
        this.guia = guia;
        this.diasTrabajados = 0;
        this.importeFijo = 0;
        this.comisionAdicionales = 0;
        this.total = 0;
    }

    // ---------- Día 2 ----------

    /**
     * Calcula los días trabajados, el importe fijo (honorario por día del guía),
     * la comisión del 5 % sobre los adicionales vendidos en la salida y el total.
     */
    public double calcularTotal() {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    // ---------- Getters ----------

    public int getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getDiasTrabajados() {
        return diasTrabajados;
    }

    public double getImporteFijo() {
        return importeFijo;
    }

    public double getComisionAdicionales() {
        return comisionAdicionales;
    }

    public double getTotal() {
        return total;
    }

    public Salida getSalida() {
        return salida;
    }

    public Guia getGuia() {
        return guia;
    }

    @Override
    public String toString() {
        return "Liquidación " + codigo + " - " + fecha + " - " + diasTrabajados + " día(s) - $" + total;
    }
}
