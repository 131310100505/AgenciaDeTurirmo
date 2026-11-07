package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Vendedor;
import ar.edu.uade.rumbosur.servicio.CalculadoraPrecio;

import java.util.HashMap;
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
}
