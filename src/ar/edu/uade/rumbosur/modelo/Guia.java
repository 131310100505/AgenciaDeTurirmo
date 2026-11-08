
package ar.edu.uade.rumbosur.modelo;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class Guia extends Empleado {

    private static final long serialVersionUID = 1L;

    // Atributos propios del guía
    private List<String> idiomas;
    private Set<CategoriaTour> categoriasHabilitadas;
    private double honorarioPorDia;

    // Constructor
    public Guia(int legajo, String nombre, String documento,
                String telefono, List<String> idiomas,
                Set<CategoriaTour> categoriasHabilitadas,
                double honorarioPorDia) {

        super(legajo, nombre, documento, telefono);

        this.idiomas = new ArrayList<>(idiomas);
        this.categoriasHabilitadas = new HashSet<>(categoriasHabilitadas);
        this.honorarioPorDia = honorarioPorDia;
    }

    // Verifica si el guía está habilitado para una categoría
    public boolean estaHabilitado(CategoriaTour categoria) {
        // La lógica se implementa el domingo
        return false;
    }

    // Verifica si el guía está disponible para una salida
    public boolean estaDisponible(Salida salida) {
        // La lógica se implementa el domingo
        return false;
    }

    // Getters
    public List<String> getIdiomas() {
        return idiomas;
    }

    public Set<CategoriaTour> getCategoriasHabilitadas() {
        return categoriasHabilitadas;
    }

    public double getHonorarioPorDia() {
        return honorarioPorDia;
    }
}
