package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.TipoReporte;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CU18 Generar reportes.
 *
 * GRASP Controlador: recibe el pedido de la vista y arma el reporte.
 * GRASP Bajo acoplamiento: no guarda datos propios, se los pide a los otros
 * controladores, que son quienes los administran.
 */
public class ControladorReportes {

    private static final String SIN_RESULTADOS = "No hay resultados para el período indicado.\n";

    private ControladorReservas controladorReservas;
    private ControladorSalidas controladorSalidas;

    public ControladorReportes(ControladorReservas controladorReservas, ControladorSalidas controladorSalidas) {
        if (controladorReservas == null || controladorSalidas == null) {
            throw new IllegalArgumentException("El controlador de reportes necesita los otros controladores.");
        }
        this.controladorReservas = controladorReservas;
        this.controladorSalidas = controladorSalidas;
    }

    /** Arma el reporte pedido para las fechas entre desde y hasta, ambas incluidas. */
    public String generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        if (tipo == null || desde == null || hasta == null) {
            throw new IllegalArgumentException("El reporte necesita un tipo y un período.");
        }
        switch (tipo) {
            case OCUPACION:
                return generarReporteOcupacion(desde, hasta);
            default:
                throw new UnsupportedOperationException("Reporte pendiente: " + tipo);
        }
    }

    private String generarReporteOcupacion(LocalDate desde, LocalDate hasta) {
        List<Salida> salidas = new ArrayList<>();
        for (Salida salida : controladorSalidas.listarSalidas()) { // ya vienen ordenadas por fecha
            if (estaEnPeriodo(salida.getFechaInicio(), desde, hasta)) {
                salidas.add(salida);
            }
        }

        StringBuilder reporte = new StringBuilder("Ocupación por salida (" + desde + " a " + hasta + ")\n");
        if (salidas.isEmpty()) {
            return reporte.append(SIN_RESULTADOS).toString();
        }
        for (Salida salida : salidas) {
            int ocupacion = salida.calcularOcupacion();
            reporte.append("Salida ").append(salida.getCodigo())
                    .append(" - ").append(salida.getTour().getNombre())
                    .append(" - ").append(salida.getFechaInicio())
                    .append(" - ").append(ocupacion).append(" de ").append(salida.getCupoMaximo()).append(" lugares")
                    .append(" (").append(ocupacion * 100 / salida.getCupoMaximo()).append(" %)")
                    .append(" - ").append(salida.getEstado())
                    .append("\n");
        }
        return reporte.toString();
    }

    private boolean estaEnPeriodo(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }
}
