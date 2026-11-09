
package ar.edu.uade.rumbosur.modelo;

import java.time.LocalDate;

public class TourCultural extends Tour {

    private static final long serialVersionUID = 1L;

    // Constructor
    public TourCultural(int codigo, String nombre, String descripcion,
                        int duracionDias, int cupoMinimo, int cupoMaximo) {

        super(codigo, nombre, descripcion,
                duracionDias, cupoMinimo, cupoMaximo);
    }

    // Devuelve la categoria del tour
    @Override
    public CategoriaTour obtenerCategoria() {
        return CategoriaTour.CULTURAL;
    }

    // Valida si un pasajero puede participar del tour
    // No tiene restricciones de edad
    @Override
    public boolean validarPasajero(
            Pasajero pasajero, LocalDate fechaSalida) {

        if (pasajero == null || fechaSalida == null) {
            return false;
        }

        LocalDate fechaNacimiento =
                pasajero.getFechaNacimiento();

        // Rechaza fechas de nacimiento inexistentes
        // o posteriores a la salida
        if (fechaNacimiento == null ||
                fechaNacimiento.isAfter(fechaSalida)) {
            return false;
        }

        return true;
    }
}
