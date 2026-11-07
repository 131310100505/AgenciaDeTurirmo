package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reserva de un cliente para una salida, con uno o más pasajeros.
 * El cálculo del precio NO está acá: lo hace CalculadoraPrecio (SRP).
 */
public class Reserva implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    // static: es un contador compartido por todas las reservas, no un dato de cada una.
    private static int siguienteNumero = 1;

    private int numero;
    private LocalDate fechaCreacion;
    private EstadoReserva estado;
    private double total;

    // Asociaciones: cada reserva tiene exactamente un cliente, una salida y un vendedor.
    private Cliente cliente;
    private Salida salida;
    private Vendedor vendedor;

    // Colecciones declaradas por interfaz (List) y creadas con ArrayList.
    private List<Pasajero> pasajeros;
    private List<Pago> pagos;                       // composición: los pagos pertenecen a la reserva
    private List<AdicionalContratado> adicionales;  // composición

    /** Constructor del mensaje «create» Reserva(cliente, salida, vendedor) del diagrama CU05. */
    public Reserva(Cliente cliente, Salida salida, Vendedor vendedor) {
        if (cliente == null || salida == null || vendedor == null) {
            throw new IllegalArgumentException("La reserva necesita cliente, salida y vendedor.");
        }
        this.numero = siguienteNumero++;
        this.fechaCreacion = LocalDate.now();
        this.estado = EstadoReserva.PENDIENTE; // supuesto S06
        this.total = 0;
        this.cliente = cliente;
        this.salida = salida;
        this.vendedor = vendedor;
        this.pasajeros = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.adicionales = new ArrayList<>();
    }

    public void agregarPasajero(Pasajero pasajero) {
        if (pasajero == null) {
            throw new IllegalArgumentException("El pasajero no puede ser nulo.");
        }
        if (pasajeros.contains(pasajero)) { // usa equals() de Pasajero (por documento)
            throw new IllegalArgumentException("El pasajero " + pasajero.getDocumento()
                    + " ya está en la reserva.");
        }
        pasajeros.add(pasajero);
    }

    public void agregarAdicional(AdicionalContratado adicional) {
        if (adicional == null) {
            throw new IllegalArgumentException("El adicional no puede ser nulo.");
        }
        adicionales.add(adicional);
    }

    /** Los pagos solo se agregan, nunca se borran (las correcciones son AjustePago). */
    public void registrarPago(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser nulo.");
        }
        pagos.add(pago);
    }

    /** Suma lo efectivamente pagado, ya descontados los ajustes de cada pago. */
    public double calcularTotalPagado() {
        double totalPagado = 0;
        for (Pago pago : pagos) {
            totalPagado += pago.getImporteNeto();
        }
        return totalPagado;
    }

    /**
     * Indica si la reserva ocupa lugares en la salida.
     * Pendientes y confirmadas ocupan cupo (supuesto S07); anuladas no.
     * La usa Salida.calcularOcupacion().
     */
    public boolean ocupaCupo() {
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA;
    }

    public int getCantidadPasajeros() {
        return pasajeros.size();
    }

    // ---------- Día 2 ----------

    /** Confirma la reserva si los pagos alcanzan la seña del 30 %. */
    public void confirmar() {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** Reemplaza un pasajero hasta 3 días antes de la salida. */
    public void reemplazarPasajero(Pasajero actual, Pasajero nuevo) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    /** Anula la reserva y libera sus lugares (deja de ocupar cupo). */
    public void anular(String motivo) {
        throw new UnsupportedOperationException("Pendiente: se implementa el día 2.");
    }

    // ---------- Getters y setters ----------

    public int getNumero() {
        return numero;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getTotal() {
        return total;
    }

    /** Lo usa el controlador con el total que calcula CalculadoraPrecio. */
    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Salida getSalida() {
        return salida;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public List<Pasajero> getPasajeros() {
        return Collections.unmodifiableList(pasajeros);
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    public List<AdicionalContratado> getAdicionales() {
        return Collections.unmodifiableList(adicionales);
    }

    /**
     * Los atributos static no se guardan al serializar. Después de cargar los datos
     * del archivo, el Repositorio llama a este método para que el contador siga
     * desde el último número y no se repitan.
     */
    public static void actualizarSiguienteNumero(int ultimoNumeroUsado) {
        siguienteNumero = ultimoNumeroUsado + 1;
    }

    @Override
    public String toString() {
        return "Reserva " + numero + " - " + cliente.getApellido() + " - "
                + pasajeros.size() + " pasajero(s) - " + estado + " - $" + total;
    }
}
