
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalTime;

public class Actividad implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos de la actividad
    private int codigo;
    private int dia;
    private int orden;
    private LocalTime horario;
    private String descripcion;
    private String lugar;
    private TipoActividad tipo;

    // Constructor
    public Actividad(int codigo, int dia, int orden,
                     LocalTime horario, String descripcion,
                     String lugar, TipoActividad tipo) {

        this.codigo = codigo;
        this.dia = dia;
        this.orden = orden;
        this.horario = horario;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.tipo = tipo;
    }

    // Getters
    public int getCodigo() {
        return codigo;
    }

    public int getDia() {
        return dia;
    }

    public int getOrden() {
        return orden;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public TipoActividad getTipo() {
        return tipo;
    }
}
