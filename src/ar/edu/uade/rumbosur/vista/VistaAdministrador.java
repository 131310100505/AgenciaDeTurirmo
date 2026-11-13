package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorCatalogo;
import ar.edu.uade.rumbosur.controlador.ControladorReportes;
import ar.edu.uade.rumbosur.modelo.TipoReporte;

import java.time.LocalDate;

/**
 * Menú del administrador: reportes (CU18) y catálogo de tours (CU01 y CU02).
 *
 * MVC: la vista solo pide los datos y muestra el resultado. El reporte lo arma
 * el controlador, así que acá no hay ninguna cuenta.
 */
public class VistaAdministrador {

    private final ControladorReportes controladorReportes;
    private final ControladorCatalogo controladorCatalogo;
    private final EntradaConsola entrada;

    public VistaAdministrador(ControladorReportes controladorReportes, ControladorCatalogo controladorCatalogo,
                              EntradaConsola entrada) {
        this.controladorReportes = controladorReportes;
        this.controladorCatalogo = controladorCatalogo;
        this.entrada = entrada;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- Administrador ---");
            System.out.println("1. Reporte de ocupación por salida");
            System.out.println("2. Reporte de ventas por vendedor");
            System.out.println("3. Reporte de ingresos por tour");
            System.out.println("4. Reporte de cancelaciones");
            System.out.println("5. Catálogo de tours (tours, itinerarios y tarifas)");
            System.out.println("0. Volver");
            opcion = entrada.leerOpcion("Opción: ", 0, 5);
            if (opcion == 5) {
                new VistaCatalogo(entrada, controladorCatalogo).mostrarMenu();
            } else if (opcion != 0) {
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
