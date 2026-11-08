
package ar.edu.uade.rumbosur.modelo;

import java.time.LocalDate;

public class TourAventura extends Tour {

    private static final long serialVersionUID = 1L;

    // Atributos propios de los tours de aventura
    private int edadMinima;
    private String dificultad;

    // Constructor
    public TourAventura(int codigo, String nombre, String descripcion,
                        int duracionDias, int cupoMinimo, int cupoMaximo,
                        int edadMinima, String dificultad) {

        super(codigo, nombre, descripcion,
                duracionDias, cupoMinimo, cupoMaximo);

        this.edadMinima = edadMinima;
        this.dificultad = dificultad;
    }

    // Devuelve la categoría del tour
    @Override
    public CategoriaTour obtenerCategoria() {
        return CategoriaTour.AVENTURA;
    }

    // Valida si el pasajero puede participar del tour
    @Override
    public boolean validarPasajero(Pasajero pasajero, LocalDate fechaSalida) {
        // La validación de edad se implementa el domingo
        return false;
    }

    // Getters
    public int getEdadMinima() {
        return edadMinima;
    }

    public String getDificultad() {
        return dificultad;
    }
}
