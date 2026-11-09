package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.AdicionalContratado;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Promocion;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.TipoPromocion;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Reglas de precios (requisitos 4.2 y supuestos S08, S12 a S16).
 * Las fechas se arman a partir de hoy, porque la promoción anticipada
 * depende de la fecha de creación de la reserva.
 */
class CalculadoraPrecioTest {

    private static final double MARGEN = 0.001;
    private static final double PRECIO_BASE = 100000;
    private static final LocalDate HOY = LocalDate.now();

    private CalculadoraPrecio calculadora;
    private TourCultural tour;
    private Tarifa tarifa;
    private Cliente cliente;
    private Vendedor vendedor;
    private int documento;

    @BeforeEach
    void prepararDatos() {
        calculadora = new CalculadoraPrecio(List.of(
                new Promocion(1, "Jubilados", TipoPromocion.JUBILADO, 0.10),
                new Promocion(2, "Reserva anticipada", TipoPromocion.ANTICIPADA, 0.15),
                new Promocion(3, "Grupo", TipoPromocion.GRUPO, 0.10)));
        tarifa = new Tarifa(1, Temporada.ALTA, HOY, HOY.plusDays(365), PRECIO_BASE);
        tour = new TourCultural(1, "Bariloche", "Circuito chico", 3, 1, 20);
        tour.agregarTarifa(tarifa);
        cliente = new Cliente(1, "Ana", "Pérez", "30111222", "1155550000", "ana@mail.com", "Argentina");
        vendedor = new Vendedor(1, "Luis", "28333444", "1155551111", 0.05);
        documento = 40000000;
    }

    private Salida salidaEnDias(int dias) {
        return new Salida(1, HOY.plusDays(dias), HOY.plusDays(dias + 2), 1, 20, tour);
    }

    private Pasajero pasajero(int edadALaSalida, LocalDate fechaSalida, TipoPasajero tipo) {
        documento++;
        LocalDate nacimiento = fechaSalida.minusYears(edadALaSalida).minusDays(1);
        return new Pasajero(documento, "Pasajero " + documento, String.valueOf(documento),
                nacimiento, tipo, "Contacto", "");
    }

    private Reserva reservaCon(Salida salida, Pasajero... pasajeros) {
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        for (Pasajero pasajero : pasajeros) {
            reserva.agregarPasajero(pasajero);
        }
        return reserva;
    }

    @Test
    void adultoPagaElCienPorCiento() {
        Salida salida = salidaEnDias(30);
        Pasajero adulto = pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO);
        assertEquals(100000, calculadora.calcularTarifaPasajero(adulto, tarifa, salida.getFechaInicio()), MARGEN);
    }

    @Test
    void menorDe12PagaElSesentaPorCiento() {
        Salida salida = salidaEnDias(30);
        Pasajero menor = pasajero(8, salida.getFechaInicio(), TipoPasajero.MENOR);
        assertEquals(60000, calculadora.calcularTarifaPasajero(menor, tarifa, salida.getFechaInicio()), MARGEN);
    }

    @Test
    void menorDe3NoPagaTarifaBase() {
        Salida salida = salidaEnDias(30);
        Pasajero bebe = pasajero(2, salida.getFechaInicio(), TipoPasajero.MENOR);
        assertEquals(0, calculadora.calcularTarifaPasajero(bebe, tarifa, salida.getFechaInicio()), MARGEN);
    }

    @Test
    void laEdadSeCalculaALaFechaDeLaSalida() {
        // Hoy tiene 11 años, pero cumple 12 antes de la salida: paga como adulto (S08).
        Salida salida = salidaEnDias(30);
        LocalDate nacimiento = salida.getFechaInicio().minusYears(12).minusDays(5);
        Pasajero pasajero = new Pasajero(1, "Tomi", "50111222", nacimiento, TipoPasajero.MENOR, "Ana", "");
        assertEquals(11, pasajero.calcularEdad(HOY));
        assertEquals(100000, calculadora.calcularTarifaPasajero(pasajero, tarifa, salida.getFechaInicio()), MARGEN);
    }

    @Test
    void sinPromocionesSePagaLaTarifa() {
        Salida salida = salidaEnDias(30);
        Reserva reserva = reservaCon(salida, pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO));
        assertEquals(100000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void jubiladoTieneDiezPorCientoDeDescuento() {
        Salida salida = salidaEnDias(30);
        Reserva reserva = reservaCon(salida, pasajero(70, salida.getFechaInicio(), TipoPasajero.JUBILADO));
        assertEquals(90000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void reservaConMasDe60DiasTieneQuincePorCiento() {
        Salida salida = salidaEnDias(90);
        Reserva reserva = reservaCon(salida, pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO));
        assertEquals(85000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void conExactamente60DiasNoHayDescuentoAnticipado() {
        // S14: corresponde cuando la anticipación es SUPERIOR a 60 días.
        Salida salida = salidaEnDias(60);
        Reserva reserva = reservaCon(salida, pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO));
        assertEquals(100000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void grupoDeSeisTieneDiezPorCientoCadaUno() {
        Salida salida = salidaEnDias(30);
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        for (int i = 0; i < 6; i++) {
            reserva.agregarPasajero(pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO));
        }
        assertEquals(6 * 90000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void lasPromocionesNoSeAcumulanYSeAplicaLaMasFavorable() {
        // Jubilado (10 %) con reserva anticipada (15 %): se aplica solo el 15 %, no el 25 %.
        Salida salida = salidaEnDias(90);
        Reserva reserva = reservaCon(salida, pasajero(70, salida.getFechaInicio(), TipoPasajero.JUBILADO));
        assertEquals(85000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void losAdicionalesSeSumanDespuesDelDescuento() {
        // S12 y S16: 10 % de descuento sobre 100.000 y después se suman 5.000 de adicional.
        Salida salida = salidaEnDias(30);
        Pasajero jubilado = pasajero(70, salida.getFechaInicio(), TipoPasajero.JUBILADO);
        Reserva reserva = reservaCon(salida, jubilado);
        ServicioAdicional excursion = new ServicioAdicional(1, "Cerro Otto", 5000, TipoAdicional.EXCURSION);
        reserva.agregarAdicional(new AdicionalContratado(1, excursion, jubilado, 1));
        assertEquals(95000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void familiaConAdultoMenorYBebe() {
        Salida salida = salidaEnDias(30);
        Reserva reserva = reservaCon(salida,
                pasajero(40, salida.getFechaInicio(), TipoPasajero.ADULTO),
                pasajero(8, salida.getFechaInicio(), TipoPasajero.MENOR),
                pasajero(1, salida.getFechaInicio(), TipoPasajero.MENOR));
        assertEquals(160000, calculadora.calcularTotal(reserva), MARGEN);
    }

    @Test
    void sinTarifaVigenteSeInformaElError() {
        Salida salida = salidaEnDias(400); // fuera de la vigencia de la tarifa
        Reserva reserva = reservaCon(salida, pasajero(30, salida.getFechaInicio(), TipoPasajero.ADULTO));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcularTotal(reserva));
    }
}
