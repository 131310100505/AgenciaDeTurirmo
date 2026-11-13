package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorReportes;
import ar.edu.uade.rumbosur.modelo.TipoReporte;

import java.time.LocalDate;

/**
 * Submenú del administrador (CU18 Generar reportes).
 *
 * MVC: la vista solo pide los datos y muestra el resultado. El reporte lo arma
 * el controlador, así que acá no hay ninguna cuenta.
 */
public class VistaAdministrador {

    private final ControladorReportes controladorReportes;
    private final EntradaConsola entrada;

    public VistaAdministrador(ControladorReportes controladorReportes, EntradaConsola entrada) {
        this.controladorReportes = controladorReportes;
        this.entrada = entrada;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- Administrador: reportes ---");
            System.out.println("1. Ocupación por salida");
            System.out.println("2. Ventas por vendedor");
            System.out.println("3. Ingresos por tour");
            System.out.println("4. Cancelaciones");
            System.out.println("0. Volver");
            opcion = entrada.leerOpcion("Opción: ", 0, 4);
            if (opcion != 0) {
                // Las opciones 1 a 4 siguen el orden del enum TipoReporte
                atenderReporte(TipoReporte.values()[opcion - 1]);
            }
        } while (opcion != 0);
    }

    private void atenderReporte(TipoReporte tipo) {
        LocalDate desde = entrada.leerFecha("Desde (dd/mm/aaaa): ");
        LocalDate hasta = entrada.leerFecha("Hasta (dd/mm/aaaa): ");
        if (hasta.isBefore(desde)) {
            System.out.println("La fecha final no puede ser anterior a la inicial.");
            return;
        }
        System.out.println();
        System.out.print(controladorReportes.generarReporte(tipo, desde, hasta));
    }
}
