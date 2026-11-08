package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Estados, seña, reemplazo de pasajeros y anulación de una reserva. */
class ReservaTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate HOY = LocalDate.now();

    private Salida salida;
    private Reserva reserva;
    private Pasajero ana;
    private Pasajero luis;

    @BeforeEach
    void prepararDatos() {
        TourCultural tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 1, 20);
        salida = new Salida(1, HOY.plusDays(10), HOY.plusDays(12), 1, 20, tour);
        Cliente cliente = new Cliente(1, "Ana", "Pérez", "30111222", "1155550000", "ana@mail.com", "Argentina");
        Vendedor vendedor = new Vendedor(1, "Luis", "28333444", "1155551111", 0.05);
        reserva = new Reserva(cliente, salida, vendedor);
        ana = new Pasajero(1, "Ana Pérez", "30111222", LocalDate.of(1990, 5, 1), TipoPasajero.ADULTO, "Juan", "");
        luis = new Pasajero(2, "Luis Gómez", "31222333", LocalDate.of(1988, 2, 3), TipoPasajero.ADULTO, "Marta", "");
        reserva.agregarPasajero(ana);
        reserva.setTotal(100000);
    }

    private Pago pago(double importe) {
        return new Pago(1, HOY, importe, MedioPago.EFECTIVO, 1);
    }

    @Test
    void unaReservaNuevaQuedaPendienteYOcupaCupo() {
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertTrue(reserva.ocupaCupo());
        assertEquals(1, reserva.getCantidadPasajeros());
    }

    @Test
    void noSePuedeAgregarDosVecesElMismoPasajero() {
        Pasajero mismoDocumento = new Pasajero(9, "Otra", "30111222", LocalDate.of(1990, 1, 1),
                TipoPasajero.ADULTO, "", "");
        assertThrows(IllegalArgumentException.class, () -> reserva.agregarPasajero(mismoDocumento));
    }

    @Test
    void seConfirmaConLaSenaDelTreintaPorCiento() {
        reserva.registrarPago(pago(30000));
        assertTrue(reserva.confirmar());
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertEquals(70000, reserva.calcularSaldo(), MARGEN);
    }

    @Test
    void siNoAlcanzaLaSenaSiguePendiente() {
        reserva.registrarPago(pago(29999));
        assertFalse(reserva.confirmar());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void laSenaSeCalculaConLosPagosNetos() throws Exception {
        // Un ajuste de -5.000 deja el neto en 25.000: no alcanza el 30 %.
        Pago pago = pago(30000);
        pago.agregarAjuste(new AjustePago(1, HOY, -5000, "Error de carga"));
        reserva.registrarPago(pago);
        assertFalse(reserva.confirmar());
    }

    @Test
    void seReemplazaUnPasajeroHastaTresDiasAntes() {
        reserva.reemplazarPasajero(ana, luis, salida.getFechaInicio().minusDays(3));
        assertEquals(luis, reserva.getPasajeros().get(0));
        assertEquals(1, reserva.getCantidadPasajeros());
    }

    @Test
    void conMenosDeTresDiasNoSePuedeReemplazar() {
        LocalDate dosDiasAntes = salida.getFechaInicio().minusDays(2);
        assertThrows(IllegalArgumentException.class, () -> reserva.reemplazarPasajero(ana, luis, dosDiasAntes));
        assertEquals(ana, reserva.getPasajeros().get(0));
    }

    @Test
    void alReemplazarSeQuitanLosAdicionalesDelPasajeroAnterior() {
        ServicioAdicional excursion = new ServicioAdicional(1, "Cerro Otto", 5000, TipoAdicional.EXCURSION);
        reserva.agregarAdicional(new AdicionalContratado(1, excursion, ana, 1));
        reserva.reemplazarPasajero(ana, luis, HOY);
        assertTrue(reserva.getAdicionales().isEmpty());
    }

    @Test
    void anularLiberaElCupoYNoSePuedeAnularDosVeces() {
        reserva.anular("El cliente no puede viajar");
        assertEquals(EstadoReserva.ANULADA, reserva.getEstado());
        assertFalse(reserva.ocupaCupo());
        assertThrows(IllegalArgumentException.class, () -> reserva.anular("Otra vez"));
    }

    @Test
    void unaReservaAnuladaNoSePuedeConfirmar() {
        reserva.registrarPago(pago(50000));
        reserva.anular("Cancelada");
        assertFalse(reserva.confirmar());
    }

    @Test
    void losCambiosQuedanEnElHistorial() {
        reserva.reemplazarPasajero(ana, luis, HOY);
        reserva.anular("Cancelada");
        assertEquals(4, reserva.getHistorial().size()); // creación, alta de Ana, reemplazo, anulación
    }
}
