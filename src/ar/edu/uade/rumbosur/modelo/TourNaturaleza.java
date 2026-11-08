
package ar.edu.uade.rumbosur.modelo;

import java.time.LocalDate;

public class TourNaturaleza extends Tour {

    private static final long serialVersionUID = 1L;

    // Constructor
    public TourNaturaleza(int codigo, String nombre, String descripcion,
                          int duracionDias, int cupoMinimo, int cupoMaximo) {

        super(codigo, nombre, descripcion,
                duracionDias, cupoMinimo, cupoMaximo);
    }

    // Devuelve la categoría del tour
    @Override
    public CategoriaTour obtenerCategoria() {
        return CategoriaTour.NATURALEZA;
    }

    // Valida si el pasajero puede participar del tour
    @Override
    public boolean validarPasajero(Pasajero pasajero, LocalDate fechaSalida) {
        // La lógica se implementa el domingo
        return false;
    }
}
