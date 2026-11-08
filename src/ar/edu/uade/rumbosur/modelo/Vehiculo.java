
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos del vehículo
    private String patente;
    private int capacidad;
    private TipoVehiculo tipo;

    // Constructor
    public Vehiculo(String patente, int capacidad, TipoVehiculo tipo) {
        this.patente = patente;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    // Verifica si el vehículo tiene capacidad suficiente
    public boolean tieneCapacidad(int cantidadPasajeros) {
        // La lógica se implementa el domingo
        return false;
    }

    // Getters
    public String getPatente() {
        return patente;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }
}
