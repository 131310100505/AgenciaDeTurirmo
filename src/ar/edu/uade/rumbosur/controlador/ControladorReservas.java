package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PagoInvalidoException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.excepciones.ReservaNoCancelableException;
import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.EstadoReserva;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacion;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionAgencia;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionCliente;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
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
 * Valen implementa crear, confirmar y modificar. Fran agrega, en su PR del día 2,
 * cancelar, registrar pago, registrar ajuste y controlar vencimientos.
 */
public class ControladorReservas {

    private CalculadoraPrecio calculadora;

    // Map: búsqueda rápida de una reserva por su número.
    private Map<Integer, Reserva> reservas;

    public ControladorReservas(CalculadoraPrecio calculadora) {
        this.calculadora = calculadora;
        this.reservas = new HashMap<>();
    }

    public Reserva buscarReserva(int numero) {
        return reservas.get(numero);
    }

    // ---------- Día 2 (Valen) ----------

    /** CU05 Crear reserva: tiene que seguir el diagrama de secuencia paso a paso. */
    public Reserva crearReserva(Cliente cliente, Salida salida, Vendedor vendedor, List<Pasajero> pasajeros)
            throws CupoInsuficienteException, PasajeroNoHabilitadoException {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** CU09 Confirmar reserva. */
    public void confirmarReserva(Reserva reserva) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** CU10 Modificar reserva. */
    public void modificarReserva(Reserva reserva) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
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
        LocalDate hoy = LocalDate.now();
        validarCancelable(reserva, hoy);

        Cancelacion cancelacion = new Cancelacion(reserva.getNumero(), hoy, motivo, origen);
        double importeDevuelto = obtenerPolitica(origen).calcularDevolucion(reserva, hoy);
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
        LocalDate hoy = LocalDate.now();
        Iterator<Reserva> iterador = reservas.values().iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            LocalDate fechaLimite = reserva.getSalida().getFechaInicio().minusDays(DIAS_LIMITE_SALDO);
            double saldo = reserva.getTotal() - reserva.calcularTotalPagado();
            if (reserva.ocupaCupo() && hoy.isAfter(fechaLimite) && saldo > MARGEN_SALDO) {
                reserva.anular("Saldo no abonado " + DIAS_LIMITE_SALDO + " días antes de la salida.");
            }
        }
    }
}
