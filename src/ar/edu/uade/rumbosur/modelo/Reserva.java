package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Reserva de un cliente para una salida, con uno o más pasajeros.
 * El cálculo del precio NO está acá: lo hace CalculadoraPrecio (SRP).
 */
public class Reserva implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    // Constantes de las reglas de negocio (static final: una sola copia, no cambia).
    private static final double PORCENTAJE_SENA = 0.30;        // supuesto S17
    private static final int DIAS_MINIMOS_REEMPLAZO = 3;       // supuesto S10
    private static final double MARGEN = 0.01;                 // un centavo, por el redondeo de double

    // static: es un contador compartido por todas las reservas, no un dato de cada una.
    private static int siguienteNumero = 1;

    private int numero;
    private LocalDate fechaCreacion;
    private EstadoReserva estado;
    private double total;
    private String motivoAnulacion;

    // Asociaciones: cada reserva tiene exactamente un cliente, una salida y un vendedor.
    private Cliente cliente;
    private Salida salida;
    private Vendedor vendedor;

    // Colecciones declaradas por interfaz (List) y creadas con ArrayList.
    private List<Pasajero> pasajeros;
    private List<Pago> pagos;                       // composición: los pagos pertenecen a la reserva
    private List<AdicionalContratado> adicionales;  // composición
    private List<String> historial;                 // registro de cambios (supuesto S10)

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
        this.historial = new ArrayList<>();
        registrarEnHistorial("Reserva creada en estado " + estado);
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
        registrarEnHistorial("Se agrega el pasajero " + pasajero.getNombre());
    }

    public void agregarAdicional(AdicionalContratado adicional) {
        if (adicional == null) {
            throw new IllegalArgumentException("El adicional no puede ser nulo.");
        }
        if (!pasajeros.contains(adicional.getPasajero())) {
            throw new IllegalArgumentException("El pasajero del adicional no está en la reserva.");
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

    /** Seña mínima para confirmar: 30 % del total exigible (supuesto S17). */
    public double calcularSenaMinima() {
        return total * PORCENTAJE_SENA;
    }

    /** Lo que falta pagar. Nunca es negativo. */
    public double calcularSaldo() {
        return Math.max(0, total - calcularTotalPagado());
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

    /**
     * CU09: confirma la reserva si los pagos netos alcanzan la seña del 30 %.
     * Devuelve true si quedó confirmada y false si sigue pendiente.
     *
     * Que no alcance la seña es una situación previsible, por eso se resuelve
     * con una validación y no con una excepción (material "Excepciones en Java":
     * no usar excepciones como control de flujo).
     */
    public boolean confirmar() {
        if (estado != EstadoReserva.PENDIENTE) {
            return false;
        }
        if (calcularTotalPagado() + MARGEN < calcularSenaMinima()) {
            return false;
        }
        estado = EstadoReserva.CONFIRMADA;
        registrarEnHistorial("Reserva confirmada con $" + calcularTotalPagado() + " pagados");
        return true;
    }

    /**
     * CU11: reemplaza un pasajero hasta 3 días antes de la salida (supuesto S10).
     * Las restricciones del tour sobre el nuevo pasajero las valida antes el controlador.
     * Los adicionales del pasajero reemplazado se quitan; el controlador vuelve a
     * contratar el seguro obligatorio si el tour es de aventura.
     */
    public void reemplazarPasajero(Pasajero actual, Pasajero nuevo, LocalDate fecha) {
        if (!ocupaCupo()) {
            throw new IllegalArgumentException("La reserva " + numero + " está " + estado
                    + " y no admite cambios.");
        }
        int posicion = pasajeros.indexOf(actual);
        if (posicion == -1) {
            throw new IllegalArgumentException("El pasajero a reemplazar no está en la reserva.");
        }
        if (pasajeros.contains(nuevo)) {
            throw new IllegalArgumentException("El nuevo pasajero ya está en la reserva.");
        }
        long diasHastaSalida = ChronoUnit.DAYS.between(fecha, salida.getFechaInicio());
        if (diasHastaSalida < DIAS_MINIMOS_REEMPLAZO) {
            throw new IllegalArgumentException("Solo se puede reemplazar un pasajero hasta "
                    + DIAS_MINIMOS_REEMPLAZO + " días antes de la salida.");
        }
        pasajeros.set(posicion, nuevo);
        quitarAdicionalesDe(actual);
        registrarEnHistorial("Se reemplaza a " + actual.getNombre() + " por " + nuevo.getNombre());
    }

    /**
     * CU16: cuando la salida termina, la reserva confirmada pasa a FINALIZADA.
     * Devuelve false si la reserva no estaba confirmada (por ejemplo, si estaba anulada).
     */
    public boolean finalizar() {
        if (estado != EstadoReserva.CONFIRMADA) {
            return false;
        }
        estado = EstadoReserva.FINALIZADA;
        registrarEnHistorial("Reserva finalizada: la salida terminó");
        return true;
    }

    /**
     * Anula la reserva y libera sus lugares: al quedar ANULADA deja de ocupar cupo.
     * La usan la cancelación (CU12) y el control de vencimientos (CU13).
     */
    public void anular(String motivo) {
        if (!ocupaCupo()) {
            throw new IllegalArgumentException("La reserva " + numero + " ya está " + estado + ".");
        }
        estado = EstadoReserva.ANULADA;
        motivoAnulacion = motivo;
        registrarEnHistorial("Reserva anulada: " + motivo);
    }

    // Recorre con Iterator porque elimina mientras recorre
    // (con for-each daría ConcurrentModificationException).
    private void quitarAdicionalesDe(Pasajero pasajero) {
        Iterator<AdicionalContratado> iterador = adicionales.iterator();
        while (iterador.hasNext()) {
            AdicionalContratado adicional = iterador.next();
            if (adicional.getPasajero().equals(pasajero)) {
                iterador.remove();
            }
        }
    }

    private void registrarEnHistorial(String descripcion) {
        historial.add(LocalDate.now() + " - " + descripcion);
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
        if (total < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo.");
        }
        this.total = total;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
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

    public List<String> getHistorial() {
        return Collections.unmodifiableList(historial);
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
                + pasajeros.size() + " pasajero(s) - " + estado + " - $" + String.format("%.2f", total);
    }
}
