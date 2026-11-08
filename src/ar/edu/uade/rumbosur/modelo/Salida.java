
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Salida implements Serializable, Comparable<Salida> {

    private static final long serialVersionUID = 1L;

    // Atributos principales de la salida
    private int codigo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupoMinimo;
    private int cupoMaximo;
    private EstadoSalida estado;
    private boolean listaPasajerosGenerada;

    // Relaciones con otras clases
    private Tour tour;
    private Guia guia;
    private Vehiculo vehiculo;
    private List<Reserva> reservas;

    // Constructor
    public Salida(int codigo, LocalDate fechaInicio,
                  LocalDate fechaFin, int cupoMinimo,
                  int cupoMaximo, Tour tour) {

        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.tour = tour;

        this.estado = EstadoSalida.PROGRAMADA;
        this.listaPasajerosGenerada = false;
        this.reservas = new ArrayList<>();

        this.guia = null;
        this.vehiculo = null;
    }

    // Ordena las salidas por fecha de inicio
    @Override
    public int compareTo(Salida otraSalida) {
        return this.fechaInicio.compareTo(otraSalida.fechaInicio);
    }

    // Métodos pendientes de implementación
    public int calcularOcupacion() {
        // Se implementa el domingo
        return 0;
    }

    public boolean verificarCupo(int cantidadPasajeros) {
        // Se implementa el domingo
        return false;
    }

    public void actualizarEstado() {
        // Se implementa el domingo
    }

    public void asignarGuia(Guia guia) {
        // Se implementa el domingo
    }

    public void asignarVehiculo(Vehiculo vehiculo) {
        // Se implementa el domingo
    }

    public void generarListaPasajeros() {
        // Se implementa más adelante
    }

    public void iniciarSalida() {
        // Se implementa el domingo
    }

    public void finalizarSalida() {
        // Se implementa el domingo
    }

    public void cancelarSalida() {
        // Se implementa el domingo
    }

    // Getters
    public int getCodigo() {
        return codigo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCupoMinimo() {
        return cupoMinimo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public EstadoSalida getEstado() {
        return estado;
    }

    public boolean isListaPasajerosGenerada() {
        return listaPasajerosGenerada;
    }

    public Tour getTour() {
        return tour;
    }

    public Guia getGuia() {
        return guia;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }
}
