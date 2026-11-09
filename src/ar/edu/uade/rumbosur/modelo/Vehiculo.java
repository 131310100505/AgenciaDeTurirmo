
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

        if (patente == null || patente.isBlank()) {
            throw new IllegalArgumentException(
                    "La patente no puede estar vacía"
            );
        }

        if (capacidad <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor a cero"
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de vehículo es obligatorio"
            );
        }

        this.patente = patente;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    // Verifica si el vehículo puede transportar
    // la cantidad de pasajeros indicada
    public boolean tieneCapacidad(int cantidadPasajeros) {

        if (cantidadPasajeros <= 0) {
            return false;
        }

        return cantidadPasajeros <= capacidad;
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
