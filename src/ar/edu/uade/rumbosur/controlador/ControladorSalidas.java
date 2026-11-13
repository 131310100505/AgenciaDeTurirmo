package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.excepciones.TransicionEstadoInvalidaException;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.EstadoSalida;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Liquidacion;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vehiculo;
import ar.edu.uade.rumbosur.persistencia.Repositorio;
import ar.edu.uade.rumbosur.servicio.PoliticaCancelacionAgencia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

    // Guias (por legajo) y vehiculos (por patente) disponibles para asignar (CU14)
    private final Map<Integer, Guia> guias = new LinkedHashMap<>();
    private final Map<String, Vehiculo> vehiculos = new LinkedHashMap<>();

    // Cantidad de dias antes de la salida en que se controla el cupo minimo
    private static final int DIAS_CONTROL_CUPO_MINIMO = 7;

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
        guias.clear();
        for (Guia guia : repositorio.listarGuias()) {
            guias.put(guia.getLegajo(), guia);
        }
        vehiculos.clear();
        for (Vehiculo vehiculo : repositorio.listarVehiculos()) {
            vehiculos.put(vehiculo.getPatente(), vehiculo);
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
        for (Guia guia : guias.values()) {
            repositorio.agregarGuia(guia);
        }
        for (Vehiculo vehiculo : vehiculos.values()) {
            repositorio.agregarVehiculo(vehiculo);
        }
    }

    // ---------- CU14: guias y vehiculos ----------

    public void registrarGuia(Guia guia) {
        if (guia == null) {
            throw new IllegalArgumentException("El guia no puede ser nulo");
        }
        guias.put(guia.getLegajo(), guia);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehiculo no puede ser nulo");
        }
        vehiculos.put(vehiculo.getPatente(), vehiculo);
    }

    public Guia buscarGuia(int legajo) {
        return guias.get(legajo);
    }

    public Vehiculo buscarVehiculo(String patente) {
        if (patente == null) {
            return null;
        }
        // La patente se busca sin importar mayusculas o espacios
        for (Vehiculo vehiculo : vehiculos.values()) {
            if (vehiculo.getPatente().equalsIgnoreCase(patente.trim())) {
                return vehiculo;
            }
        }
        return null;
    }

    public List<Guia> listarGuias() {
        return new ArrayList<>(guias.values());
    }

    public List<Vehiculo> listarVehiculos() {
        return new ArrayList<>(vehiculos.values());
    }

    // Asigna un guia buscandolo por legajo
    public void asignarGuia(int codigoSalida, int legajoGuia) {
        Guia guia = buscarGuia(legajoGuia);
        if (guia == null) {
            throw new IllegalArgumentException("No existe un guia con ese legajo");
        }
        asignarGuia(codigoSalida, guia);
    }

    // Asigna un vehiculo buscandolo por patente
    public void asignarVehiculo(int codigoSalida, String patente) {
        Vehiculo vehiculo = buscarVehiculo(patente);
        if (vehiculo == null) {
            throw new IllegalArgumentException("No existe un vehiculo con esa patente");
        }
        asignarVehiculo(codigoSalida, vehiculo);
    }

    // ---------- CU03: programar salida ----------

    /**
     * Programa una salida de un tour: el codigo es el siguiente libre, la fecha de fin
     * sale de la duracion del tour y los cupos son los del tour.
     */
    public Salida programarSalida(Tour tour, LocalDate fechaInicio) {
        if (tour == null || fechaInicio == null) {
            throw new IllegalArgumentException("El tour y la fecha de inicio son obligatorios");
        }
        LocalDate fechaFin = fechaInicio.plusDays(tour.getDuracionDias() - 1);
        return crearSalida(siguienteCodigoSalida(), fechaInicio, fechaFin,
                tour.getCupoMinimo(), tour.getCupoMaximo(), tour);
    }

    private int siguienteCodigoSalida() {
        int mayor = 100;
        for (Salida salida : salidas) {
            mayor = Math.max(mayor, salida.getCodigo());
        }
        return mayor + 1;
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

    // Exporta la lista operativa de pasajeros a un archivo de texto
    public void exportarListaPasajeros(int codigoSalida, Path rutaArchivo) throws IOException {

        if (rutaArchivo == null) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        if (!salida.isListaPasajerosGenerada()) {
            throw new IllegalStateException("Primero debe generarse la lista de pasajeros");
        }

        // Se exporta la lista ya generada, sin modificar las reservas.
        try (BufferedWriter escritor = Files.newBufferedWriter(
                rutaArchivo, StandardCharsets.UTF_8)) {

            escritor.write("RUMBO SUR - LISTA DE PASAJEROS");
            escritor.newLine();
            escritor.write("Salida: " + salida.getCodigo());
            escritor.newLine();
            escritor.write("Fecha de inicio: " + salida.getFechaInicio());
            escritor.newLine();
            escritor.write("Fecha de fin: " + salida.getFechaFin());
            escritor.newLine();
            escritor.write("Cantidad de pasajeros: " + salida.getListaPasajeros().size());
            escritor.newLine();
            escritor.newLine();

            int numero = 1;
            for (Pasajero pasajero : salida.getListaPasajeros()) {
                escritor.write(numero + ". " + pasajero.getNombre());
                escritor.newLine();
                escritor.write("   Documento: " + pasajero.getDocumento());
                escritor.newLine();
                escritor.write("   Contacto de emergencia: " + pasajero.getContactoEmergencia());
                escritor.newLine();
                escritor.newLine();
                numero++;
            }
        }
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
    // Se cancela si faltan 7 dias o menos y no se llego al cupo minimo
    // (asi no depende de que el control se ejecute justo ese dia)
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

        return diasRestantes <= DIAS_CONTROL_CUPO_MINIMO &&
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
    // el cupo minimo cuando faltan 7 dias o menos para el inicio
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