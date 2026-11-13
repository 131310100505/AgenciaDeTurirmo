package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.EstadoSalida;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.MedioPago;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.TipoVehiculo;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vehiculo;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU03 Programar salida, CU14 Asignar guía y vehículo, cupo mínimo y fin de las reservas (CU16). */
class ControladorSalidasOperacionTest {

    private static final LocalDate INICIO = LocalDate.of(2027, 3, 15);

    private ControladorSalidas controlador;
    private TourCultural tour;
    private Guia guia;

    @BeforeEach
    void prepararDatos() {
        controlador = new ControladorSalidas();
        tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 2, 10);
        guia = new Guia(1, "Marta", "444", "1155553333", List.of("Español"), Set.of(CategoriaTour.CULTURAL), 500);
        controlador.registrarGuia(guia);
        controlador.registrarVehiculo(new Vehiculo("AB123CD", 20, TipoVehiculo.COMBI));
    }

    private Reserva reservaConfirmada(Salida salida, int cantidadPasajeros) {
        Cliente cliente = new Cliente(1, "Ana", "Pérez", "111", "1155550000", "ana@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(2, "Luis", "222", "1155551111", 0.05);
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        for (int i = 0; i < cantidadPasajeros; i++) {
            reserva.agregarPasajero(new Pasajero(i, "Pasajero " + i, "DOC" + i, LocalDate.of(1990, 1, 1),
                    TipoPasajero.ADULTO, "Contacto", ""));
        }
        reserva.setTotal(1000);
        reserva.registrarPago(new Pago(1, INICIO.minusDays(60), 1000, MedioPago.EFECTIVO, 1));
        assertTrue(reserva.confirmar());
        salida.agregarReserva(reserva);
        return reserva;
    }

    @Test
    void programarSalidaUsaLaDuracionYLosCuposDelTour() {
        Salida primera = controlador.programarSalida(tour, INICIO);
        Salida segunda = controlador.programarSalida(tour, INICIO.plusDays(10));

        assertEquals(INICIO.plusDays(2), primera.getFechaFin());
        assertEquals(2, primera.getCupoMinimo());
        assertEquals(10, primera.getCupoMaximo());
        assertEquals(EstadoSalida.PROGRAMADA, primera.getEstado());
        assertEquals(primera.getCodigo() + 1, segunda.getCodigo());
    }

    @Test
    void asignaGuiaPorLegajoYVehiculoPorPatente() {
        Salida salida = controlador.programarSalida(tour, INICIO);

        controlador.asignarGuia(salida.getCodigo(), 1);
        controlador.asignarVehiculo(salida.getCodigo(), "ab123cd");

        assertSame(guia, salida.getGuia());
        assertEquals("AB123CD", salida.getVehiculo().getPatente());
    }

    @Test
    void rechazaGuiaOVehiculoInexistentes() {
        Salida salida = controlador.programarSalida(tour, INICIO);

        assertThrows(IllegalArgumentException.class, () -> controlador.asignarGuia(salida.getCodigo(), 99));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.asignarVehiculo(salida.getCodigo(), "ZZZ999"));
    }

    @Test
    void cancelaPorCupoMinimoAunqueFaltenMenosDeSieteDias() {
        Salida salida = controlador.programarSalida(tour, INICIO);
        reservaConfirmada(salida, 1); // cupo mínimo 2

        // Faltan 5 días: el control no se hizo justo a los 7, pero igual corresponde cancelar
        Map<Integer, Map<Integer, Double>> canceladas = controlador.controlarCupoMinimo(INICIO.minusDays(5));

        assertTrue(canceladas.containsKey(salida.getCodigo()));
        assertEquals(EstadoSalida.CANCELADA, salida.getEstado());
    }

    @Test
    void noCancelaSiFaltanMasDeSieteDias() {
        Salida salida = controlador.programarSalida(tour, INICIO);
        reservaConfirmada(salida, 1);

        assertFalse(controlador.debeCancelarPorCupoMinimo(salida.getCodigo(), INICIO.minusDays(8)));
    }

    @Test
    void alFinalizarLaSalidaSusReservasQuedanFinalizadas() {
        Salida salida = controlador.programarSalida(tour, INICIO);
        Reserva reserva = reservaConfirmada(salida, 2);
        controlador.asignarGuia(salida.getCodigo(), 1);
        controlador.asignarVehiculo(salida.getCodigo(), "AB123CD");
        controlador.generarListaPasajeros(salida.getCodigo());
        controlador.iniciarSalida(salida.getCodigo());

        controlador.finalizarSalida(salida.getCodigo());

        assertEquals(EstadoReserva.FINALIZADA, reserva.getEstado());
        // Los pasajeros siguen contando en la ocupación de la salida (reporte de ocupación)
        assertEquals(2, salida.calcularOcupacion());
    }
}
