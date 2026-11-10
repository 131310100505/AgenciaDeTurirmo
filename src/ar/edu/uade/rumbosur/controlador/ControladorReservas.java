package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PagoInvalidoException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.excepciones.ReservaNoCancelableException;
import ar.edu.uade.rumbosur.modelo.AdicionalContratado;
import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacion;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionAgencia;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GRASP Controlador: recibe los pedidos de la vista para los casos de uso de reservas
 * y los coordina, delegando las reglas en las entidades y en CalculadoraPrecio.
 *
 * GRASP Bajo acoplamiento: recibe la calculadora por constructor y no conoce
 * cómo se calculan los precios.
 *
 * Valen: registrar clientes y pasajeros, crear, contratar adicionales, confirmar,
 * modificar y reemplazar pasajero. Fran: cancelar, registrar pago, registrar ajuste
 * y controlar vencimientos.
 */
public class ControladorReservas {

    private CalculadoraPrecio calculadora;

    // Servicio de seguro que se contrata obligatoriamente en los tours de aventura (S09).
    private ServicioAdicional seguroAventura;
    private int siguienteCodigoAdicional;

    // Map: búsqueda rápida de una reserva por su número.
    private Map<Integer, Reserva> reservas;

    // Map: clientes y pasajeros por documento, así no se registran dos veces (CU04).
    private Map<String, Cliente> clientes;
    private Map<String, Pasajero> pasajeros;

    public ControladorReservas(CalculadoraPrecio calculadora, ServicioAdicional seguroAventura) {
        if (calculadora == null || seguroAventura == null || !seguroAventura.esSeguro()) {
            throw new IllegalArgumentException("El controlador necesita la calculadora y el seguro de aventura.");
        }
        this.calculadora = calculadora;
        this.seguroAventura = seguroAventura;
        this.siguienteCodigoAdicional = 1;
        this.reservas = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pasajeros = new HashMap<>();
    }

    public Reserva buscarReserva(int numero) {
        return reservas.get(numero);
    }

    public List<Reserva> listarReservas() {
        return new ArrayList<>(reservas.values());
    }

    // ---------- CU04 Registrar cliente y pasajero ----------

    public void registrarCliente(Cliente cliente) {
        if (clientes.containsKey(cliente.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un cliente con documento " + cliente.getDocumento() + ".");
        }
        clientes.put(cliente.getDocumento(), cliente);
    }

    public Cliente buscarCliente(String documento) {
        return clientes.get(documento);
    }

    public void registrarPasajero(Pasajero pasajero) {
        if (pasajeros.containsKey(pasajero.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un pasajero con documento " + pasajero.getDocumento() + ".");
        }
        pasajeros.put(pasajero.getDocumento(), pasajero);
    }

    public Pasajero buscarPasajero(String documento) {
        return pasajeros.get(documento);
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes.values());
    }

    public List<Pasajero> listarPasajeros() {
        return new ArrayList<>(pasajeros.values());
    }

    // ---------- CU05 Crear reserva (sigue el diagrama de secuencia) ----------

    /**
     * Mensajes del diagrama CU05:
     *  1. solicitarCrearReserva          -> este método
     *  2. salida.verificarCupo           -> alt [sin cupo]: CupoInsuficienteException (4a)
     *     tour.validarPasajero           -> si no cumple la edad mínima: PasajeroNoHabilitadoException
     *  4b. cliente.crearReserva          -> «create» Reserva(cliente, salida, vendedor)
     *  7. loop: reserva.agregarPasajero  -> opt [aventura]: agrega el seguro obligatorio
     *  8. calculadora.calcularTotal      -> evalúa promociones (esAplicable / calcularDescuento)
     *     salida.agregarReserva          -> los pasajeros ocupan los lugares (CU05 paso 7)
     * 14. devuelve la reserva PENDIENTE  -> la vista informa número, total y estado
     *
     * Primero se valida todo y recién después se crea la reserva, para no dejar
     * una reserva a medio armar si algo falla.
     */
    public Reserva solicitarCrearReserva(Cliente cliente, Salida salida, Vendedor vendedor,
                                         List<Pasajero> pasajerosReserva)
            throws CupoInsuficienteException, PasajeroNoHabilitadoException {
        if (cliente == null || salida == null || vendedor == null
                || pasajerosReserva == null || pasajerosReserva.isEmpty()) {
            throw new IllegalArgumentException("Faltan datos: cliente, salida, vendedor y al menos un pasajero.");
        }
        // Un Set no admite repetidos (usa equals y hashCode de Pasajero, por documento):
        // si tiene menos elementos que la lista, hay un pasajero cargado dos veces.
        if (new HashSet<>(pasajerosReserva).size() < pasajerosReserva.size()) {
            throw new IllegalArgumentException("Hay un pasajero cargado más de una vez en la reserva.");
        }
        if (!salida.verificarCupo(pasajerosReserva.size())) {
            throw new CupoInsuficienteException("La salida no tiene lugar para "
                    + pasajerosReserva.size() + " pasajero(s).");
        }
        for (Pasajero pasajero : pasajerosReserva) {
            validarPasajero(salida, pasajero);
        }

        Reserva reserva = cliente.crearReserva(salida, vendedor);
        for (Pasajero pasajero : pasajerosReserva) {
            reserva.agregarPasajero(pasajero);
            contratarSeguroSiCorresponde(reserva, pasajero);
        }
        recalcularTotal(reserva);

        salida.agregarReserva(reserva);
        reservas.put(reserva.getNumero(), reserva);
        return reserva;
    }

    // ---------- CU07 Contratar servicios adicionales ----------

    public void contratarAdicional(Reserva reserva, ServicioAdicional servicio, Pasajero pasajero, int cantidad) {
        validarVigente(reserva);
        reserva.agregarAdicional(new AdicionalContratado(siguienteCodigoAdicional++, servicio, pasajero, cantidad));
        recalcularTotal(reserva);
    }

    // ---------- CU09 Confirmar reserva ----------

    /** Devuelve true si quedó confirmada; false si los pagos no alcanzan la seña o no estaba pendiente. */
    public boolean confirmarReserva(Reserva reserva) {
        return reserva.confirmar();
    }

    // ---------- CU10 Modificar reserva (agregar un pasajero) ----------

    /** Valida cupo y restricciones del tour y recalcula el importe. Los adicionales se modifican con CU07. */
    public void agregarPasajeroAReserva(Reserva reserva, Pasajero pasajero)
            throws CupoInsuficienteException, PasajeroNoHabilitadoException {
        validarVigente(reserva);
        Salida salida = reserva.getSalida();
        if (!salida.verificarCupo(1)) {
            throw new CupoInsuficienteException("La salida no tiene lugar para otro pasajero.");
        }
        validarPasajero(salida, pasajero);
        reserva.agregarPasajero(pasajero);
        contratarSeguroSiCorresponde(reserva, pasajero);
        recalcularTotal(reserva);
    }

    // ---------- CU11 Reemplazar pasajero ----------

    public void reemplazarPasajero(Reserva reserva, Pasajero actual, Pasajero nuevo)
            throws PasajeroNoHabilitadoException {
        validarPasajero(reserva.getSalida(), nuevo);
        reserva.reemplazarPasajero(actual, nuevo, LocalDate.now());
        contratarSeguroSiCorresponde(reserva, nuevo);
        recalcularTotal(reserva);
    }

    // ---------- Métodos auxiliares ----------

    private void validarPasajero(Salida salida, Pasajero pasajero) throws PasajeroNoHabilitadoException {
        if (!salida.getTour().validarPasajero(pasajero, salida.getFechaInicio())) {
            throw new PasajeroNoHabilitadoException("El pasajero " + pasajero.getNombre()
                    + " no cumple las condiciones del tour " + salida.getTour().getNombre() + ".");
        }
    }

    private void validarVigente(Reserva reserva) {
        if (reserva == null || !reserva.ocupaCupo()) {
            throw new IllegalArgumentException("La reserva no existe o no está vigente.");
        }
    }

    /** Supuestos S09 y S16: en aventura el seguro es obligatorio y se registra como adicional. */
    private void contratarSeguroSiCorresponde(Reserva reserva, Pasajero pasajero) {
        if (reserva.getSalida().getTour().obtenerCategoria() == CategoriaTour.AVENTURA) {
            reserva.agregarAdicional(new AdicionalContratado(siguienteCodigoAdicional++,
                    seguroAventura, pasajero, 1));
        }
    }

    private void recalcularTotal(Reserva reserva) {
        reserva.setTotal(calculadora.calcularTotal(reserva));
    }

    // ---------- Día 2 (Fran agrega acá sus métodos) ----------

    private static final int DIAS_LIMITE_SALDO = 10;

    // Margen de un centavo: al sumar double puede quedar una diferencia mínima.
    private static final double MARGEN_SALDO = 0.01;

    // SOLID - DIP: se declaran por la interfaz; cancelarReserva no conoce las clases concretas.
    private final PoliticaCancelacion politicaCliente = new PoliticaCancelacionCliente();
    private final PoliticaCancelacion politicaAgencia = new PoliticaCancelacionAgencia();

    // Map: cada reserva tiene como máximo una cancelación, se busca por número de reserva.
    private final Map<Integer, Cancelacion> cancelaciones = new HashMap<>();

    /**
     * CU12 Cancelar reserva: la política elegida según el origen calcula la devolución,
     * la reserva queda anulada y la cancelación registra el importe devuelto.
     */
    public Cancelacion cancelarReserva(Reserva reserva, String motivo, OrigenCancelacion origen)
            throws ReservaNoCancelableException {
        return cancelarReserva(reserva, motivo, origen, LocalDate.now());
    }

    /** Sobrecarga que recibe la fecha de cancelación, para poder probar con fechas fijas. */
    public Cancelacion cancelarReserva(Reserva reserva, String motivo, OrigenCancelacion origen, LocalDate fecha)
            throws ReservaNoCancelableException {
        validarCancelable(reserva, fecha);

        Cancelacion cancelacion = new Cancelacion(reserva.getNumero(), fecha, motivo, origen);
        double importeDevuelto = obtenerPolitica(origen).calcularDevolucion(reserva, fecha);
        reserva.anular(motivo);
        cancelacion.registrarDevolucion(importeDevuelto);

        cancelaciones.put(reserva.getNumero(), cancelacion);
        return cancelacion;
    }

    private void validarCancelable(Reserva reserva, LocalDate fecha) throws ReservaNoCancelableException {
        if (reserva == null) {
            throw new ReservaNoCancelableException("La reserva no existe.");
        }
        if (reserva.getEstado() == EstadoReserva.ANULADA || reserva.getEstado() == EstadoReserva.FINALIZADA) {
            throw new ReservaNoCancelableException("La reserva " + reserva.getNumero() + " ya está "
                    + reserva.getEstado() + ".");
        }
        if (fecha.isAfter(reserva.getSalida().getFechaInicio())) {
            throw new ReservaNoCancelableException("La salida de la reserva " + reserva.getNumero()
                    + " ya comenzó.");
        }
    }

    private PoliticaCancelacion obtenerPolitica(OrigenCancelacion origen) {
        if (origen == OrigenCancelacion.AGENCIA) {
            return politicaAgencia;
        }
        return politicaCliente;
    }

    public Map<Integer, Cancelacion> getCancelaciones() {
        return Collections.unmodifiableMap(cancelaciones);
    }

    // ---------- Persistencia ----------

    /** Reemplaza los datos del controlador por los que tiene el repositorio. */
    public void cargarDesde(Repositorio repositorio) {
        reservas.clear();
        clientes.clear();
        pasajeros.clear();
        cancelaciones.clear();
        for (Reserva reserva : repositorio.listarReservas()) {
            reservas.put(reserva.getNumero(), reserva);
            actualizarCodigoDeAdicionales(reserva);
        }
        for (Cliente cliente : repositorio.listarClientes()) {
            clientes.put(cliente.getDocumento(), cliente);
        }
        for (Pasajero pasajero : repositorio.listarPasajeros()) {
            pasajeros.put(pasajero.getDocumento(), pasajero);
        }
        for (Cancelacion cancelacion : repositorio.listarCancelaciones()) {
            cancelaciones.put(cancelacion.getCodigo(), cancelacion);
        }
    }

    /** El próximo código de adicional tiene que seguir después de los ya cargados. */
    private void actualizarCodigoDeAdicionales(Reserva reserva) {
        for (AdicionalContratado adicional : reserva.getAdicionales()) {
            if (adicional.getCodigo() >= siguienteCodigoAdicional) {
                siguienteCodigoAdicional = adicional.getCodigo() + 1;
            }
        }
    }

    /** Le pasa al repositorio los datos del controlador, antes de guardar el archivo. */
    public void guardarEn(Repositorio repositorio) {
        for (Reserva reserva : reservas.values()) {
            repositorio.agregarReserva(reserva);
        }
        for (Cliente cliente : clientes.values()) {
            repositorio.agregarCliente(cliente);
        }
        for (Pasajero pasajero : pasajeros.values()) {
            repositorio.agregarPasajero(pasajero);
        }
        for (Cancelacion cancelacion : cancelaciones.values()) {
            repositorio.agregarCancelacion(cancelacion);
        }
    }

    /** CU08 Registrar pago. El recargo ya lo calcula el propio Pago al crearse. */
    public void registrarPago(Reserva reserva, Pago pago) throws PagoInvalidoException {
        if (reserva == null || pago == null) {
            throw new PagoInvalidoException("El pago necesita una reserva y los datos del pago.");
        }
        if (!reserva.ocupaCupo()) {
            throw new PagoInvalidoException("La reserva " + reserva.getNumero() + " no admite pagos porque está "
                    + reserva.getEstado() + ".");
        }
        reserva.registrarPago(pago);
    }

    /** CU19 Registrar ajuste: el pago original no se modifica, se le agrega la nota. */
    public void registrarAjuste(Reserva reserva, Pago pago, AjustePago ajuste) throws PagoInvalidoException {
        if (reserva == null || !reserva.getPagos().contains(pago)) {
            throw new PagoInvalidoException("El pago no existe en esa reserva.");
        }
        pago.agregarAjuste(ajuste);
    }

    /**
     * CU13 Controlar vencimientos: anula las reservas vigentes con saldo pendiente
     * una vez pasada la fecha límite (10 días antes del inicio de la salida).
     * Las reservas no se sacan del Map: quedan anuladas para conservar el historial.
     */
    public void controlarVencimientos() {
        controlarVencimientos(LocalDate.now());
    }

    /** Sobrecarga que recibe la fecha del control, para poder probar con fechas fijas. */
    public void controlarVencimientos(LocalDate fecha) {
        Iterator<Reserva> iterador = reservas.values().iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            LocalDate fechaLimite = reserva.getSalida().getFechaInicio().minusDays(DIAS_LIMITE_SALDO);
            double saldo = reserva.getTotal() - reserva.calcularTotalPagado();
            if (reserva.ocupaCupo() && fecha.isAfter(fechaLimite) && saldo > MARGEN_SALDO) {
                reserva.anular("Saldo no abonado " + DIAS_LIMITE_SALDO + " días antes de la salida.");
            }
        }
    }
}
