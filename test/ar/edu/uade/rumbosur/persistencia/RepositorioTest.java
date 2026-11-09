package ar.edu.uade.rumbosur.persistencia;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
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
import ar.edu.uade.rumbosur.modelo.TourAventura;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate INICIO_SALIDA = LocalDate.of(2027, 3, 15);

    // JUnit crea esta carpeta para cada prueba y después la borra.
    @TempDir
    Path carpeta;

    private String ruta;
    private TourAventura tour;
    private Salida salida;
    private Cliente cliente;
    private Vendedor vendedor;

    @BeforeEach
    void prepararDatos() {
        ruta = carpeta.resolve("datos.dat").toString();
        tour = new TourAventura(1, "Trekking", "Laguna de los Tres", 3, 1, 10, 16, "Alta");
        tour.agregarTarifa(new Tarifa(1, Temporada.ALTA, INICIO_SALIDA.minusYears(1), INICIO_SALIDA.plusYears(1), 1000));
        salida = new Salida(7, INICIO_SALIDA, INICIO_SALIDA.plusDays(2), 1, 10, tour);
        cliente = new Cliente(1, "Ana", "Pérez", "111", "1155550000", "ana@mail.com", "Argentina");
        vendedor = new Vendedor(2, "Luis", "222", "1155551111", 0.05);
    }

    private Pasajero crearPasajero(String documento) {
        return new Pasajero(1, "Pasajero " + documento, documento, LocalDate.of(1990, 5, 20),
                TipoPasajero.ADULTO, "Contacto", "");
    }

    private Reserva crearReserva() {
        Reserva reserva = new Reserva(cliente, salida, vendedor);
        reserva.agregarPasajero(crearPasajero("900"));
        salida.agregarReserva(reserva);
        return reserva;
    }

    private ControladorReservas crearControladorReservas() {
        ServicioAdicional seguro = new ServicioAdicional(1, "Seguro", 8000, TipoAdicional.SEGURO);
        return new ControladorReservas(new CalculadoraPrecio(List.of()), seguro);
    }

    private Repositorio cargarRepositorio() {
        Repositorio repositorio = new Repositorio(ruta);
        assertTrue(repositorio.cargarDatos());
        return repositorio;
    }

    // ---------- Archivo ----------

    @Test
    void sinArchivoNoHayDatosParaCargar() {
        Repositorio repositorio = new Repositorio(ruta);
        assertFalse(repositorio.cargarDatos());
        assertTrue(repositorio.listarReservas().isEmpty());
    }

    @Test
    void unArchivoDanadoNoSeCargaYNoRompeElPrograma() throws IOException {
        Files.writeString(carpeta.resolve("datos.dat"), "esto no es un archivo serializado");
        Repositorio repositorio = new Repositorio(ruta);
        assertFalse(repositorio.cargarDatos());
        assertTrue(repositorio.listarReservas().isEmpty());
    }

    @Test
    void guardaYCargaLosDatos() throws Exception {
        Reserva reserva = crearReserva();
        Pago pago = new Pago(1, INICIO_SALIDA.minusDays(60), 1000, MedioPago.TARJETA, 3);
        pago.agregarAjuste(new AjustePago(1, INICIO_SALIDA.minusDays(59), -200, "Error de carga"));
        reserva.registrarPago(pago);

        Repositorio repositorio = new Repositorio(ruta);
        repositorio.agregarReserva(reserva);
        repositorio.agregarSalida(salida);
        repositorio.agregarCliente(cliente);
        repositorio.agregarTour(tour);
        repositorio.guardarDatos();

        Reserva cargada = cargarRepositorio().buscarReserva(reserva.getNumero());

        assertNotSame(reserva, cargada);
        assertEquals(EstadoReserva.PENDIENTE, cargada.getEstado());
        assertEquals(800, cargada.calcularTotalPagado(), MARGEN);
        assertEquals(70, cargada.getPagos().get(0).getRecargo(), MARGEN);
    }

    @Test
    void alCargarSeMantienenLasReferenciasEntreObjetos() {
        Reserva reserva = crearReserva();
        Repositorio repositorio = new Repositorio(ruta);
        repositorio.agregarReserva(reserva);
        repositorio.agregarSalida(salida);
        repositorio.agregarCliente(cliente);
        repositorio.agregarTour(tour);
        repositorio.guardarDatos();

        Repositorio cargado = cargarRepositorio();
        Reserva reservaCargada = cargado.buscarReserva(reserva.getNumero());
        Salida salidaCargada = cargado.buscarSalida(7);

        assertSame(salidaCargada, reservaCargada.getSalida());
        assertSame(reservaCargada, salidaCargada.getReservas().get(0));
        assertSame(cargado.buscarTour(1), salidaCargada.getTour());
        assertSame(cargado.buscarCliente("111"), reservaCargada.getCliente());
    }

    @Test
    void clientesConElMismoCodigoNoSePisan() {
        Repositorio repositorio = new Repositorio(ruta);
        repositorio.agregarCliente(cliente);
        repositorio.agregarCliente(new Cliente(1, "Juan", "Gómez", "333", "1155552222", "juan@mail.com", "Chile"));
        assertEquals(2, repositorio.listarClientes().size());
    }

    @Test
    void lasListasNoModificanLosDatosDelRepositorio() {
        Repositorio repositorio = new Repositorio(ruta);
        repositorio.agregarCliente(cliente);
        repositorio.listarClientes().clear();
        assertEquals(1, repositorio.listarClientes().size());
    }

    // ---------- Contador de reservas ----------

    @Test
    void alCargarElContadorSigueDesdeElUltimoNumero() {
        Reserva.actualizarSiguienteNumero(40);
        Repositorio repositorio = new Repositorio(ruta);
        repositorio.agregarReserva(crearReserva());
        repositorio.agregarReserva(crearReserva());
        Reserva ultima = crearReserva();
        repositorio.agregarReserva(ultima);
        repositorio.guardarDatos();

        Reserva.actualizarSiguienteNumero(0); // como si se reiniciara el programa
        cargarRepositorio();

        assertEquals(ultima.getNumero() + 1, crearReserva().getNumero());
    }

    @Test
    void sinArchivoElContadorNoCambia() {
        Reserva.actualizarSiguienteNumero(100);
        new Repositorio(ruta).cargarDatos();
        assertEquals(101, crearReserva().getNumero());
    }

    // ---------- Controladores ----------

    @Test
    void losControladoresGuardanYRecuperanSusDatos() throws Exception {
        Repositorio primero = new Repositorio(ruta);
        primero.agregarCliente(cliente);
        ControladorReservas reservas = crearControladorReservas();
        ControladorSalidas salidas = new ControladorSalidas();
        reservas.cargarDesde(primero);
        salidas.cargarDesde(primero);

        Salida creada = salidas.crearSalida(9, INICIO_SALIDA, INICIO_SALIDA.plusDays(2), 1, 10, tour);
        reservas.registrarPasajero(crearPasajero("900"));
        Reserva vigente = reservas.solicitarCrearReserva(cliente, creada, vendedor, List.of(crearPasajero("900")));
        Reserva anulada = reservas.solicitarCrearReserva(cliente, creada, vendedor, List.of(crearPasajero("901")));
        reservas.registrarPago(anulada, new Pago(1, INICIO_SALIDA.minusDays(60), 1000, MedioPago.EFECTIVO, 1));
        reservas.cancelarReserva(anulada, "Cambio de planes", OrigenCancelacion.CLIENTE, INICIO_SALIDA.minusDays(30));

        reservas.guardarEn(primero);
        salidas.guardarEn(primero);
        primero.guardarDatos();

        Repositorio segundo = cargarRepositorio();
        ControladorReservas reservasCargadas = crearControladorReservas();
        ControladorSalidas salidasCargadas = new ControladorSalidas();
        reservasCargadas.cargarDesde(segundo);
        salidasCargadas.cargarDesde(segundo);

        assertEquals(2, reservasCargadas.listarReservas().size());
        assertNotNull(reservasCargadas.buscarCliente("111"));
        assertNotNull(reservasCargadas.buscarPasajero("900"));
        Reserva vigenteCargada = reservasCargadas.buscarReserva(vigente.getNumero());
        assertEquals(EstadoReserva.PENDIENTE, vigenteCargada.getEstado());
        assertEquals(EstadoReserva.ANULADA, reservasCargadas.buscarReserva(anulada.getNumero()).getEstado());
        Cancelacion cancelacion = reservasCargadas.getCancelaciones().get(anulada.getNumero());
        assertEquals(900, cancelacion.getImporteDevuelto(), MARGEN);
        assertSame(salidasCargadas.buscarSalida(9), vigenteCargada.getSalida());
    }

    @Test
    void despuesDeCargarLosAdicionalesSiguenNumerando() throws Exception {
        ControladorReservas reservas = crearControladorReservas();
        reservas.solicitarCrearReserva(cliente, salida, vendedor, List.of(crearPasajero("900"), crearPasajero("901")));
        Repositorio repositorio = new Repositorio(ruta);
        reservas.guardarEn(repositorio);

        ControladorReservas reservasCargadas = crearControladorReservas();
        reservasCargadas.cargarDesde(repositorio);
        Reserva nueva = reservasCargadas.solicitarCrearReserva(cliente, salida, vendedor, List.of(crearPasajero("902")));

        assertEquals(3, nueva.getAdicionales().get(0).getCodigo());
    }
}
