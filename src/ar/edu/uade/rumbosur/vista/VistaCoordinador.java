
package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.modelo.Salida;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VistaCoordinador {

    private final EntradaConsola entrada;
    private final ControladorSalidas controladorSalidas;

    public VistaCoordinador(EntradaConsola entrada,
                            ControladorSalidas controladorSalidas) {
        this.entrada = entrada;
        this.controladorSalidas = controladorSalidas;
    }

    public void mostrarMenu() {
        int opcion;

        do {
            System.out.println();
            System.out.println("===== COORDINADOR DE OPERACIONES =====");
            System.out.println("1. Listar salidas");
            System.out.println("2. Consultar ocupacion");
            System.out.println("3. Generar lista de pasajeros");
            System.out.println("4. Exportar lista de pasajeros");
            System.out.println("5. Iniciar salida");
            System.out.println("6. Finalizar salida");
            System.out.println("7. Controlar cupo minimo");
            System.out.println("0. Volver");

            opcion = entrada.leerOpcion("Opcion: ", 0, 7);

            try {
                switch (opcion) {
                    case 1:
                        listarSalidas();
                        break;
                    case 2:
                        consultarOcupacion();
                        break;
                    case 3:
                        generarLista();
                        break;
                    case 4:
                        exportarLista();
                        break;
                    case 5:
                        iniciarSalida();
                        break;
                    case 6:
                        finalizarSalida();
                        break;
                    case 7:
                        controlarCupoMinimo();
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException |
                     IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println(
                        "Error al exportar: " + e.getMessage());
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
            );
        }
    }

    private int leerCodigoSalida() {
        return entrada.leerOpcion(
                "Codigo de salida: ", 1, Integer.MAX_VALUE);
    }

    private void consultarOcupacion() {
        int codigo = leerCodigoSalida();

        System.out.println(
                "Ocupacion actual: "
                        + controladorSalidas.consultarOcupacion(codigo)
        );
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

    private void controlarCupoMinimo() {
        Map<Integer, Map<Integer, Double>> resultado =
                controladorSalidas.controlarCupoMinimo(
                        LocalDate.now());

        if (resultado.isEmpty()) {
            System.out.println(
                    "No hay salidas para cancelar por cupo minimo.");
            return;
        }

        for (Map.Entry<Integer, Map<Integer, Double>> item
                : resultado.entrySet()) {
            System.out.println(
                    "Salida cancelada: " + item.getKey());

            for (Map.Entry<Integer, Double> devolucion
                    : item.getValue().entrySet()) {
                System.out.println(
                        "Reserva " + devolucion.getKey()
                                + " | Devolucion: $"
                                + devolucion.getValue());
            }
        }
    }
}
