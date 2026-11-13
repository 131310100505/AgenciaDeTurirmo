
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    // Lista operativa de pasajeros
    private List<Pasajero> listaPasajeros;

    // Constructor
    public Salida(int codigo, LocalDate fechaInicio,
                  LocalDate fechaFin, int cupoMinimo,
                  int cupoMaximo, Tour tour) {

        if (fechaInicio == null || fechaFin == null ||
                fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                    "Las fechas de la salida son invalidas");
        }

        if (tour == null) {
            throw new IllegalArgumentException(
                    "El tour es obligatorio");
        }

        if (cupoMinimo <= 0 || cupoMaximo < cupoMinimo) {
            throw new IllegalArgumentException(
                    "Los cupos son invalidos");
        }

        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.tour = tour;

        this.estado = EstadoSalida.PROGRAMADA;
        this.listaPasajerosGenerada = false;

        this.reservas = new ArrayList<>();
        this.listaPasajeros = new ArrayList<>();

        this.guia = null;
        this.vehiculo = null;
    }

    // Ordena las salidas por fecha de inicio
    @Override
    public int compareTo(Salida otraSalida) {
        return this.fechaInicio.compareTo(otraSalida.fechaInicio);
    }

    // Agrega una reserva a la salida
    public void agregarReserva(Reserva reserva) {

        if (reserva == null) {
            throw new IllegalArgumentException(
                    "La reserva no puede ser nula");
        }

        if (reserva.getSalida() != this) {
            throw new IllegalArgumentException(
                    "La reserva no pertenece a esta salida");
        }

        if (estado == EstadoSalida.CANCELADA ||
                estado == EstadoSalida.FINALIZADA ||
                estado == EstadoSalida.EN_CURSO) {
            throw new IllegalStateException(
                    "No se pueden agregar reservas a esta salida");
        }

        if (reservas.contains(reserva)) {
            return;
        }

        if (reserva.ocupaCupo() &&
                !verificarCupo(reserva.getCantidadPasajeros())) {
            throw new IllegalArgumentException(
                    "No hay cupo suficiente para esta reserva");
        }

        reservas.add(reserva);

        // Una reserva nueva invalida la lista anterior
        listaPasajerosGenerada = false;
        listaPasajeros.clear();

        actualizarEstado();
    }

    // Calcula la ocupacion de reservas pendientes y confirmadas
    // (las finalizadas tambien cuentan: ocuparon su lugar en la salida)
    public int calcularOcupacion() {

        int ocupacion = 0;

        for (Reserva reserva : reservas) {
            if (reserva.ocupaCupo()
                    || reserva.getEstado() == EstadoReserva.FINALIZADA) {
                ocupacion += reserva.getCantidadPasajeros();
            }
        }

        return ocupacion;
    }

    // Verifica si hay cupo para nuevos pasajeros
    public boolean verificarCupo(int cantidadPasajeros) {

        if (cantidadPasajeros <= 0) {
            return false;
        }

        if (estado == EstadoSalida.CANCELADA ||
                estado == EstadoSalida.FINALIZADA ||
                estado == EstadoSalida.EN_CURSO) {
            return false;
        }

        return calcularOcupacion() + cantidadPasajeros <= cupoMaximo;
    }

    // Actualiza el estado segun la ocupacion
    public void actualizarEstado() {

        if (estado == EstadoSalida.CANCELADA ||
                estado == EstadoSalida.FINALIZADA ||
                estado == EstadoSalida.EN_CURSO) {
            return;
        }

        int ocupacion = calcularOcupacion();

        if (ocupacion >= cupoMaximo) {
            estado = EstadoSalida.COMPLETA;
        } else if (ocupacion >= cupoMinimo) {
            estado = EstadoSalida.CONFIRMADA;
        } else {
            estado = EstadoSalida.PROGRAMADA;
        }
    }

    // Asigna un guia habilitado y disponible
    public void asignarGuia(Guia nuevoGuia) {

        if (nuevoGuia == null) {
            throw new IllegalArgumentException(
                    "El guia no puede ser nulo");
        }

        if (estado == EstadoSalida.EN_CURSO ||
                estado == EstadoSalida.FINALIZADA ||
                estado == EstadoSalida.CANCELADA) {
            throw new IllegalStateException(
                    "No se puede asignar un guia a esta salida");
        }

        if (!nuevoGuia.estaHabilitado(tour.obtenerCategoria())) {
            throw new IllegalArgumentException(
                    "El guia no esta habilitado para este tour");
        }

        if (this.guia == nuevoGuia) {
            return;
        }

        if (!nuevoGuia.estaDisponible(this)) {
            throw new IllegalArgumentException(
                    "El guia ya tiene una salida en esas fechas");
        }

        nuevoGuia.agregarSalida(this);

        if (this.guia != null) {
            this.guia.quitarSalida(this);
        }

        this.guia = nuevoGuia;
    }

    // Asigna un vehiculo con capacidad suficiente
    public void asignarVehiculo(Vehiculo nuevoVehiculo) {

        if (nuevoVehiculo == null) {
            throw new IllegalArgumentException(
                    "El vehiculo no puede ser nulo");
        }

        if (estado == EstadoSalida.EN_CURSO ||
                estado == EstadoSalida.FINALIZADA ||
                estado == EstadoSalida.CANCELADA) {
            throw new IllegalStateException(
                    "No se puede asignar un vehiculo a esta salida");
        }

        if (!nuevoVehiculo.tieneCapacidad(cupoMaximo)) {
            throw new IllegalArgumentException(
                    "El vehiculo no tiene capacidad suficiente");
        }

        this.vehiculo = nuevoVehiculo;
    }

    // Genera la lista operativa con pasajeros de reservas confirmadas
    public void generarListaPasajeros() {

        actualizarEstado();

        if (estado != EstadoSalida.CONFIRMADA &&
                estado != EstadoSalida.COMPLETA) {
            throw new IllegalStateException(
                    "La salida debe estar confirmada");
        }

        List<Pasajero> nuevaLista = new ArrayList<>();

        for (Reserva reserva : reservas) {
            if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                nuevaLista.addAll(reserva.getPasajeros());
            }
        }

        listaPasajeros = nuevaLista;
        listaPasajerosGenerada = true;
    }

    // Inicia una salida confirmada y preparada
    public void iniciarSalida() {

        actualizarEstado();

        if (estado != EstadoSalida.CONFIRMADA &&
                estado != EstadoSalida.COMPLETA) {
            throw new IllegalStateException(
                    "La salida no esta confirmada");
        }

        if (guia == null || vehiculo == null) {
            throw new IllegalStateException(
                    "Falta asignar guia o vehiculo");
        }

        if (!listaPasajerosGenerada) {
            throw new IllegalStateException(
                    "Falta generar la lista de pasajeros");
        }

        estado = EstadoSalida.EN_CURSO;
    }

    // Finaliza una salida en curso
    public void finalizarSalida() {

        if (estado != EstadoSalida.EN_CURSO) {
            throw new IllegalStateException(
                    "Solo se puede finalizar una salida en curso");
        }

        estado = EstadoSalida.FINALIZADA;

        // Las reservas confirmadas de la salida tambien terminan
        for (Reserva reserva : reservas) {
            reserva.finalizar();
        }
    }

    // Cancela una salida que no comenzo
    public void cancelarSalida() {

        if (estado == EstadoSalida.EN_CURSO ||
                estado == EstadoSalida.FINALIZADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una salida iniciada");
        }

        if (estado == EstadoSalida.CANCELADA) {
            throw new IllegalStateException(
                    "La salida ya esta cancelada");
        }

        estado = EstadoSalida.CANCELADA;
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
        actualizarEstado();
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

    // Devuelve las reservas sin permitir modificaciones externas
    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // Devuelve la lista operativa sin permitir modificaciones externas
    public List<Pasajero> getListaPasajeros() {
        return Collections.unmodifiableList(listaPasajeros);
    }
}
