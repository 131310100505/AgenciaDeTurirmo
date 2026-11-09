
package ar.edu.uade.rumbosur.modelo;

public class Vendedor extends Empleado {

    private static final long serialVersionUID = 1L;

    // Atributo propio del vendedor
    private double porcentajeComision;

    // Constructor
    public Vendedor(int legajo, String nombre, String documento,
                    String telefono, double porcentajeComision) {

        super(legajo, nombre, documento, telefono);

        this.porcentajeComision = porcentajeComision;
    }

    // Getter
    public double getPorcentajeComision() {
        return porcentajeComision;
    }
}
