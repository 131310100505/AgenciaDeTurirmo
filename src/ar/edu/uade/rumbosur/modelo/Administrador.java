
package ar.edu.uade.rumbosur.modelo;

public class Administrador extends Empleado {

    private static final long serialVersionUID = 1L;

    // Constructor
    public Administrador(int legajo, String nombre,
                         String documento, String telefono) {

        super(legajo, nombre, documento, telefono);
    }
}
