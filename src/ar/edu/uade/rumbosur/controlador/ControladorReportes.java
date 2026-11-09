package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.TipoReporte;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vendedor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            case VENTAS:
                return generarReporteVentas(desde, hasta);
            case INGRESOS:
                return generarReporteIngresos(desde, hasta);
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

    /** Una venta es una reserva creada en el período que no fue anulada. */
    private String generarReporteVentas(LocalDate desde, LocalDate hasta) {
        Map<Integer, FilaReporte> filasPorLegajo = new HashMap<>();
        for (Reserva reserva : controladorReservas.listarReservas()) {
            if (reserva.getEstado() != EstadoReserva.ANULADA
                    && estaEnPeriodo(reserva.getFechaCreacion(), desde, hasta)) {
                Vendedor vendedor = reserva.getVendedor();
                String nombre = vendedor.getNombre() + " (legajo " + vendedor.getLegajo() + ")";
                obtenerFila(filasPorLegajo, vendedor.getLegajo(), nombre).sumar(reserva.getTotal());
            }
        }

        String titulo = "Ventas por vendedor (" + desde + " a " + hasta + ")\n";
        return armarReporteOrdenado(titulo, filasPorLegajo);
    }

    private String generarReporteIngresos(LocalDate desde, LocalDate hasta) {
        Map<Integer, FilaReporte> filasPorTour = new HashMap<>();
        for (Reserva reserva : controladorReservas.listarReservas()) {
            double ingreso = calcularIngreso(reserva, desde, hasta);
            if (ingreso != 0) {
                Tour tour = reserva.getSalida().getTour();
                obtenerFila(filasPorTour, tour.getCodigo(), tour.getNombre()).sumar(ingreso);
            }
        }

        String titulo = "Ingresos por tour (" + desde + " a " + hasta + ")\n";
        return armarReporteOrdenado(titulo, filasPorTour);
    }

    /** Lo cobrado por la reserva en el período, menos lo devuelto en el período. */
    private double calcularIngreso(Reserva reserva, LocalDate desde, LocalDate hasta) {
        double ingreso = 0;
        for (Pago pago : reserva.getPagos()) {
            if (estaEnPeriodo(pago.getFecha(), desde, hasta)) {
                ingreso += pago.getImporteNeto();
            }
        }
        Cancelacion cancelacion = controladorReservas.getCancelaciones().get(reserva.getNumero());
        if (cancelacion != null && estaEnPeriodo(cancelacion.getFecha(), desde, hasta)) {
            ingreso -= cancelacion.getImporteDevuelto();
        }
        return ingreso;
    }

    private FilaReporte obtenerFila(Map<Integer, FilaReporte> filas, int codigo, String nombre) {
        FilaReporte fila = filas.get(codigo);
        if (fila == null) {
            fila = new FilaReporte(nombre);
            filas.put(codigo, fila);
        }
        return fila;
    }

    /** Ordena las filas de mayor a menor importe y arma el texto del reporte. */
    private String armarReporteOrdenado(String titulo, Map<Integer, FilaReporte> filasPorCodigo) {
        StringBuilder reporte = new StringBuilder(titulo);
        if (filasPorCodigo.isEmpty()) {
            return reporte.append(SIN_RESULTADOS).toString();
        }
        List<FilaReporte> filas = new ArrayList<>(filasPorCodigo.values());
        // Comparator: orden por importe; reversed() lo invierte para que quede de mayor a menor.
        filas.sort(Comparator.comparingDouble(FilaReporte::getImporte).reversed());
        for (FilaReporte fila : filas) {
            reporte.append(fila).append("\n");
        }
        return reporte.toString();
    }

    private boolean estaEnPeriodo(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    /** Una línea de un reporte: un nombre con su cantidad de reservas y su importe acumulado. */
    private static class FilaReporte {

        private String nombre;
        private int cantidad;
        private double importe;

        FilaReporte(String nombre) {
            this.nombre = nombre;
        }

        void sumar(double importeReserva) {
            cantidad++;
            importe += importeReserva;
        }

        double getImporte() {
            return importe;
        }

        @Override
        public String toString() {
            return nombre + " - " + cantidad + " reserva(s) - $" + String.format(Locale.ROOT, "%.2f", importe);
        }
    }
}
