package ar.edu.uade.rumbosur;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.modelo.Promocion;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.TipoAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPromocion;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import ar.edu.uade.rumbosur.vista.EntradaConsola;
import ar.edu.uade.rumbosur.vista.MenuPrincipal;

import java.util.List;
import java.util.Scanner;

/**
 * Arranque de la aplicación (como "Principal" del material de MVC):
 * crea los objetos, los conecta y abre el menú.
 */
public class Principal {

    public static void main(String[] args) {
        Repositorio repositorio = new Repositorio();
        if (repositorio.cargarDatos()) {
            System.out.println("Datos cargados del archivo.");
        } else {
            System.out.println("No había datos guardados: se empieza con el sistema vacío.");
            // Germán: acá se cargan los datos de prueba, por ejemplo DatosIniciales.cargarEn(repositorio);
        }

        List<ServicioAdicional> catalogoAdicionales = crearCatalogoAdicionales();
        ControladorReservas controladorReservas = new ControladorReservas(
                new CalculadoraPrecio(crearPromociones()), catalogoAdicionales.get(0));
        ControladorSalidas controladorSalidas = new ControladorSalidas();
        controladorReservas.cargarDesde(repositorio);
        controladorSalidas.cargarDesde(repositorio);

        // CU13: el control de vencimientos lo hace el sistema solo, al iniciar.
        controladorReservas.controlarVencimientos();

        EntradaConsola entrada = new EntradaConsola(new Scanner(System.in));
        MenuPrincipal menu = new MenuPrincipal(entrada, controladorReservas, controladorSalidas,
                repositorio.listarVendedores(), catalogoAdicionales);
        try {
            menu.iniciar();
        } finally {
            // finally: los datos se guardan aunque el programa termine por un error.
            controladorReservas.guardarEn(repositorio);
            controladorSalidas.guardarEn(repositorio);
            repositorio.guardarDatos();
            entrada.cerrar();
            System.out.println("Datos guardados. ¡Hasta luego!");
            System.exit(0); // cierra también la ventana de reservas, si quedó abierta
        }
    }

    /** Promociones de los requisitos (supuestos S13 a S15). */
    private static List<Promocion> crearPromociones() {
        return List.of(
                new Promocion(1, "Jubilados", TipoPromocion.JUBILADO, 0.10),
                new Promocion(2, "Reserva anticipada (más de 60 días)", TipoPromocion.ANTICIPADA, 0.15),
                new Promocion(3, "Grupo de 6 o más pasajeros", TipoPromocion.GRUPO, 0.10));
    }

    /**
     * Servicios adicionales que ofrece la agencia. El primero es el seguro obligatorio
     * de los tours de aventura. Los precios son de ejemplo: el enunciado no los define.
     */
    private static List<ServicioAdicional> crearCatalogoAdicionales() {
        return List.of(
                new ServicioAdicional(1, "Seguro de aventura", 15000, TipoAdicional.SEGURO),
                new ServicioAdicional(2, "Traslado aeropuerto - hotel", 12000, TipoAdicional.TRASLADO),
                new ServicioAdicional(3, "Excursión opcional", 25000, TipoAdicional.EXCURSION));
    }
}
