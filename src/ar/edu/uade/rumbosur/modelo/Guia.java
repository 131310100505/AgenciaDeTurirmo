
package ar.edu.uade.rumbosur.modelo;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.time.LocalDate;

public class Guia extends Empleado {

    private static final long serialVersionUID = 1L;

    // Atributos propios del guia
    private List<String> idiomas;
    private Set<CategoriaTour> categoriasHabilitadas;
    private double honorarioPorDia;

    // Salidas asignadas al guia
    private List<Salida> salidasAsignadas;

    // Constructor
    public Guia(int legajo, String nombre, String documento,
                String telefono, List<String> idiomas,
                Set<CategoriaTour> categoriasHabilitadas,
                double honorarioPorDia) {

        super(legajo, nombre, documento, telefono);

        if (idiomas == null || categoriasHabilitadas == null) {
            throw new IllegalArgumentException(
                    "Los idiomas y categorias no pueden ser nulos");
        }

        if (honorarioPorDia < 0) {
            throw new IllegalArgumentException(
                    "El honorario no puede ser negativo");
        }

        this.idiomas = new ArrayList<>(idiomas);
        this.categoriasHabilitadas = new HashSet<>(categoriasHabilitadas);
        this.honorarioPorDia = honorarioPorDia;
        this.salidasAsignadas = new ArrayList<>();
    }

    // Verifica si el guia esta habilitado para una categoria
    public boolean estaHabilitado(CategoriaTour categoria) {
        return categoria != null &&
                categoriasHabilitadas.contains(categoria);
    }

    // Verifica si el guia esta disponible para una salida
    public boolean estaDisponible(Salida salida) {

        if (salida == null) {
            return false;
        }

        LocalDate inicioNuevo = salida.getFechaInicio();
        LocalDate finNuevo = salida.getFechaFin();

        if (inicioNuevo == null || finNuevo == null ||
                inicioNuevo.isAfter(finNuevo)) {
            return false;
        }

        for (Salida asignada : salidasAsignadas) {

            if (asignada == salida) {
                continue;
            }

            // Una salida cancelada no ocupa al guia
            if (asignada.getEstado() == EstadoSalida.CANCELADA) {
                continue;
            }

            LocalDate inicioExistente = asignada.getFechaInicio();
            LocalDate finExistente = asignada.getFechaFin();

            // Comprueba si las fechas se superponen
            boolean seSuperponen =
                    !inicioNuevo.isAfter(finExistente) &&
                            !finNuevo.isBefore(inicioExistente);

            if (seSuperponen) {
                return false;
            }
        }

        return true;
    }

    // Registra una salida asignada al guia
    public void agregarSalida(Salida salida) {

        if (salida == null) {
            throw new IllegalArgumentException(
                    "La salida no puede ser nula");
        }

        if (!estaDisponible(salida)) {
            throw new IllegalArgumentException(
                    "El guia no esta disponible para esas fechas");
        }

        if (!salidasAsignadas.contains(salida)) {
            salidasAsignadas.add(salida);
        }
    }

    // Elimina una salida cuando se reasigna a otro guia
    public void quitarSalida(Salida salida) {

        if (salida == null) {
            throw new IllegalArgumentException(
                    "La salida no puede ser nula");
        }

        salidasAsignadas.remove(salida);
    }

    // Getters
    public List<String> getIdiomas() {
        return new ArrayList<>(idiomas);
    }

    public Set<CategoriaTour> getCategoriasHabilitadas() {
        return new HashSet<>(categoriasHabilitadas);
    }

    public double getHonorarioPorDia() {
        return honorarioPorDia;
    }

    public List<Salida> getSalidasAsignadas() {
        return new ArrayList<>(salidasAsignadas);
    }
}
