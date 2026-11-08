
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

    // Devuelve la categoría del tour
    @Override
    public CategoriaTour obtenerCategoria() {
        return CategoriaTour.CULTURAL;
    }

    // Valida si un pasajero puede participar del tour
    @Override
    public boolean validarPasajero(Pasajero pasajero, LocalDate fechaSalida) {
        // La lógica se implementa el domingo
        return false;
    }
}
