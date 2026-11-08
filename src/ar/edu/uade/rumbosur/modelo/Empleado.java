
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

public abstract class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos comunes a todos los empleados
    protected int legajo;
    protected String nombre;
    protected String documento;
    protected String telefono;

    // Constructor
    public Empleado(int legajo, String nombre,
                    String documento, String telefono) {

        this.legajo = legajo;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
    }

    // Getters
    public int getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }
}
