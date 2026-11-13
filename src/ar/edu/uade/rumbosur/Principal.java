
package ar.edu.uade.rumbosur;

import ar.edu.uade.rumbosur.controlador.ControladorCatalogo;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Arranque de la aplicación:
 * crea los objetos, los conecta y abre el menú.
 */
public class Principal {

    public static void main(String[] args) {

        Repositorio repositorio = new Repositorio();

        if (repositorio.cargarDatos()) {
            System.out.println("Datos cargados del archivo.");
        } else {
            System.out.println("No había datos guardados.");
        }

        // Carga solamente los datos iniciales que no existen.
        DatosIniciales.cargarEn(repositorio);

        List<ServicioAdicional> catalogoAdicionales =
                crearCatalogoAdicionales();

        ControladorReservas controladorReservas =
                new ControladorReservas(
                        new CalculadoraPrecio(crearPromociones()),
                        catalogoAdicionales.get(0)
                );

        ControladorSalidas controladorSalidas =
                new ControladorSalidas();

        ControladorCatalogo controladorCatalogo =
                new ControladorCatalogo();

        // Recuperar datos guardados.
        controladorReservas.cargarDesde(repositorio);
        controladorSalidas.cargarDesde(repositorio);
        controladorCatalogo.cargarDesde(repositorio);

        // Crear una sola vez la reserva de prueba de la salida 101.
        DatosIniciales.cargarReservaPrueba(
                repositorio,
                controladorReservas
        );

        // CU13: controlar vencimientos al iniciar.
        controladorReservas.controlarVencimientos();

        // Cupo minimo: se cancelan las salidas que a 7 dias o menos no lo alcanzan.
        informarCancelacionesPorCupoMinimo(
                controladorSalidas.controlarCupoMinimo(LocalDate.now()));

        EntradaConsola entrada =
                new EntradaConsola(new Scanner(System.in));

        MenuPrincipal menu = new MenuPrincipal(
                entrada,
                controladorReservas,
                controladorSalidas,
                controladorCatalogo,
                repositorio.listarVendedores(),
                catalogoAdicionales
        );

        try {
            menu.iniciar();
        } finally {

            // Guardar los datos al terminar.
            controladorReservas.guardarEn(repositorio);
            controladorSalidas.guardarEn(repositorio);
            controladorCatalogo.guardarEn(repositorio);

            repositorio.guardarDatos();

            entrada.cerrar();

            System.out.println("Datos guardados. ¡Hasta luego!");

            System.exit(0);
        }
    }

    private static void informarCancelacionesPorCupoMinimo(
            Map<Integer, Map<Integer, Double>> canceladas) {

        for (Map.Entry<Integer, Map<Integer, Double>> salida : canceladas.entrySet()) {
            System.out.println("Salida " + salida.getKey()
                    + " cancelada por no alcanzar el cupo minimo. Reservas a reintegrar (100 %): "
                    + salida.getValue());
        }
    }

    /**
     * Promociones de los requisitos (S13 a S15).
     */
    private static List<Promocion> crearPromociones() {

        return List.of(
                new Promocion(
                        1, "Jubilados",
                        TipoPromocion.JUBILADO, 0.10
                ),
                new Promocion(
                        2, "Reserva anticipada (más de 60 días)",
                        TipoPromocion.ANTICIPADA, 0.15
                ),
                new Promocion(
                        3, "Grupo de 6 o más pasajeros",
                        TipoPromocion.GRUPO, 0.10
                )
        );
    }

    /**
     * Servicios adicionales de la agencia.
     * El primero es el seguro obligatorio de aventura.
     */
    private static List<ServicioAdicional> crearCatalogoAdicionales() {

        return List.of(
                new ServicioAdicional(
                        1, "Seguro de aventura",
                        15000, TipoAdicional.SEGURO
                ),
                new ServicioAdicional(
                        2, "Traslado aeropuerto - hotel",
                        12000, TipoAdicional.TRASLADO
                ),
                new ServicioAdicional(
                        3, "Excursión opcional",
                        25000, TipoAdicional.EXCURSION
                )
        );
    }
}
