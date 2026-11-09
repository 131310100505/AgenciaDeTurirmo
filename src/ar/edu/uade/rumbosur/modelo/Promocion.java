package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;

/**
 * Promoción o descuento: jubilado (10 %), reserva anticipada (15 %) o grupo (10 %).
 * No se acumulan: CalculadoraPrecio elige la más favorable (supuesto S13).
 */
public class Promocion implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private static final int DIAS_ANTICIPACION_MINIMOS = 60; // supuesto S14: "más de 60 días"
    private static final int PASAJEROS_MINIMOS_GRUPO = 6;    // supuesto S15

    private int codigo;
    private String nombre;
    private TipoPromocion tipo;
    private double porcentajeDescuento; // ejemplo: 0.15 = 15 %

    public Promocion(int codigo, String nombre, TipoPromocion tipo, double porcentajeDescuento) {
        if (tipo == null) {
            throw new IllegalArgumentException("La promoción necesita un tipo.");
        }
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

    /**
     * Indica si la promoción corresponde a ese pasajero de esa reserva
     * (mensaje esAplicable del diagrama CU05).
     */
    public boolean esAplicable(Reserva reserva, Pasajero pasajero) {
        if (tipo == TipoPromocion.JUBILADO) {
            // Supuesto S08: la condición de jubilado es un dato registrado en el pasajero.
            return pasajero.getTipo() == TipoPasajero.JUBILADO;
        }
        if (tipo == TipoPromocion.ANTICIPADA) {
            // Supuesto S14: días entre la creación de la reserva y el inicio de la salida.
            long dias = ChronoUnit.DAYS.between(reserva.getFechaCreacion(),
                    reserva.getSalida().getFechaInicio());
            return dias > DIAS_ANTICIPACION_MINIMOS;
        }
        // GRUPO: todos los pasajeros ocupan cupo, incluso los menores de 3 (supuestos S05 y S15).
        return reserva.getCantidadPasajeros() >= PASAJEROS_MINIMOS_GRUPO;
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
