
package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.TransicionEstadoInvalidaException;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.EstadoSalida;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Liquidacion;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vehiculo;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionAgencia;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ControladorSalidas {

    // Lista de salidas administradas por el controlador
    private final List<Salida> salidas;

    // Politica de devolucion por cancelacion de la agencia
    private final PoliticaCancelacionAgencia politicaCancelacion;

    // Liquidaciones de guias: como maximo una por salida, se buscan por codigo de salida
    private final Map<Integer, Liquidacion> liquidaciones = new LinkedHashMap<>();

    // Cancelaciones hechas por la agencia, por numero de reserva
    private final Map<Integer, Cancelacion> cancelaciones = new LinkedHashMap<>();

    // Constructor
    public ControladorSalidas() {
        this.salidas = new ArrayList<>();
        this.politicaCancelacion = new PoliticaCancelacionAgencia();
    }

    // Reemplaza las salidas por las que tiene el repositorio
    public void cargarDesde(Repositorio repositorio) {
        salidas.clear();
        salidas.addAll(repositorio.listarSalidas());
        liquidaciones.clear();
        for (Liquidacion liquidacion : repositorio.listarLiquidaciones()) {
            liquidaciones.put(liquidacion.getCodigo(), liquidacion);
        }
    }

    // Le pasa al repositorio las salidas, antes de guardar el archivo
    public void guardarEn(Repositorio repositorio) {
        for (Salida salida : salidas) {
            repositorio.agregarSalida(salida);
        }
        for (Liquidacion liquidacion : liquidaciones.values()) {
            repositorio.agregarLiquidacion(liquidacion);
        }
        // Al cargar, todas las cancelaciones quedan en ControladorReservas
        for (Cancelacion cancelacion : cancelaciones.values()) {
            repositorio.agregarCancelacion(cancelacion);
        }
    }

    // CU17: cierra una salida finalizada y liquida los honorarios del guia
    public Liquidacion cerrarSalida(int codigoSalida) throws TransicionEstadoInvalidaException {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        if (salida.getEstado() != EstadoSalida.FINALIZADA) {
            throw new TransicionEstadoInvalidaException(
                    "La salida " + codigoSalida + " todavia no finalizo");
        }

        if (liquidaciones.containsKey(codigoSalida)) {
            throw new TransicionEstadoInvalidaException(
                    "La salida " + codigoSalida + " ya fue liquidada");
        }

        // La liquidacion usa el codigo de la salida: hay una como maximo por salida
        Liquidacion liquidacion = new Liquidacion(
                codigoSalida, LocalDate.now(), salida, salida.getGuia());
        liquidacion.calcularTotal();
        liquidaciones.put(codigoSalida, liquidacion);

        return liquidacion;
    }

    public List<Liquidacion> listarLiquidaciones() {
        return new ArrayList<>(liquidaciones.values());
    }

    // Deja registrada la devolucion de una reserva anulada por la agencia
    private void registrarCancelacionAgencia(Reserva reserva, LocalDate fecha, double importe) {

        Cancelacion cancelacion = new Cancelacion(reserva.getNumero(), fecha,
                "Cancelacion de la agencia por cupo minimo", OrigenCancelacion.AGENCIA);
        cancelacion.registrarDevolucion(importe);
        cancelaciones.put(reserva.getNumero(), cancelacion);
    }

    public List<Cancelacion> listarCancelaciones() {
        return new ArrayList<>(cancelaciones.values());
    }

    // Crea y registra una nueva salida
    public Salida crearSalida(int codigo, LocalDate fechaInicio,
                              LocalDate fechaFin, int cupoMinimo,
                              int cupoMaximo, Tour tour) {

        if (buscarSalida(codigo) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una salida con ese codigo");
        }

        Salida nuevaSalida = new Salida(
                codigo, fechaInicio, fechaFin,
                cupoMinimo, cupoMaximo, tour
        );

        salidas.add(nuevaSalida);
        return nuevaSalida;
    }

    // Busca una salida por su codigo
    public Salida buscarSalida(int codigo) {

        for (Salida salida : salidas) {
            if (salida.getCodigo() == codigo) {
                return salida;
            }
        }

        return null;
    }

    // Devuelve una salida existente o informa un error
    private Salida obtenerSalidaObligatoria(int codigo) {

        Salida salida = buscarSalida(codigo);

        if (salida == null) {
            throw new IllegalArgumentException(
                    "No existe una salida con ese codigo");
        }

        return salida;
    }

    // Devuelve todas las salidas ordenadas por fecha
    public List<Salida> listarSalidas() {

        List<Salida> resultado = new ArrayList<>(salidas);

        Collections.sort(resultado);

        return resultado;
    }

    // Devuelve las salidas asociadas a un tour
    public List<Salida> listarSalidasPorTour(Tour tour) {

        if (tour == null) {
            throw new IllegalArgumentException(
                    "El tour no puede ser nulo");
        }

        List<Salida> resultado = new ArrayList<>();

        for (Salida salida : salidas) {
            if (salida.getTour() == tour) {
                resultado.add(salida);
            }
        }

        Collections.sort(resultado);
        return resultado;
    }

    // Consulta la ocupacion actual de una salida
    public int consultarOcupacion(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        return salida.calcularOcupacion();
    }

    // Verifica si una salida admite nuevos pasajeros
    public boolean verificarCupo(int codigoSalida,
                                 int cantidadPasajeros) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        return salida.verificarCupo(cantidadPasajeros);
    }

    // Actualiza el estado de una salida
    public void actualizarEstado(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.actualizarEstado();
    }

    // Asigna un guia a una salida
    public void asignarGuia(int codigoSalida, Guia guia) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.asignarGuia(guia);
    }

    // Asigna un vehiculo a una salida
    public void asignarVehiculo(int codigoSalida,
                                Vehiculo vehiculo) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.asignarVehiculo(vehiculo);
    }

    // Genera la lista de pasajeros de una salida
    public void generarListaPasajeros(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.generarListaPasajeros();
    }

    // Inicia una salida
    public void iniciarSalida(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.iniciarSalida();
    }

    // Finaliza una salida
    public void finalizarSalida(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.finalizarSalida();
    }

    // Cancela una salida manualmente
    public void cancelarSalida(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.cancelarSalida();
    }

    // Consulta el estado actual de una salida
    public EstadoSalida consultarEstado(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        return salida.getEstado();
    }

    // Comprueba si corresponde cancelar por cupo minimo
    // El control se realiza exactamente 7 dias antes del inicio
    public boolean debeCancelarPorCupoMinimo(
            int codigoSalida, LocalDate fechaControl) {

        if (fechaControl == null) {
            throw new IllegalArgumentException(
                    "La fecha de control es obligatoria");
        }

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        EstadoSalida estado = salida.getEstado();

        if (estado == EstadoSalida.CANCELADA ||
                estado == EstadoSalida.EN_CURSO ||
                estado == EstadoSalida.FINALIZADA) {
            return false;
        }

        long diasRestantes = ChronoUnit.DAYS.between(
                fechaControl, salida.getFechaInicio());

        return diasRestantes == 7 &&
                salida.calcularOcupacion() < salida.getCupoMinimo();
    }

    // Cancela una salida por cupo minimo y calcula las devoluciones
    // Devuelve un mapa: numero de reserva -> importe a devolver
    // No registra ni ejecuta el reintegro
    public Map<Integer, Double> cancelarPorCupoMinimo(
            int codigoSalida, LocalDate fechaControl) {

        if (!debeCancelarPorCupoMinimo(
                codigoSalida, fechaControl)) {
            throw new IllegalStateException(
                    "La salida no cumple las condiciones de cancelacion");
        }

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        Map<Integer, Double> devoluciones = new LinkedHashMap<>();

        // Calcula primero los importes de todas las reservas activas
        for (Reserva reserva : salida.getReservas()) {

            if (reserva.ocupaCupo()) {

                double importe = politicaCancelacion.calcularDevolucion(
                        reserva, fechaControl);

                devoluciones.put(reserva.getNumero(), importe);
            }
        }

        // Cancela la salida
        salida.cancelarSalida();

        // Anula las reservas activas asociadas a la salida
        for (Reserva reserva : salida.getReservas()) {

            if (reserva.ocupaCupo()) {
                reserva.anular(
                        "Cancelacion de la agencia por cupo minimo");
                registrarCancelacionAgencia(reserva, fechaControl,
                        devoluciones.get(reserva.getNumero()));
            }
        }

        return Collections.unmodifiableMap(devoluciones);
    }

    // Revisa todas las salidas y cancela las que no alcanzaron
    // el cupo minimo exactamente 7 dias antes del inicio
    // Devuelve: codigo de salida -> devoluciones por reserva
    public Map<Integer, Map<Integer, Double>>
    controlarCupoMinimo(LocalDate fechaControl) {

        if (fechaControl == null) {
            throw new IllegalArgumentException(
                    "La fecha de control es obligatoria");
        }

        Map<Integer, Map<Integer, Double>> resultado =
                new LinkedHashMap<>();

        for (Salida salida : salidas) {

            if (debeCancelarPorCupoMinimo(
                    salida.getCodigo(), fechaControl)) {

                Map<Integer, Double> devoluciones =
                        cancelarPorCupoMinimo(
                                salida.getCodigo(), fechaControl);

                resultado.put(salida.getCodigo(), devoluciones);
            }
        }

        return Collections.unmodifiableMap(resultado);
    }
}
