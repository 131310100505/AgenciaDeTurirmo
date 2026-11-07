package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Registro de la cancelación de una reserva y del importe devuelto (CU12).
 *
 * El porcentaje de devolución NO se calcula acá: lo calcula una implementación
 * de la interfaz PoliticaCancelacion, elegida según el origen (OCP y DIP).
 * Esta clase solo registra el resultado, así queda el historial (supuesto S24).
 */
public class Cancelacion implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private LocalDate fecha;
    private String motivo;
    private OrigenCancelacion origen;
    private double importeDevuelto;

    public Cancelacion(int codigo, LocalDate fecha, String motivo, OrigenCancelacion origen) {
        if (fecha == null || origen == null) {
            throw new IllegalArgumentException("La cancelación necesita fecha y origen.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("La cancelación necesita un motivo.");
        }
        this.codigo = codigo;
        this.fecha = fecha;
        this.motivo = motivo;
        this.origen = origen;
        this.importeDevuelto = 0;
    }

    // ---------- Día 2 ----------

    /** Registra el importe que devuelve la política de cancelación. */
    public void registrarDevolucion(double importe) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    // ---------- Getters ----------

    public int getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public OrigenCancelacion getOrigen() {
        return origen;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }

    @Override
    public String toString() {
        return "Cancelación " + codigo + " - " + fecha + " - " + origen + " - " + motivo
                + " - devuelto $" + importeDevuelto;
    }
}
