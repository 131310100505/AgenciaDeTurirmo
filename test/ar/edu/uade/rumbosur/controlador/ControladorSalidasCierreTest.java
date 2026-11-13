package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.TransicionEstadoInvalidaException;
import ar.edu.uade.rumbosur.modelo.AdicionalContratado;
import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Liquidacion;
import ar.edu.uade.rumbosur.modelo.MedioPago;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.TipoAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.TipoVehiculo;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vehiculo;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU17 Cerrar salida y liquidar guía. */
class ControladorSalidasCierreTest {

    private static final double MARGEN = 0.001;
    private static final int CODIGO_SALIDA = 7;
    private static final LocalDate INICIO_SALIDA = LocalDate.of(2027, 3, 15);

    private ControladorSalidas controlador;
    private Salida salida;
    private Guia guia;

    @BeforeEach
    void prepararDatos() {
        controlador = new ControladorSalidas();
        TourCultural tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 1, 10);
        // Salida de 3 días con un guía que cobra $500 por día.
        salida = controlador.crearSalida(CODIGO_SALIDA, INICIO_SALIDA, INICIO_SALIDA.plusDays(2), 1, 10, tour);
        guia = new Guia(1, "Marta", "444", "1155553333", List.of("Español"), Set.of(CategoriaTour.CULTURAL), 500);

        Pasajero pasajero = new Pasajero(1, "Ana Pérez", "111", LocalDate.of(1990, 5, 20),
                TipoPasajero.ADULTO, "Contacto", "");
        Cliente cliente = new Cliente(1, "Ana", "Pérez", "111", "1155550000", "ana@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(2, "Luis", "222", "1155551111", 0.05);
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        reserva.agregarPasajero(pasajero);
        // $2000 en adicionales: la comisión del guía es el 5 %, o sea $100.
        reserva.agregarAdicional(new AdicionalContratado(1,
                new ServicioAdicional(1, "Excursión", 2000, TipoAdicional.EXCURSION), pasajero, 1));
        reserva.setTotal(1000);
        reserva.registrarPago(new Pago(1, INICIO_SALIDA.minusDays(60), 1000, MedioPago.EFECTIVO, 1));
        assertTrue(reserva.confirmar());
        salida.agregarReserva(reserva);
    }

    private void llevarSalidaHastaFinalizada() {
        controlador.registrarGuia(guia);
        controlador.registrarVehiculo(new Vehiculo("AB123CD", 20, TipoVehiculo.COMBI));
        controlador.asignarGuia(CODIGO_SALIDA, guia.getLegajo());
        controlador.asignarVehiculo(CODIGO_SALIDA, "AB123CD");
        controlador.generarListaPasajeros(CODIGO_SALIDA);
        controlador.iniciarSalida(CODIGO_SALIDA);
        controlador.finalizarSalida(CODIGO_SALIDA);
    }

    @Test
    void cerrarUnaSalidaFinalizadaLiquidaAlGuia() throws Exception {
        llevarSalidaHastaFinalizada();

        Liquidacion liquidacion = controlador.cerrarSalida(CODIGO_SALIDA);

        assertEquals(3, liquidacion.getDiasTrabajados());
        assertEquals(1500, liquidacion.getImporteFijo(), MARGEN);
        assertEquals(100, liquidacion.getComisionAdicionales(), MARGEN);
        assertEquals(1600, liquidacion.getTotal(), MARGEN);
        assertSame(guia, liquidacion.getGuia());
        assertSame(salida, liquidacion.getSalida());
    }

    @Test
    void noSePuedeCerrarUnaSalidaSinFinalizar() {
        assertThrows(TransicionEstadoInvalidaException.class, () -> controlador.cerrarSalida(CODIGO_SALIDA));
        assertTrue(controlador.listarLiquidaciones().isEmpty());
    }

    @Test
    void noSeLiquidaDosVecesLaMismaSalida() throws Exception {
        llevarSalidaHastaFinalizada();
        Liquidacion primera = controlador.cerrarSalida(CODIGO_SALIDA);

        assertThrows(TransicionEstadoInvalidaException.class, () -> controlador.cerrarSalida(CODIGO_SALIDA));
        assertEquals(1, controlador.listarLiquidaciones().size());
        assertSame(primera, controlador.listarLiquidaciones().get(0));
    }

    @Test
    void noSePuedeCerrarUnaSalidaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> controlador.cerrarSalida(999));
    }

    @Test
    void despuesDeCargarNoSeDuplicaLaLiquidacion() throws Exception {
        llevarSalidaHastaFinalizada();
        controlador.cerrarSalida(CODIGO_SALIDA);
        Repositorio repositorio = new Repositorio("no-se-usa.dat");
        controlador.guardarEn(repositorio);

        ControladorSalidas cargado = new ControladorSalidas();
        cargado.cargarDesde(repositorio);

        assertEquals(1, cargado.listarLiquidaciones().size());
        assertThrows(TransicionEstadoInvalidaException.class, () -> cargado.cerrarSalida(CODIGO_SALIDA));
    }
}
