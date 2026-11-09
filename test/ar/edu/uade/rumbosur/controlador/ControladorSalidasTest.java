
package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControladorSalidasTest {

    // Crea un tour de prueba
    private Tour crearTour() {
        return new Tour(
                1, "Bariloche", "Tour de prueba",
                5, 5, 20
        ) {
            @Override
            public CategoriaTour obtenerCategoria() {
                return CategoriaTour.NATURALEZA;
            }

            @Override
            public boolean validarPasajero(
                    Pasajero pasajero, LocalDate fechaSalida) {
                return true;
            }
        };
    }

    // Crea una salida con cupo minimo de 5 pasajeros
    private Salida crearSalida(
            ControladorSalidas controlador,
            LocalDate fechaInicio) {

        return controlador.crearSalida(
                100,
                fechaInicio,
                fechaInicio.plusDays(2),
                5,
                20,
                crearTour()
        );
    }

    // Crea un cliente de prueba
    private Cliente crearCliente() {
        return new Cliente(
                1, "Juan", "Perez",
                "30111222", "1123456789",
                "juan@test.com", "Argentina"
        );
    }

    // Crea un vendedor de prueba
    private Vendedor crearVendedor() {
        return new Vendedor(
                10, "Carlos", "25111222",
                "1198765432", 0.05
        );
    }

    // Crea un pasajero con documento unico
    private Pasajero crearPasajero(int numero) {
        return new Pasajero(
                numero,
                "Pasajero " + numero,
                "DNI" + numero,
                LocalDate.of(2000, 1, 1),
                TipoPasajero.ADULTO,
                "1123456789",
                "Sin restricciones"
        );
    }

    // Crea una reserva con la cantidad indicada de pasajeros
    private Reserva crearReserva(
            Salida salida, int cantidad, int primerNumero) {

        Reserva reserva = new Reserva(
                crearCliente(),
                salida,
                crearVendedor()
        );

        for (int i = 0; i < cantidad; i++) {
            reserva.agregarPasajero(
                    crearPasajero(primerNumero + i)
            );
        }

        return reserva;
    }

    // TEST 1: cancela por cupo insuficiente a los 7 dias
    @Test
    void debeCancelarSiNoAlcanzaCupoMinimo() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 13);

        Salida salida = crearSalida(
                controlador, fechaControl.plusDays(7)
        );

        Map<Integer, Double> devoluciones =
                controlador.cancelarPorCupoMinimo(
                        100, fechaControl
                );

        assertEquals(
                EstadoSalida.CANCELADA,
                salida.getEstado()
        );

        assertTrue(devoluciones.isEmpty());
    }

    // TEST 2: no cancela fuera de los 7 dias
    @Test
    void noDebeCancelarFueraDeLosSieteDias() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 12);

        crearSalida(
                controlador,
                LocalDate.of(2026, 11, 20)
        );

        assertFalse(
                controlador.debeCancelarPorCupoMinimo(
                        100, fechaControl
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> controlador.cancelarPorCupoMinimo(
                        100, fechaControl
                )
        );
    }

    // TEST 3: no cancela dos veces la misma salida
    @Test
    void noDebeCancelarUnaSalidaYaCancelada() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 13);

        Salida salida = crearSalida(
                controlador, fechaControl.plusDays(7)
        );

        controlador.cancelarPorCupoMinimo(
                100, fechaControl
        );

        assertFalse(
                controlador.debeCancelarPorCupoMinimo(
                        100, fechaControl
                )
        );

        assertEquals(
                EstadoSalida.CANCELADA,
                salida.getEstado()
        );
    }

    // TEST 4: controla todas las salidas registradas
    @Test
    void debeControlarTodasLasSalidas() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 13);

        crearSalida(
                controlador, fechaControl.plusDays(7)
        );

        Map<Integer, Map<Integer, Double>> resultado =
                controlador.controlarCupoMinimo(fechaControl);

        assertEquals(1, resultado.size());
        assertTrue(resultado.containsKey(100));
    }

    // TEST 5: no cancela si alcanza el cupo minimo
    @Test
    void noDebeCancelarSiAlcanzaCupoMinimo() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 13);

        Salida salida = crearSalida(
                controlador, fechaControl.plusDays(7)
        );

        Reserva reserva = crearReserva(salida, 5, 100);

        salida.agregarReserva(reserva);

        assertEquals(5, salida.calcularOcupacion());

        assertFalse(
                controlador.debeCancelarPorCupoMinimo(
                        100, fechaControl
                )
        );

        assertTrue(
                controlador.controlarCupoMinimo(
                        fechaControl
                ).isEmpty()
        );

        assertNotEquals(
                EstadoSalida.CANCELADA,
                salida.getEstado()
        );

        assertTrue(reserva.ocupaCupo());
    }

    // TEST 6: anula la reserva y calcula el 100 % a devolver
    @Test
    void debeAnularReservasYCalcularDevolucion() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        LocalDate fechaControl =
                LocalDate.of(2026, 11, 13);

        Salida salida = crearSalida(
                controlador, fechaControl.plusDays(7)
        );

        // Reserva de 3 pasajeros: no alcanza el minimo de 5
        Reserva reserva = crearReserva(salida, 3, 200);

        reserva.setTotal(100000.0);

        // Se registra un pago de 30000
        Pago pago = new Pago(
                1,
                fechaControl.minusDays(1),
                30000.0,
                MedioPago.TARJETA,
                1
        );

        reserva.registrarPago(pago);

        assertTrue(reserva.confirmar());

        salida.agregarReserva(reserva);

        assertEquals(3, salida.calcularOcupacion());

        // Ejecuta la cancelacion por cupo minimo
        Map<Integer, Double> devoluciones =
                controlador.cancelarPorCupoMinimo(
                        100, fechaControl
                );

        // La salida queda cancelada
        assertEquals(
                EstadoSalida.CANCELADA,
                salida.getEstado()
        );

        // La reserva queda anulada
        assertEquals(
                EstadoReserva.ANULADA,
                reserva.getEstado()
        );

        // Los pasajeros dejan de ocupar cupo
        assertEquals(0, salida.calcularOcupacion());

        // La agencia debe devolver el 100 % de lo pagado
        assertEquals(
                30000.0,
                devoluciones.get(reserva.getNumero()),
                0.001
        );

        // El pago original no se modifica
        assertEquals(
                30000.0,
                reserva.calcularTotalPagado(),
                0.001
        );
    }
}
