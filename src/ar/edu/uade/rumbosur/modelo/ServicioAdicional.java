package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

/**
 * Servicio que se puede contratar además del tour:
 * excursiones opcionales, traslados o seguro de viaje.
 */
public class ServicioAdicional implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nombre;
    private double precio; // por pasajero
    private TipoAdicional tipo;

    public ServicioAdicional(int codigo, String nombre, double precio, TipoAdicional tipo) {
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
    }

    public double obtenerPrecio() {
        return precio;
    }

    /** El seguro es el adicional obligatorio en los tours de aventura. */
    public boolean esSeguro() {
        return tipo == TipoAdicional.SEGURO;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoAdicional getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ") - $" + precio;
    }
}
