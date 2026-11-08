package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.excepciones.PagoInvalidoException;
import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.MedioPago;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliticaCancelacionTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate INICIO_SALIDA = LocalDate.of(2027, 3, 15);

    private final PoliticaCancelacion politicaCliente = new PoliticaCancelacionCliente();
    private final PoliticaCancelacion politicaAgencia = new PoliticaCancelacionAgencia();

    private Reserva crearReserva() {
        TourCultural tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 2, 10);
        Salida salida = new Salida(1, INICIO_SALIDA, INICIO_SALIDA.plusDays(2), 2, 10, tour);
        Cliente cliente = new Cliente(1, "Ana", "Pérez", "30111222", "1155550000", "ana@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(1, "Luis", "28333444", "1155551111", 0.05);
        return new Reserva(cliente, salida, vendedor);
    }

    private Pago crearPago(double importe) {
        return new Pago(1, LocalDate.of(2026, 11, 8), importe, MedioPago.EFECTIVO, 1);
    }

    private LocalDate diasAntesDeLaSalida(int dias) {
        return INICIO_SALIDA.minusDays(dias);
    }

    @ParameterizedTest(name = "{0} días de anticipación devuelven ${1}")
    @CsvSource({
            "60, 900",
            "30, 900",
            "29, 500",
            "15, 500",
            "14, 200",
            "7, 200",
            "6, 0",
            "0, 0"
    })
    void clienteDevuelveSegunLosDiasDeAnticipacion(int dias, double devolucionEsperada) {
        Reserva reserva = crearReserva();
        reserva.registrarPago(crearPago(1000));
        assertEquals(devolucionEsperada,
                politicaCliente.calcularDevolucion(reserva, diasAntesDeLaSalida(dias)), MARGEN);
    }

    @ParameterizedTest(name = "agencia con {0} días de anticipación devuelve todo")
    @CsvSource({"60", "7", "0"})
    void agenciaDevuelveElCienPorCientoSiempre(int dias) {
        Reserva reserva = crearReserva();
        reserva.registrarPago(crearPago(1000));
        assertEquals(1000, politicaAgencia.calcularDevolucion(reserva, diasAntesDeLaSalida(dias)), MARGEN);
    }

    @Test
    void sinPagosNoSeDevuelveNada() {
        Reserva reserva = crearReserva();
        assertEquals(0, politicaCliente.calcularDevolucion(reserva, diasAntesDeLaSalida(30)), MARGEN);
        assertEquals(0, politicaAgencia.calcularDevolucion(reserva, diasAntesDeLaSalida(30)), MARGEN);
    }

    @Test
    void laDevolucionConsideraLosAjustes() throws PagoInvalidoException {
        Reserva reserva = crearReserva();
        Pago pago = crearPago(1000);
        pago.agregarAjuste(new AjustePago(1, LocalDate.of(2026, 11, 9), -200, "Corrección de carga"));
        reserva.registrarPago(pago);
        assertEquals(720, politicaCliente.calcularDevolucion(reserva, diasAntesDeLaSalida(30)), MARGEN);
        assertEquals(800, politicaAgencia.calcularDevolucion(reserva, diasAntesDeLaSalida(30)), MARGEN);
    }

    @Test
    void elRecargoNoSeDevuelve() {
        Reserva reserva = crearReserva();
        reserva.registrarPago(new Pago(1, LocalDate.of(2026, 11, 8), 1000, MedioPago.TARJETA, 6));
        assertEquals(1000, politicaAgencia.calcularDevolucion(reserva, diasAntesDeLaSalida(30)), MARGEN);
    }
}
