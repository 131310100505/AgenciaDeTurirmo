package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorReportes;
import ar.edu.uade.rumbosur.modelo.TipoReporte;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

/**
 * Submenú del administrador (CU18 Generar reportes).
 *
 * MVC: la vista solo pide los datos y muestra el resultado. El reporte lo arma
 * el controlador, así que acá no hay ninguna cuenta.
 */
public class VistaAdministrador {

    // STRICT: rechaza fechas que no existen, como el 31/02
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final ControladorReportes controladorReportes;
    private final Scanner entrada;

    /** Recibe el Scanner de afuera porque tiene que haber uno solo leyendo System.in. */
    public VistaAdministrador(ControladorReportes controladorReportes, Scanner entrada) {
        this.controladorReportes = controladorReportes;
        this.entrada = entrada;
    }

    public void mostrarMenu() {
        String opcion;
        do {
            imprimirOpciones();
            opcion = entrada.nextLine().trim();
            TipoReporte tipo = obtenerTipo(opcion);
            if (tipo != null) {
                atenderReporte(tipo);
            } else if (!opcion.equals("0")) {
                System.out.println("Opción inválida.");
            }
        } while (!opcion.equals("0"));
    }

    private void imprimirOpciones() {
        System.out.println();
        System.out.println("--- Administrador: reportes ---");
        System.out.println("1. Ocupación por salida");
        System.out.println("2. Ventas por vendedor");
        System.out.println("3. Ingresos por tour");
        System.out.println("4. Cancelaciones");
        System.out.println("0. Volver");
        System.out.print("Opción: ");
    }

    private TipoReporte obtenerTipo(String opcion) {
        switch (opcion) {
            case "1":
                return TipoReporte.OCUPACION;
            case "2":
                return TipoReporte.VENTAS;
            case "3":
                return TipoReporte.INGRESOS;
            case "4":
                return TipoReporte.CANCELACIONES;
            default:
                return null;
        }
    }

    private void atenderReporte(TipoReporte tipo) {
        LocalDate desde = leerFecha("Desde (dd/mm/aaaa): ");
        LocalDate hasta = leerFecha("Hasta (dd/mm/aaaa): ");
        if (hasta.isBefore(desde)) {
            System.out.println("La fecha final no puede ser anterior a la inicial.");
            return;
        }

        System.out.println();
        System.out.print(controladorReportes.generarReporte(tipo, desde, hasta));

        System.out.print("¿Exportar a un archivo de texto? (s/n): ");
        if (entrada.nextLine().trim().equalsIgnoreCase("s")) {
            exportar(tipo, desde, hasta);
        }
    }

    private void exportar(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        System.out.print("Nombre del archivo (por ejemplo ventas.txt): ");
        String nombre = entrada.nextLine().trim();
        try {
            controladorReportes.exportarReporte(tipo, desde, hasta, nombre);
            System.out.println("Reporte guardado en " + nombre);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("No se pudo exportar el reporte: " + e.getMessage());
        }
    }

    /** Vuelve a preguntar hasta que la fecha sea válida. */
    private LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDate.parse(entrada.nextLine().trim(), FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida.");
            }
        }
    }
}
