package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.ReservaNoCancelableException;
import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.MedioPago;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU12 Cancelar reserva y CU13 Controlar vencimientos, con fechas fijas. */
class ControladorReservasPagosTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate INICIO_SALIDA = LocalDate.of(2027, 3, 15);
    // El saldo tiene que estar pago 10 días antes del inicio.
    private static final LocalDate FECHA_LIMITE = LocalDate.of(2027, 3, 5);

    private ControladorReservas controlador;
    private Salida salida;
    private Cliente cliente;
    private Vendedor vendedor;
    private Pasajero pasajero;

    @BeforeEach
    void prepararDatos() {
        ServicioAdicional seguro = new ServicioAdicional(1, "Seguro", 8000, TipoAdicional.SEGURO);
        controlador = new ControladorReservas(new CalculadoraPrecio(List.of()), seguro);
        TourCultural tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 1, 10);
        tour.agregarTarifa(new Tarifa(1, Temporada.ALTA, INICIO_SALIDA.minusYears(1), INICIO_SALIDA.plusYears(1), 1000));
        salida = new Salida(1, INICIO_SALIDA, INICIO_SALIDA.plusDays(2), 1, 10, tour);
        cliente = new Cliente(1, "Ana", "Pérez", "30111222", "1155550000", "ana@mail.com", "Argentina");
        vendedor = new Vendedor(1, "Luis", "28333444", "1155551111", 0.05);
        pasajero = new Pasajero(1, "Ana Pérez", "30111222", LocalDate.of(1990, 5, 20),
                TipoPasajero.ADULTO, "Contacto", "");
    }

    private Reserva crearReservaConPago(double importePagado) {
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        reserva.registrarPago(crearPago(importePagado));
        return reserva;
    }

    /** controlarVencimientos recorre las reservas del controlador: hay que crearlas con él. */
    private Reserva crearReservaEnControlador(double total, double importePagado) throws Exception {
        Reserva reserva = controlador.solicitarCrearReserva(cliente, salida, vendedor, List.of(pasajero));
        reserva.setTotal(total);
        if (importePagado > 0) {
            reserva.registrarPago(crearPago(importePagado));
        }
        return reserva;
    }

    private Pago crearPago(double importe) {
        return new Pago(1, LocalDate.of(2027, 1, 10), importe, MedioPago.EFECTIVO, 1);
    }

    // ---------- CU12 Cancelar reserva ----------

    @Test
    void clienteConTreintaDiasRecibeElNoventaPorCiento() throws Exception {
        Reserva reserva = crearReservaConPago(1000);
        LocalDate fecha = INICIO_SALIDA.minusDays(30);

        Cancelacion cancelacion = controlador.cancelarReserva(reserva, "Cambio de planes",
                OrigenCancelacion.CLIENTE, fecha);

        assertEquals(900, cancelacion.getImporteDevuelto(), MARGEN);
        assertEquals(fecha, cancelacion.getFecha());
        assertEquals("Cambio de planes", cancelacion.getMotivo());
        assertEquals(OrigenCancelacion.CLIENTE, cancelacion.getOrigen());
    }

    @Test
    void laDevolucionDependeDeLaFechaRecibida() throws Exception {
        Cancelacion cancelacion = controlador.cancelarReserva(crearReservaConPago(1000), "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA.minusDays(14));
        assertEquals(200, cancelacion.getImporteDevuelto(), MARGEN);
    }

    @Test
    void agenciaDevuelveTodoAunqueFalteUnDia() throws Exception {
        Cancelacion cancelacion = controlador.cancelarReserva(crearReservaConPago(1000), "Sin cupo mínimo",
                OrigenCancelacion.AGENCIA, INICIO_SALIDA.minusDays(1));
        assertEquals(1000, cancelacion.getImporteDevuelto(), MARGEN);
    }

    @Test
    void laCancelacionConsideraLosAjustes() throws Exception {
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        Pago pago = crearPago(1000);
        controlador.registrarPago(reserva, pago);
        controlador.registrarAjuste(reserva, pago,
                new AjustePago(1, LocalDate.of(2027, 1, 11), -200, "Error de carga"));

        Cancelacion cancelacion = controlador.cancelarReserva(reserva, "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA.minusDays(30));

        assertEquals(720, cancelacion.getImporteDevuelto(), MARGEN);
    }

    @Test
    void sinPagosNoSeDevuelveNadaPeroSeCancela() throws Exception {
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        Cancelacion cancelacion = controlador.cancelarReserva(reserva, "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA.minusDays(30));
        assertEquals(0, cancelacion.getImporteDevuelto(), MARGEN);
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
    }

    @Test
    void cancelarAnulaLaReservaYLiberaElCupo() throws Exception {
        Reserva reserva = crearReservaConPago(1000);
        controlador.cancelarReserva(reserva, "Cambio de planes", OrigenCancelacion.CLIENTE,
                INICIO_SALIDA.minusDays(30));
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
        assertEquals("Cambio de planes", reserva.getMotivoAnulacion());
        assertFalse(reserva.ocupaCupo());
    }

    @Test
    void laCancelacionQuedaRegistradaYLosPagosSeConservan() throws Exception {
        Reserva reserva = crearReservaConPago(1000);
        Cancelacion cancelacion = controlador.cancelarReserva(reserva, "Motivo", OrigenCancelacion.CLIENTE,
                INICIO_SALIDA.minusDays(30));
        assertSame(cancelacion, controlador.getCancelaciones().get(reserva.getNumero()));
        assertEquals(1, reserva.getPagos().size());
        assertEquals(1000, reserva.calcularTotalPagado(), MARGEN);
    }

    @Test
    void elDiaDeInicioSeCancelaSinDevolucion() throws Exception {
        Reserva reserva = crearReservaConPago(1000);
        Cancelacion cancelacion = controlador.cancelarReserva(reserva, "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA);
        assertEquals(0, cancelacion.getImporteDevuelto(), MARGEN);
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
    }

    @Test
    void noSePuedeCancelarDespuesDelInicio() {
        Reserva reserva = crearReservaConPago(1000);
        assertThrows(ReservaNoCancelableException.class, () -> controlador.cancelarReserva(reserva, "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA.plusDays(1)));
        assertTrue(reserva.ocupaCupo());
        assertTrue(controlador.getCancelaciones().isEmpty());
    }

    @Test
    void noSePuedeCancelarDosVeces() throws Exception {
        Reserva reserva = crearReservaConPago(1000);
        LocalDate fecha = INICIO_SALIDA.minusDays(30);
        Cancelacion primera = controlador.cancelarReserva(reserva, "Motivo", OrigenCancelacion.CLIENTE, fecha);

        assertThrows(ReservaNoCancelableException.class, () -> controlador.cancelarReserva(reserva, "Otro",
                OrigenCancelacion.AGENCIA, fecha));
        assertSame(primera, controlador.getCancelaciones().get(reserva.getNumero()));
    }

    @Test
    void noSePuedeCancelarUnaReservaInexistente() {
        assertThrows(ReservaNoCancelableException.class, () -> controlador.cancelarReserva(null, "Motivo",
                OrigenCancelacion.CLIENTE, INICIO_SALIDA.minusDays(30)));
    }

    // ---------- CU13 Controlar vencimientos ----------

    @Test
    void pasadaLaFechaLimiteConSaldoSeAnula() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 300);
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
        assertTrue(reserva.getMotivoAnulacion().contains("Saldo no abonado"));
    }

    @Test
    void elDiaLimiteTodaviaEstaVigente() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 300);
        controlador.controlarVencimientos(FECHA_LIMITE);
        assertTrue(reserva.ocupaCupo());
    }

    @Test
    void antesDeLaFechaLimiteNoSeAnula() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 0);
        controlador.controlarVencimientos(FECHA_LIMITE.minusDays(20));
        assertTrue(reserva.ocupaCupo());
    }

    @Test
    void unaReservaPagadaNoSeAnula() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 1000);
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        assertTrue(reserva.ocupaCupo());
    }

    @Test
    void unaDiferenciaMenorAUnCentavoNoSeConsideraDeuda() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 999.995);
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        assertTrue(reserva.ocupaCupo());
    }

    @Test
    void unAjusteQueGeneraDeudaHaceVencerLaReserva() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 1000);
        controlador.registrarAjuste(reserva, reserva.getPagos().get(0),
                new AjustePago(1, LocalDate.of(2027, 1, 11), -200, "Error de carga"));
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
    }

    @Test
    void soloAnulaLasReservasVencidas() throws Exception {
        Reserva conDeuda = crearReservaEnControlador(1000, 300);
        Reserva pagada = crearReservaEnControlador(1000, 1000);
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        assertEquals(EstadoReserva.ANULADA, conDeuda.getEstado());
        assertTrue(pagada.ocupaCupo());
    }

    @Test
    void controlarDosVecesNoFallaConLasYaAnuladas() throws Exception {
        Reserva reserva = crearReservaEnControlador(1000, 300);
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(1));
        String motivo = reserva.getMotivoAnulacion();
        controlador.controlarVencimientos(FECHA_LIMITE.plusDays(2));
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
        assertEquals(motivo, reserva.getMotivoAnulacion());
    }
}
