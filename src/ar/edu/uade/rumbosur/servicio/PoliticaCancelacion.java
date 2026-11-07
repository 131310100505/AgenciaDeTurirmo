package ar.edu.uade.rumbosur.servicio;

import ar.edu.uade.rumbosur.modelo.Reserva;

import java.time.LocalDate;

/**
 * Contrato para calcular cuánto se devuelve al cancelar una reserva.
 *
 * SOLID - OCP: para agregar una política nueva se crea otra clase que implemente
 * esta interfaz, sin modificar las que ya existen ni ControladorReservas.
 * SOLID - DIP: el controlador depende de esta abstracción y no de las clases concretas.
 * GRASP - Polimorfismo: cada implementación resuelve el cálculo a su manera.
 *
 * La firma es la del modelo de clases final (reserva, fechaCancelacion).
 */
public interface PoliticaCancelacion {

    /**
     * Devuelve el importe a reintegrar. Se calcula sobre lo efectivamente pagado
     * (Reserva.calcularTotalPagado(), ya con ajustes) y nunca lo supera.
     */
    double calcularDevolucion(Reserva reserva, LocalDate fechaCancelacion);
}
