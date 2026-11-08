
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Tour implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos principales del tour
    protected int codigo;
    protected String nombre;
    protected String descripcion;
    protected int duracionDias;
    protected int cupoMinimo;
    protected int cupoMaximo;

    // Actividades y tarifas asociadas al tour
    protected List<Actividad> actividades;
    protected List<Tarifa> tarifas;

    // Constructor
    public Tour(int codigo, String nombre, String descripcion,
                int duracionDias, int cupoMinimo, int cupoMaximo) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;

        this.actividades = new ArrayList<>();
        this.tarifas = new ArrayList<>();
    }

    // Cada tipo de tour define su categoría
    public abstract CategoriaTour obtenerCategoria();

    // Cada tipo de tour valida si un pasajero puede participar
    public abstract boolean validarPasajero(Pasajero pasajero, LocalDate fechaSalida);

    // Agrega una actividad al tour
    public void agregarActividad(Actividad actividad) {
        // Se implementa el domingo
    }

    // Agrega una tarifa al tour
    public void agregarTarifa(Tarifa tarifa) {
        // Se implementa el domingo
    }

    // Busca la tarifa vigente para una fecha
    public Tarifa obtenerTarifaVigente(LocalDate fecha) {
        // Se implementa el domingo
        return null;
    }
}
