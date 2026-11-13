
package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorCatalogo;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.excepciones.TransicionEstadoInvalidaException;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Liquidacion;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vehiculo;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class VistaCoordinador {

    private final EntradaConsola entrada;
    private final ControladorSalidas controladorSalidas;
    private final ControladorCatalogo controladorCatalogo;

    public VistaCoordinador(EntradaConsola entrada,
                            ControladorSalidas controladorSalidas,
                            ControladorCatalogo controladorCatalogo) {
        this.entrada = entrada;
        this.controladorSalidas = controladorSalidas;
        this.controladorCatalogo = controladorCatalogo;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println();
            System.out.println("===== COORDINADOR DE OPERACIONES =====");
            System.out.println("1. Listar salidas");
            System.out.println("2. Programar salida");
            System.out.println("3. Asignar guia");
            System.out.println("4. Asignar vehiculo");
            System.out.println("5. Generar lista de pasajeros");
            System.out.println("6. Exportar lista de pasajeros");
            System.out.println("7. Iniciar salida");
            System.out.println("8. Finalizar salida");
            System.out.println("9. Cerrar salida y liquidar al guia");
            System.out.println("0. Volver");

            opcion = entrada.leerOpcion("Opcion: ", 0, 9);

            try {
                switch (opcion) {
                    case 1:
                        listarSalidas();
                        break;
                    case 2:
                        programarSalida();
                        break;
                    case 3:
                        asignarGuia();
                        break;
                    case 4:
                        asignarVehiculo();
                        break;
                    case 5:
                        generarLista();
                        break;
                    case 6:
                        exportarLista();
                        break;
                    case 7:
                        iniciarSalida();
                        break;
                    case 8:
                        finalizarSalida();
                        break;
                    case 9:
                        cerrarSalida();
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException
                     | TransicionEstadoInvalidaException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error al exportar: " + e.getMessage());
            }

        } while (opcion != 0);
    }

    private void listarSalidas() {
        List<Salida> salidas = controladorSalidas.listarSalidas();

        if (salidas.isEmpty()) {
            System.out.println("No hay salidas registradas.");
            return;
        }

        for (Salida salida : salidas) {
            System.out.println(
                    "Codigo: " + salida.getCodigo()
                            + " | Inicio: " + salida.getFechaInicio()
                            + " | Estado: " + salida.getEstado()
                            + " | Ocupacion: "
                            + salida.calcularOcupacion()
                            + "/" + salida.getCupoMaximo()
                            + " | Guia: " + (salida.getGuia() == null
                                    ? "sin asignar" : salida.getGuia().getNombre())
                            + " | Vehiculo: " + (salida.getVehiculo() == null
                                    ? "sin asignar" : salida.getVehiculo().getPatente())
            );
        }
    }

    private int leerCodigoSalida() {
        return entrada.leerOpcion(
                "Codigo de salida: ", 1, Integer.MAX_VALUE);
    }

    private void generarLista() {
        int codigo = leerCodigoSalida();

        controladorSalidas.generarListaPasajeros(codigo);
        System.out.println("Lista de pasajeros generada.");
    }

    private void exportarLista() throws IOException {
        int codigo = leerCodigoSalida();

        String nombre = "pasajeros_salida_" + codigo + ".txt";

        controladorSalidas.exportarListaPasajeros(
                codigo, Path.of(nombre));

        System.out.println("Archivo generado: " + nombre);
    }

    private void iniciarSalida() {
        int codigo = leerCodigoSalida();

        controladorSalidas.iniciarSalida(codigo);
        System.out.println("Salida iniciada correctamente.");
    }

    private void finalizarSalida() {
        int codigo = leerCodigoSalida();

        controladorSalidas.finalizarSalida(codigo);
        System.out.println("Salida finalizada correctamente.");
    }

    // CU03: programa una salida de un tour del catalogo
    private void programarSalida() {
        List<Tour> tours = controladorCatalogo.listarTours();
        if (tours.isEmpty()) {
            System.out.println("No hay tours en el catalogo.");
            return;
        }
        for (Tour tour : tours) {
            System.out.println(tour.getCodigo() + ". " + tour.getNombre()
                    + " (" + tour.getDuracionDias() + " dia(s), cupo "
                    + tour.getCupoMinimo() + " a " + tour.getCupoMaximo() + ")");
        }
        Tour tour = controladorCatalogo.buscarTour(
                entrada.leerOpcion("Codigo del tour: ", 1, Integer.MAX_VALUE));
        if (tour == null) {
            System.out.println("Error: no existe ese tour.");
            return;
        }
        LocalDate inicio = entrada.leerFecha("Fecha de inicio (dd/mm/aaaa): ");
        if (inicio.isBefore(LocalDate.now())) {
            System.out.println("Error: la fecha de inicio ya paso.");
            return;
        }
        if (tour.obtenerTarifaVigente(inicio) == null) {
            System.out.println("Error: el tour no tiene una tarifa vigente para esa fecha. "
                    + "Primero hay que cargarla desde el catalogo del administrador.");
            return;
        }
        Salida salida = controladorSalidas.programarSalida(tour, inicio);
        System.out.println("Salida " + salida.getCodigo() + " programada del "
                + salida.getFechaInicio() + " al " + salida.getFechaFin()
                + " (estado " + salida.getEstado() + ").");
    }

    // CU14: asigna un guia habilitado y disponible
    private void asignarGuia() {
        int codigo = leerCodigoSalida();
        List<Guia> guias = controladorSalidas.listarGuias();
        if (guias.isEmpty()) {
            System.out.println("No hay guias cargados.");
            return;
        }
        for (Guia guia : guias) {
            System.out.println("Legajo " + guia.getLegajo() + " - " + guia.getNombre()
                    + " | habilitado para " + guia.getCategoriasHabilitadas());
        }
        int legajo = entrada.leerOpcion("Legajo del guia: ", 1, Integer.MAX_VALUE);
        controladorSalidas.asignarGuia(codigo, legajo);
        System.out.println("Guia asignado a la salida " + codigo + ".");
    }

    // CU14: asigna un vehiculo con capacidad suficiente
    private void asignarVehiculo() {
        int codigo = leerCodigoSalida();
        List<Vehiculo> vehiculos = controladorSalidas.listarVehiculos();
        if (vehiculos.isEmpty()) {
            System.out.println("No hay vehiculos cargados.");
            return;
        }
        for (Vehiculo vehiculo : vehiculos) {
            System.out.println(vehiculo.getPatente() + " - " + vehiculo.getTipo()
                    + " - capacidad " + vehiculo.getCapacidad());
        }
        String patente = entrada.leerTexto("Patente: ");
        controladorSalidas.asignarVehiculo(codigo, patente);
        System.out.println("Vehiculo asignado a la salida " + codigo + ".");
    }

    // CU17: cierra una salida finalizada y liquida los honorarios del guia
    private void cerrarSalida() throws TransicionEstadoInvalidaException {
        int codigo = leerCodigoSalida();
        Liquidacion liquidacion = controladorSalidas.cerrarSalida(codigo);
        System.out.println("Liquidacion del guia " + liquidacion.getGuia().getNombre() + ":");
        System.out.printf("  Dias trabajados: %d%n", liquidacion.getDiasTrabajados());
        System.out.printf("  Honorario fijo: $%.2f%n", liquidacion.getImporteFijo());
        System.out.printf("  Comision por adicionales (5 %%): $%.2f%n", liquidacion.getComisionAdicionales());
        System.out.printf("  Total a pagar: $%.2f%n", liquidacion.getTotal());
    }
}
