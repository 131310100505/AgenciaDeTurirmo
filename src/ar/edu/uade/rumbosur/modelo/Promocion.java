package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

/**
 * Promoción o descuento: jubilado (10 %), reserva anticipada (15 %) o grupo (10 %).
 * No se acumulan: CalculadoraPrecio elige la más favorable (supuesto S13).
 */
public class Promocion implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nombre;
    private TipoPromocion tipo;
    private double porcentajeDescuento; // ejemplo: 0.15 = 15 %

    public Promocion(int codigo, String nombre, TipoPromocion tipo, double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 1) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1.");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    /** Descuento en pesos sobre un importe base. */
    public double calcularDescuento(double importeBase) {
        return importeBase * porcentajeDescuento;
    }

    // ---------- Día 2 ----------

    /** Indica si la promoción corresponde a ese pasajero de esa reserva. */
    public boolean esAplicable(Reserva reserva, Pasajero pasajero) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoPromocion getTipo() {
        return tipo;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public String toString() {
        return nombre + " (" + (porcentajeDescuento * 100) + " %)";
    }
}
