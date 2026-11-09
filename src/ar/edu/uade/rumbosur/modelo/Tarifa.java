
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Tarifa implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos de la tarifa
    private int codigo;
    private Temporada temporada;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private double precioBase;

    // Constructor
    public Tarifa(int codigo, Temporada temporada,
                  LocalDate fechaInicioVigencia,
                  LocalDate fechaFinVigencia,
                  double precioBase) {

        this.codigo = codigo;
        this.temporada = temporada;
        this.fechaInicioVigencia = fechaInicioVigencia;
        this.fechaFinVigencia = fechaFinVigencia;
        this.precioBase = precioBase;
    }

    // Getters
    public int getCodigo() {
        return codigo;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public double getPrecioBase() {
        return precioBase;
    }
}
