package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cliente;
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
import ar.edu.uade.rumbosur.modelo.TipoReporte;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU18 Generar reportes. */
class ControladorReportesTest {

    private static final LocalDate INICIO = LocalDate.of(2027, 3, 15);
    private static final LocalDate FECHA_PAGO = LocalDate.of(2027, 1, 10);
    private static final LocalDate HOY = LocalDate.now();
    private static final String SIN_RESULTADOS = "No hay resultados";

    @TempDir
    Path carpeta;

    private ControladorReservas reservas;
    private ControladorSalidas salidas;
    private ControladorReportes reportes;
    private Tour bariloche;
    private Tour iguazu;
    private Cliente cliente;
    private Vendedor luis;
    private Vendedor eva;

    @BeforeEach
    void prepararDatos() {
        reservas = crearControladorReservas();
        salidas = new ControladorSalidas();
        reportes = new ControladorReportes(reservas, salidas);
        bariloche = crearTour(1, "Bariloche");
        iguazu = crearTour(2, "Iguazú");
        cliente = new Cliente(1, "Ana", "Pérez", "111", "1155550000", "ana@mail.com", "Argentina");
        luis = new Vendedor(1, "Luis", "222", "1155551111", 0.05);
        eva = new Vendedor(2, "Eva", "333", "1155552222", 0.05);
    }

    private ControladorReservas crearControladorReservas() {
        ServicioAdicional seguro = new ServicioAdicional(1, "Seguro", 8000, TipoAdicional.SEGURO);
        return new ControladorReservas(new CalculadoraPrecio(List.of()), seguro);
    }

    private Tour crearTour(int codigo, String nombre) {
        TourCultural tour = new TourCultural(codigo, nombre, "Descripción", 3, 1, 10);
        tour.agregarTarifa(new Tarifa(codigo, Temporada.ALTA, INICIO.minusYears(1), INICIO.plusYears(1), 1000));
        return tour;
    }

    private Salida crearSalida(int codigo, LocalDate inicio, Tour tour) {
        return salidas.crearSalida(codigo, inicio, inicio.plusDays(2), 1, 10, tour);
    }

    private Reserva crearReserva(Salida salida, Vendedor vendedor, String documento, double total) throws Exception {
        Pasajero pasajero = new Pasajero(1, "Pasajero " + documento, documento, LocalDate.of(1990, 5, 20),
                TipoPasajero.ADULTO, "Contacto", "");
        Reserva reserva = reservas.solicitarCrearReserva(cliente, salida, vendedor, List.of(pasajero));
        reserva.setTotal(total);
        return reserva;
    }

    private Pago registrarPago(Reserva reserva, double importe, MedioPago medio, int cuotas) throws Exception {
        Pago pago = new Pago(1, FECHA_PAGO, importe, medio, cuotas);
        reservas.registrarPago(reserva, pago);
        return pago;
    }

    private String reporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        return reportes.generarReporte(tipo, desde, hasta);
    }

    // ---------- Ocupación ----------

    @Test
    void ocupacionMuestraLugaresYPorcentaje() throws Exception {
        Salida salida = crearSalida(7, INICIO, bariloche);
        crearReserva(salida, luis, "901", 1000);
        crearReserva(salida, luis, "902", 1000);

        String texto = reporte(TipoReporte.OCUPACION, INICIO, INICIO);

        assertTrue(texto.contains("Salida 7 - Bariloche - 2027-03-15 - 2 de 10 lugares (20 %)"), texto);
    }

    @Test
    void ocupacionNoCuentaLasReservasAnuladas() throws Exception {
        Salida salida = crearSalida(7, INICIO, bariloche);
        crearReserva(salida, luis, "901", 1000);
        Reserva anulada = crearReserva(salida, luis, "902", 1000);
        reservas.cancelarReserva(anulada, "Motivo", OrigenCancelacion.CLIENTE, INICIO.minusDays(30));

        assertTrue(reporte(TipoReporte.OCUPACION, INICIO, INICIO).contains("1 de 10 lugares"));
    }

    @Test
    void ocupacionOrdenaPorFechaYFiltraPorPeriodo() {
        crearSalida(2, INICIO.plusDays(10), iguazu);
        crearSalida(1, INICIO, bariloche);
        crearSalida(3, INICIO.plusDays(11), iguazu);

        String texto = reporte(TipoReporte.OCUPACION, INICIO, INICIO.plusDays(10));

        assertTrue(texto.indexOf("Salida 1") < texto.indexOf("Salida 2"), texto);
        assertFalse(texto.contains("Salida 3"), texto);
    }

    // ---------- Ventas ----------

    @Test
    void ventasSumaPorVendedorYOrdenaDeMayorAMenor() throws Exception {
        Salida salida = crearSalida(7, INICIO, bariloche);
        crearReserva(salida, luis, "901", 1000);
        crearReserva(salida, luis, "902", 300);
        crearReserva(salida, eva, "903", 2500.5);

        String texto = reporte(TipoReporte.VENTAS, HOY, HOY);

        assertTrue(texto.contains("Luis (legajo 1) - 2 reserva(s) - $1300.00"), texto);
        assertTrue(texto.contains("Eva (legajo 2) - 1 reserva(s) - $2500.50"), texto);
        assertTrue(texto.indexOf("Eva") < texto.indexOf("Luis"), texto);
    }

    @Test
    void ventasNoCuentaLasReservasAnuladas() throws Exception {
        Reserva anulada = crearReserva(crearSalida(7, INICIO, bariloche), luis, "901", 1000);
        reservas.cancelarReserva(anulada, "Motivo", OrigenCancelacion.CLIENTE, INICIO.minusDays(30));

        assertTrue(reporte(TipoReporte.VENTAS, HOY, HOY).contains(SIN_RESULTADOS));
    }

    @Test
    void ventasFiltraPorFechaDeCreacion() throws Exception {
        crearReserva(crearSalida(7, INICIO, bariloche), luis, "901", 1000);

        assertTrue(reporte(TipoReporte.VENTAS, HOY.plusDays(1), HOY.plusDays(2)).contains(SIN_RESULTADOS));
    }

    // ---------- Ingresos ----------

    @Test
    void ingresosSumaPorTourYOrdenaDeMayorAMenor() throws Exception {
        registrarPago(crearReserva(crearSalida(1, INICIO, bariloche), luis, "901", 1000), 1000, MedioPago.EFECTIVO, 1);
        registrarPago(crearReserva(crearSalida(2, INICIO, iguazu), luis, "902", 5000), 4000, MedioPago.EFECTIVO, 1);

        String texto = reporte(TipoReporte.INGRESOS, FECHA_PAGO, FECHA_PAGO);

        assertTrue(texto.contains("Iguazú - 1 reserva(s) - $4000.00"), texto);
        assertTrue(texto.contains("Bariloche - 1 reserva(s) - $1000.00"), texto);
        assertTrue(texto.indexOf("Iguazú") < texto.indexOf("Bariloche"), texto);
    }

    @Test
    void ingresosConsideraLosAjustesYNoElRecargo() throws Exception {
        Reserva reserva = crearReserva(crearSalida(1, INICIO, bariloche), luis, "901", 1000);
        Pago pago = registrarPago(reserva, 1000, MedioPago.TARJETA, 3);
        reservas.registrarAjuste(reserva, pago, new AjustePago(1, FECHA_PAGO, -200, "Error de carga"));

        assertTrue(reporte(TipoReporte.INGRESOS, FECHA_PAGO, FECHA_PAGO).contains("$800.00"));
    }

    @Test
    void ingresosRestaLoDevuelto() throws Exception {
        Reserva reserva = crearReserva(crearSalida(1, INICIO, bariloche), luis, "901", 1000);
        registrarPago(reserva, 1000, MedioPago.EFECTIVO, 1);
        reservas.cancelarReserva(reserva, "Motivo", OrigenCancelacion.CLIENTE, FECHA_PAGO.plusDays(1)); // devuelve 900

        assertTrue(reporte(TipoReporte.INGRESOS, FECHA_PAGO, FECHA_PAGO.plusDays(1)).contains("$100.00"));
    }

    @Test
    void ingresosPuedeDarNegativoSiSoloHuboDevolucion() throws Exception {
        Reserva reserva = crearReserva(crearSalida(1, INICIO, bariloche), luis, "901", 1000);
        registrarPago(reserva, 1000, MedioPago.EFECTIVO, 1);
        LocalDate fecha = FECHA_PAGO.plusDays(5);
        reservas.cancelarReserva(reserva, "Motivo", OrigenCancelacion.CLIENTE, fecha);

        assertTrue(reporte(TipoReporte.INGRESOS, fecha, fecha).contains("$-900.00"));
    }

    // ---------- Cancelaciones ----------

    @Test
    void cancelacionesMuestraLasDelClienteYLasDeLaAgencia() throws Exception {
        Salida conCupo = crearSalida(1, INICIO, bariloche);
        Reserva delCliente = crearReserva(conCupo, luis, "901", 1000);
        registrarPago(delCliente, 1000, MedioPago.EFECTIVO, 1);
        LocalDate fechaCliente = INICIO.minusDays(30);
        reservas.cancelarReserva(delCliente, "Cambio de planes", OrigenCancelacion.CLIENTE, fechaCliente);

        Salida sinCupo = salidas.crearSalida(2, INICIO, INICIO.plusDays(2), 5, 10, iguazu);
        Reserva deLaAgencia = crearReserva(sinCupo, luis, "902", 1000);
        registrarPago(deLaAgencia, 1000, MedioPago.EFECTIVO, 1);
        LocalDate fechaAgencia = INICIO.minusDays(7);
        salidas.cancelarPorCupoMinimo(2, fechaAgencia);

        String texto = reporte(TipoReporte.CANCELACIONES, fechaCliente, fechaAgencia);

        assertTrue(texto.contains("CLIENTE - Cambio de planes - devuelto $900.00"), texto);
        assertTrue(texto.contains("AGENCIA - Cancelacion de la agencia por cupo minimo - devuelto $1000.00"), texto);
        assertTrue(texto.indexOf("CLIENTE") < texto.indexOf("AGENCIA"), texto);
        assertTrue(texto.contains("Total: 2 cancelación(es) - devuelto $1900.00"), texto);
    }

    @Test
    void lasDevolucionesDeLaAgenciaRestanEnIngresos() throws Exception {
        Salida sinCupo = salidas.crearSalida(2, INICIO, INICIO.plusDays(2), 5, 10, iguazu);
        registrarPago(crearReserva(sinCupo, luis, "902", 1000), 1000, MedioPago.EFECTIVO, 1);
        salidas.cancelarPorCupoMinimo(2, INICIO.minusDays(7));

        assertTrue(reporte(TipoReporte.INGRESOS, INICIO.minusDays(7), INICIO.minusDays(7)).contains("$-1000.00"));
    }

    @Test
    void cancelacionesFiltraPorPeriodo() throws Exception {
        Reserva reserva = crearReserva(crearSalida(1, INICIO, bariloche), luis, "901", 1000);
        LocalDate fecha = INICIO.minusDays(30);
        reservas.cancelarReserva(reserva, "Motivo", OrigenCancelacion.CLIENTE, fecha);

        assertTrue(reporte(TipoReporte.CANCELACIONES, fecha.plusDays(1), INICIO).contains(SIN_RESULTADOS));
    }

    @Test
    void despuesDeCargarLasCancelacionesNoSeDuplican() throws Exception {
        Salida sinCupo = salidas.crearSalida(2, INICIO, INICIO.plusDays(2), 5, 10, iguazu);
        registrarPago(crearReserva(sinCupo, luis, "902", 1000), 1000, MedioPago.EFECTIVO, 1);
        LocalDate fecha = INICIO.minusDays(7);
        salidas.cancelarPorCupoMinimo(2, fecha);

        Repositorio repositorio = new Repositorio(carpeta.resolve("datos.dat").toString());
        reservas.guardarEn(repositorio);
        salidas.guardarEn(repositorio);
        repositorio.guardarDatos();

        Repositorio cargado = new Repositorio(carpeta.resolve("datos.dat").toString());
        assertTrue(cargado.cargarDatos());
        ControladorReservas reservasCargadas = crearControladorReservas();
        ControladorSalidas salidasCargadas = new ControladorSalidas();
        reservasCargadas.cargarDesde(cargado);
        salidasCargadas.cargarDesde(cargado);

        String texto = new ControladorReportes(reservasCargadas, salidasCargadas)
                .generarReporte(TipoReporte.CANCELACIONES, fecha, fecha);

        assertTrue(texto.contains("Total: 1 cancelación(es)"), texto);
    }

    // ---------- Generales ----------

    @Test
    void sinDatosInformaQueNoHayResultados() {
        for (TipoReporte tipo : TipoReporte.values()) {
            assertTrue(reporte(tipo, INICIO, INICIO).contains(SIN_RESULTADOS), tipo.toString());
        }
    }

    @Test
    void rechazaPedidosIncompletos() {
        assertThrows(IllegalArgumentException.class, () -> reporte(null, INICIO, INICIO));
        assertThrows(IllegalArgumentException.class, () -> reporte(TipoReporte.VENTAS, null, INICIO));
        assertThrows(IllegalArgumentException.class, () -> reporte(TipoReporte.VENTAS, INICIO, null));
    }
}
