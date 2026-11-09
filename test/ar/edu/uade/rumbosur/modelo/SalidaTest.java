
package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SalidaTest {

    // Tour de prueba
    private Tour crearTour() {
        return new Tour(
                1,
                "Bariloche",
                "Tour de prueba",
                5,
                5,
                20
        ) {
            @Override
            public CategoriaTour obtenerCategoria() {
                return CategoriaTour.NATURALEZA;
            }

            @Override
            public boolean validarPasajero(
                    Pasajero pasajero,
                    LocalDate fechaSalida) {
                return true;
            }
        };
    }

    // Crea una salida para las pruebas
    private Salida crearSalida(int codigo,
                               LocalDate inicio,
                               LocalDate fin) {
        return new Salida(
                codigo,
                inicio,
                fin,
                5,
                20,
                crearTour()
        );
    }

    // Crea un guia habilitado para naturaleza
    private Guia crearGuia(int legajo) {
        return new Guia(
                legajo,
                "Guia " + legajo,
                "DNI" + legajo,
                "1123456789",
                Arrays.asList("Español"),
                Collections.singleton(CategoriaTour.NATURALEZA),
                50000.0
        );
    }

    // Crea un cliente de prueba
    private Cliente crearCliente() {
        return new Cliente(
                1,
                "Juan",
                "Perez",
                "30111222",
                "1123456789",
                "juan@test.com",
                "Argentina"
        );
    }

    // Crea un vendedor de prueba
    private Vendedor crearVendedor() {
        return new Vendedor(
                10,
                "Carlos",
                "25111222",
                "1198765432",
                0.05
        );
    }

    // Crea un pasajero de prueba con documento unico
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
    private Reserva crearReserva(Salida salida, int cantidad,
                                 int primerNumero) {

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

    // Confirma una reserva sin registrar pagos,
    // usando total cero exclusivamente para las pruebas
    private void confirmarReservaDePrueba(Reserva reserva) {
        reserva.setTotal(0);
        assertTrue(reserva.confirmar());
    }

    // TESTS ORIGINALES

    @Test
    void testEstadoInicial() {
        Salida salida = crearSalida(
                1,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
        assertEquals(0, salida.calcularOcupacion());
        assertFalse(salida.isListaPasajerosGenerada());
    }

    @Test
    void testVerificarCupo() {
        Salida salida = crearSalida(
                2,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        assertTrue(salida.verificarCupo(10));
        assertFalse(salida.verificarCupo(21));
    }

    @Test
    void testOrdenarPorFecha() {
        Salida salida1 = crearSalida(
                1,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Salida salida2 = crearSalida(
                2,
                LocalDate.of(2026, 12, 20),
                LocalDate.of(2026, 12, 25)
        );

        assertTrue(salida1.compareTo(salida2) < 0);
    }

    // TESTS DE GUIAS

    @Test
    void testAsignarGuia() {
        Salida salida = crearSalida(
                3,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Guia guia = crearGuia(101);

        salida.asignarGuia(guia);

        assertSame(guia, salida.getGuia());
        assertTrue(guia.getSalidasAsignadas().contains(salida));
    }

    @Test
    void testReasignarGuiaLiberaAlAnterior() {
        Salida salida = crearSalida(
                4,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Guia guiaAnterior = crearGuia(102);
        Guia guiaNuevo = crearGuia(103);

        salida.asignarGuia(guiaAnterior);
        salida.asignarGuia(guiaNuevo);

        assertSame(guiaNuevo, salida.getGuia());
        assertFalse(guiaAnterior.getSalidasAsignadas().contains(salida));
        assertTrue(guiaNuevo.getSalidasAsignadas().contains(salida));
        assertTrue(guiaAnterior.estaDisponible(salida));
    }

    @Test
    void testGuiaNoDisponiblePorFechasSuperpuestas() {
        Guia guia = crearGuia(104);

        Salida salida1 = crearSalida(
                5,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Salida salida2 = crearSalida(
                6,
                LocalDate.of(2026, 12, 13),
                LocalDate.of(2026, 12, 18)
        );

        salida1.asignarGuia(guia);

        assertThrows(
                IllegalArgumentException.class,
                () -> salida2.asignarGuia(guia)
        );

        assertNull(salida2.getGuia());
        assertFalse(guia.getSalidasAsignadas().contains(salida2));
    }

    @Test
    void testReasignacionFallidaConservaGuiaAnterior() {
        Guia guiaOriginal = crearGuia(105);
        Guia guiaOcupado = crearGuia(106);

        Salida salida = crearSalida(
                7,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Salida otraSalida = crearSalida(
                8,
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 16)
        );

        salida.asignarGuia(guiaOriginal);
        otraSalida.asignarGuia(guiaOcupado);

        assertThrows(
                IllegalArgumentException.class,
                () -> salida.asignarGuia(guiaOcupado)
        );

        assertSame(guiaOriginal, salida.getGuia());
        assertTrue(guiaOriginal.getSalidasAsignadas().contains(salida));
        assertFalse(guiaOcupado.getSalidasAsignadas().contains(salida));
    }

    // NUEVOS TESTS DE RESERVAS Y PASAJEROS

    @Test
    void testReservaPendienteOcupaCupo() {
        Salida salida = crearSalida(
                9,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva reserva = crearReserva(salida, 3, 100);

        salida.agregarReserva(reserva);

        assertEquals(3, salida.calcularOcupacion());
        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
        assertEquals(1, salida.getReservas().size());
    }

    @Test
    void testReservaConfirmadaOcupaCupo() {
        Salida salida = crearSalida(
                10,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva reserva = crearReserva(salida, 5, 200);

        confirmarReservaDePrueba(reserva);
        salida.agregarReserva(reserva);

        assertEquals(5, salida.calcularOcupacion());
        assertEquals(EstadoSalida.CONFIRMADA, salida.getEstado());
    }

    @Test
    void testReservaAnuladaLiberaCupo() {
        Salida salida = crearSalida(
                11,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva reserva = crearReserva(salida, 5, 300);

        salida.agregarReserva(reserva);

        assertEquals(5, salida.calcularOcupacion());

        reserva.anular("Cancelacion de prueba");

        assertEquals(0, salida.calcularOcupacion());
        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
    }

    @Test
    void testNoSuperarCupoMaximo() {
        Salida salida = crearSalida(
                12,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva reserva1 = crearReserva(salida, 15, 400);
        Reserva reserva2 = crearReserva(salida, 6, 500);

        salida.agregarReserva(reserva1);

        assertThrows(
                IllegalArgumentException.class,
                () -> salida.agregarReserva(reserva2)
        );

        assertEquals(15, salida.calcularOcupacion());
        assertEquals(1, salida.getReservas().size());
    }

    @Test
    void testGenerarListaSoloConfirmados() {
        Salida salida = crearSalida(
                13,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva confirmada = crearReserva(salida, 5, 600);
        Reserva pendiente = crearReserva(salida, 2, 700);

        confirmarReservaDePrueba(confirmada);

        salida.agregarReserva(confirmada);
        salida.agregarReserva(pendiente);

        salida.generarListaPasajeros();

        assertTrue(salida.isListaPasajerosGenerada());
        assertEquals(5, salida.getListaPasajeros().size());

        assertTrue(
                salida.getListaPasajeros()
                        .containsAll(confirmada.getPasajeros())
        );

        assertFalse(
                salida.getListaPasajeros()
                        .contains(pendiente.getPasajeros().get(0))
        );
    }

    @Test
    void testNuevaReservaInvalidaListaAnterior() {
        Salida salida = crearSalida(
                14,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 15)
        );

        Reserva reserva1 = crearReserva(salida, 5, 800);
        confirmarReservaDePrueba(reserva1);

        salida.agregarReserva(reserva1);
        salida.generarListaPasajeros();

        assertTrue(salida.isListaPasajerosGenerada());
        assertEquals(5, salida.getListaPasajeros().size());

        Reserva reserva2 = crearReserva(salida, 2, 900);
        salida.agregarReserva(reserva2);

        assertFalse(salida.isListaPasajerosGenerada());
        assertTrue(salida.getListaPasajeros().isEmpty());
    }
}
