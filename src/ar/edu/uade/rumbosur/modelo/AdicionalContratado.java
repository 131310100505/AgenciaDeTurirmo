package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

/**
 * Contratación de un servicio adicional para un pasajero de una reserva.
 * Guarda el precio aplicado en el momento de contratar, para que un cambio
 * posterior de precio no altere reservas ya hechas.
 */
public class AdicionalContratado implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private ServicioAdicional servicio;
    private Pasajero pasajero;
    private int cantidad;
    private double precioUnitarioAplicado;

    public AdicionalContratado(int codigo, ServicioAdicional servicio, Pasajero pasajero, int cantidad) {
        if (servicio == null || pasajero == null) {
            throw new IllegalArgumentException("El adicional necesita un servicio y un pasajero.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.codigo = codigo;
        this.servicio = servicio;
        this.pasajero = pasajero;
        this.cantidad = cantidad;
        this.precioUnitarioAplicado = servicio.obtenerPrecio();
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitarioAplicado;
    }

    public int getCodigo() {
        return codigo;
    }

    public ServicioAdicional getServicio() {
        return servicio;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitarioAplicado() {
        return precioUnitarioAplicado;
    }

    @Override
    public String toString() {
        return servicio.getNombre() + " x" + cantidad + " para " + pasajero.getNombre()
                + " - $" + calcularSubtotal();
    }
}
