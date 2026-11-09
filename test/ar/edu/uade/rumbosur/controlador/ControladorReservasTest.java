package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.TourAventura;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU05 Crear reserva, tal como está en el diagrama de secuencia. */
class ControladorReservasTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate HOY = LocalDate.now();

    private ControladorReservas controlador;
    private Salida salida;
    private Cliente cliente;
    private Vendedor vendedor;

    @BeforeEach
    void prepararDatos() {
        ServicioAdicional seguro = new ServicioAdicional(1, "Seguro de aventura", 8000, TipoAdicional.SEGURO);
        controlador = new ControladorReservas(new CalculadoraPrecio(List.of()), seguro);
        TourAventura tour = new TourAventura(1, "Trekking Fitz Roy", "Laguna de los Tres", 2, 1, 2, 16, "Alta");
        tour.agregarTarifa(new Tarifa(1, Temporada.ALTA, HOY, HOY.plusDays(365), 100000));
        salida = new Salida(1, HOY.plusDays(30), HOY.plusDays(31), 1, 2, tour);
        cliente = new Cliente(1, "Ana", "Pérez", "30111222", "1155550000", "ana@mail.com", "Argentina");
        vendedor = new Vendedor(1, "Luis", "28333444", "1155551111", 0.05);
    }

    private Pasajero pasajero(String documento, int edad) {
        return new Pasajero(1, "Pasajero " + documento, documento, HOY.minusYears(edad),
                TipoPasajero.ADULTO, "Contacto", "");
    }

    @Test
    void creaLaReservaPendienteConSeguroObligatorio() throws Exception {
        Reserva reserva = controlador.solicitarCrearReserva(cliente, salida, vendedor,
                List.of(pasajero("1", 30), pasajero("2", 25)));

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertEquals(2, reserva.getAdicionales().size());          // un seguro por pasajero
        assertEquals(2 * 100000 + 2 * 8000, reserva.getTotal(), MARGEN);
        assertTrue(salida.getReservas().contains(reserva));         // ocupa lugares en la salida
        assertEquals(reserva, controlador.buscarReserva(reserva.getNumero()));
    }

    @Test
    void sinCupoSeRechazaLaReserva() {
        // El cupo máximo de la salida es 2.
        assertThrows(CupoInsuficienteException.class, () -> controlador.solicitarCrearReserva(cliente, salida,
                vendedor, List.of(pasajero("1", 30), pasajero("2", 30), pasajero("3", 30))));
        assertTrue(cliente.getReservas().isEmpty());
    }

    @Test
    void siUnPasajeroNoTieneLaEdadMinimaNoSeCreaLaReserva() {
        // Edad mínima del tour: 16 años.
        assertThrows(PasajeroNoHabilitadoException.class, () -> controlador.solicitarCrearReserva(cliente,
                salida, vendedor, List.of(pasajero("1", 30), pasajero("2", 14))));
        assertTrue(cliente.getReservas().isEmpty());
    }

    @Test
    void noSeRegistraDosVecesElMismoCliente() {
        controlador.registrarCliente(cliente);
        assertThrows(IllegalArgumentException.class, () -> controlador.registrarCliente(cliente));
    }
}
