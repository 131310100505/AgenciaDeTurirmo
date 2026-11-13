package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorCatalogo;
import ar.edu.uade.rumbosur.controlador.ControladorReportes;
import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.Vendedor;

import java.util.ArrayList;
import java.util.List;

/**
 * Menú de entrada del sistema: el usuario elige con qué actor trabajar.
 * Cada actor tiene su propia vista (VistaVendedor, VistaCoordinador, VistaAdministrador).
 */
public class MenuPrincipal {

    private EntradaConsola entrada;
    private ControladorReservas controladorReservas;
    private ControladorSalidas controladorSalidas;
    private ControladorCatalogo controladorCatalogo;
    private List<Vendedor> vendedores;
    private List<ServicioAdicional> catalogoAdicionales;

    public MenuPrincipal(EntradaConsola entrada, ControladorReservas controladorReservas,
                         ControladorSalidas controladorSalidas, ControladorCatalogo controladorCatalogo,
                         List<Vendedor> vendedores, List<ServicioAdicional> catalogoAdicionales) {
        this.entrada = entrada;
        this.controladorReservas = controladorReservas;
        this.controladorSalidas = controladorSalidas;
        this.controladorCatalogo = controladorCatalogo;
        this.vendedores = new ArrayList<>(vendedores);
        this.catalogoAdicionales = new ArrayList<>(catalogoAdicionales);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println();
            System.out.println("========== RUMBO SUR - Agencia de turismo ==========");
            System.out.println("1. Vendedor");
            System.out.println("2. Coordinador de operaciones");
            System.out.println("3. Administrador");
            System.out.println("4. Crear reserva con ventana (interfaz gráfica)");
            System.out.println("0. Guardar y salir");
            opcion = entrada.leerOpcion("Opción: ", 0, 4);
            switch (opcion) {
                case 1:
                    ingresarComoVendedor();
                    break;
                case 2:
                    new VistaCoordinador(entrada, controladorSalidas, controladorCatalogo)
                            .mostrarMenu();
                    break;
                case 3:
                    ingresarComoAdministrador();
                    break;
                case 4:
                    abrirVentanaReserva();
                    break;
                default:
                    break; // 0: sale y Principal guarda los datos
            }
        } while (opcion != 0);
    }

    /** El administrador elige entre los reportes (CU18) y el catálogo de tours (CU01 y CU02). */
    private void ingresarComoAdministrador() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- Administrador ---");
            System.out.println("1. Reportes");
            System.out.println("2. Catálogo de tours (tours, itinerarios y tarifas)");
            System.out.println("0. Volver");
            opcion = entrada.leerOpcion("Opción: ", 0, 2);
            if (opcion == 1) {
                ControladorReportes controladorReportes =
                        new ControladorReportes(controladorReservas, controladorSalidas);
                new VistaAdministrador(controladorReportes, entrada).mostrarMenu();
            } else if (opcion == 2) {
                new VistaCatalogo(entrada, controladorCatalogo).mostrarMenu();
            }
        } while (opcion != 0);
    }

    private void ingresarComoVendedor() {
        Vendedor vendedor = elegirVendedor();
        if (vendedor != null) {
            new VistaVendedor(entrada, controladorReservas, controladorSalidas, vendedor, catalogoAdicionales)
                    .mostrarMenu();
        }
    }

    private Vendedor elegirVendedor() {
        if (vendedores.isEmpty()) {
            System.out.println("No hay vendedores cargados.");
            return null;
        }
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor vendedor = vendedores.get(i);
            System.out.println((i + 1) + ". " + vendedor.getNombre() + " (legajo " + vendedor.getLegajo() + ")");
        }
        return vendedores.get(entrada.leerOpcion("¿Quién atiende? ", 1, vendedores.size()) - 1);
    }

    private void abrirVentanaReserva() {
        if (vendedores.isEmpty()) {
            System.out.println("No hay vendedores cargados.");
            return;
        }
        VentanaReserva.abrir(controladorReservas, controladorSalidas, vendedores);
        System.out.println("Se abrió la ventana de reservas. Puede seguir usando este menú.");
    }
}
