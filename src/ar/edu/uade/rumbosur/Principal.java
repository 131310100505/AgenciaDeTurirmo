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
 * Arranque de la aplicación: crea los objetos, los conecta y abre el menú.
 */
public class Principal {

    public static void main(String[] args) {
        Repositorio repositorio = new Repositorio();
        boolean habiaDatos = repositorio.cargarDatos();
        if (habiaDatos) {
            System.out.println("Datos cargados del archivo.");
        } else {
            DatosIniciales.cargarEn(repositorio);
        }

        // Promociones de los requisitos: jubilado 10 %, anticipada 15 %, grupo 10 %
        List<Promocion> promociones = List.of(
                new Promocion(1, "Jubilados", TipoPromocion.JUBILADO, 0.10),
                new Promocion(2, "Reserva anticipada (más de 60 días)", TipoPromocion.ANTICIPADA, 0.15),
                new Promocion(3, "Grupo de 6 o más pasajeros", TipoPromocion.GRUPO, 0.10));

        // Servicios adicionales (precios de ejemplo). El primero es el seguro obligatorio de aventura.
        List<ServicioAdicional> catalogoAdicionales = List.of(
                new ServicioAdicional(1, "Seguro de aventura", 15000, TipoAdicional.SEGURO),
                new ServicioAdicional(2, "Traslado aeropuerto - hotel", 12000, TipoAdicional.TRASLADO),
                new ServicioAdicional(3, "Excursión opcional", 25000, TipoAdicional.EXCURSION));

        ControladorReservas controladorReservas =
                new ControladorReservas(new CalculadoraPrecio(promociones), catalogoAdicionales.get(0));
        ControladorSalidas controladorSalidas = new ControladorSalidas();
        ControladorCatalogo controladorCatalogo = new ControladorCatalogo();

        controladorReservas.cargarDesde(repositorio);
        controladorSalidas.cargarDesde(repositorio);
        controladorCatalogo.cargarDesde(repositorio);

        if (!habiaDatos) {
            DatosIniciales.cargarReservaPrueba(repositorio, controladorReservas);
        }

        // CU13: anula las reservas con el saldo vencido
        controladorReservas.controlarVencimientos();

        // Cupo mínimo: cancela las salidas que a 7 días o menos no lo alcanzan
        Map<Integer, Map<Integer, Double>> canceladas = controladorSalidas.controlarCupoMinimo(LocalDate.now());
        for (Integer codigoSalida : canceladas.keySet()) {
            System.out.println("Salida " + codigoSalida + " cancelada por no alcanzar el cupo mínimo.");
        }

        EntradaConsola entrada = new EntradaConsola(new Scanner(System.in));
        MenuPrincipal menu = new MenuPrincipal(entrada, controladorReservas, controladorSalidas,
                controladorCatalogo, repositorio.listarVendedores(), catalogoAdicionales);
        try {
            menu.iniciar();
        } finally {
            // Se guardan los datos aunque el programa termine por un error
            controladorReservas.guardarEn(repositorio);
            controladorSalidas.guardarEn(repositorio);
            controladorCatalogo.guardarEn(repositorio);
            repositorio.guardarDatos();
            entrada.cerrar();
            System.out.println("Datos guardados. ¡Hasta luego!");
            System.exit(0);
        }
    }
}
