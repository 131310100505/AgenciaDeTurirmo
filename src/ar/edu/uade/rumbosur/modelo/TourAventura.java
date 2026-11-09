
package ar.edu.uade.rumbosur.modelo;

import java.time.LocalDate;
import java.time.Period;

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

        if (edadMinima < 0) {
            throw new IllegalArgumentException(
                    "La edad minima no puede ser negativa");
        }

        this.edadMinima = edadMinima;
        this.dificultad = dificultad;
    }

    // Devuelve la categoria del tour
    @Override
    public CategoriaTour obtenerCategoria() {
        return CategoriaTour.AVENTURA;
    }

    // Valida si el pasajero cumple la edad minima
    // La edad se calcula a la fecha de inicio de la salida
    @Override
    public boolean validarPasajero(
            Pasajero pasajero, LocalDate fechaSalida) {

        if (pasajero == null || fechaSalida == null) {
            return false;
        }

        LocalDate fechaNacimiento =
                pasajero.getFechaNacimiento();

        if (fechaNacimiento == null ||
                fechaNacimiento.isAfter(fechaSalida)) {
            return false;
        }

        int edad = Period.between(
                fechaNacimiento, fechaSalida
        ).getYears();

        return edad >= edadMinima;
    }

    // Getters
    public int getEdadMinima() {
        return edadMinima;
    }

    public String getDificultad() {
        return dificultad;
    }
}
