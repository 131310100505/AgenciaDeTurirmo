package ar.edu.uade.rumbosur.persistencia;

import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Liquidacion;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vehiculo;
import ar.edu.uade.rumbosur.modelo.Vendedor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda los datos del sistema en memoria y en un archivo.
 *
 * GRASP Fabricación pura: no representa nada del negocio de la agencia. Existe
 * para que las clases del modelo no tengan que encargarse de guardar sus datos.
 *
 * Los Map permiten buscar rápido por código. La clave se declara Integer (la clase
 * envoltorio) porque las colecciones no admiten tipos primitivos como int.
 *
 * Se serializa el repositorio entero en un solo archivo: así Java mantiene las
 * referencias y, al cargar, la salida de una reserva sigue siendo el mismo objeto
 * que la salida guardada en el Map de salidas.
 */
public class Repositorio implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private static final String RUTA_POR_DEFECTO = "rumbosur.dat";

    private String rutaArchivo;

    private Map<Integer, Reserva> reservas;
    private Map<Integer, Salida> salidas;
    private Map<Integer, Cliente> clientes;
    private Map<Integer, Pasajero> pasajeros;
    private Map<Integer, Tour> tours;
    private Map<Integer, Guia> guias;
    private Map<Integer, Vendedor> vendedores;
    private Map<Integer, Cancelacion> cancelaciones;
    private Map<Integer, Liquidacion> liquidaciones;

    // La clave es String porque un vehículo se identifica por su patente.
    private Map<String, Vehiculo> vehiculos;

    public Repositorio() {
        this(RUTA_POR_DEFECTO);
    }

    public Repositorio(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.reservas = new HashMap<>();
        this.salidas = new HashMap<>();
        this.clientes = new HashMap<>();
        this.pasajeros = new HashMap<>();
        this.tours = new HashMap<>();
        this.guias = new HashMap<>();
        this.vendedores = new HashMap<>();
        this.cancelaciones = new HashMap<>();
        this.liquidaciones = new HashMap<>();
        this.vehiculos = new HashMap<>();
    }

    // ---------- Archivo ----------

    /** Guarda todos los datos en el archivo. try-with-resources lo cierra aunque falle. */
    public void guardarDatos() {
        try (FileOutputStream archivo = new FileOutputStream(rutaArchivo);
             ObjectOutputStream archivoSalida = new ObjectOutputStream(archivo)) {
            archivoSalida.writeObject(this);
        } catch (IOException e) {
            System.out.println("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    /**
     * Carga los datos del archivo. Devuelve false si no había datos para cargar:
     * la primera vez que se usa el programa o si el archivo no se pudo leer.
     */
    public boolean cargarDatos() {
        if (!new File(rutaArchivo).exists()) {
            return false;
        }
        // Se declaran los dos: si el archivo está dañado, falla el ObjectInputStream
        // y el FileInputStream igual se cierra.
        try (FileInputStream archivo = new FileInputStream(rutaArchivo);
             ObjectInputStream archivoEntrada = new ObjectInputStream(archivo)) {
            copiarDatosDe((Repositorio) archivoEntrada.readObject());
            return true;
        } catch (IOException | ClassNotFoundException e) { // multi-catch: mismo tratamiento para las dos
            System.out.println("No se pudieron cargar los datos: " + e.getMessage());
            return false;
        }
    }

    private void copiarDatosDe(Repositorio guardado) {
        this.reservas = guardado.reservas;
        this.salidas = guardado.salidas;
        this.clientes = guardado.clientes;
        this.pasajeros = guardado.pasajeros;
        this.tours = guardado.tours;
        this.guias = guardado.guias;
        this.vendedores = guardado.vendedores;
        this.cancelaciones = guardado.cancelaciones;
        this.liquidaciones = guardado.liquidaciones;
        this.vehiculos = guardado.vehiculos;
    }

    // ---------- Reservas ----------

    public void agregarReserva(Reserva reserva) {
        reservas.put(reserva.getNumero(), reserva);
    }

    public Reserva buscarReserva(int numero) {
        return reservas.get(numero);
    }

    public List<Reserva> listarReservas() {
        return new ArrayList<>(reservas.values());
    }

    // ---------- Salidas ----------

    public void agregarSalida(Salida salida) {
        salidas.put(salida.getCodigo(), salida);
    }

    public Salida buscarSalida(int codigo) {
        return salidas.get(codigo);
    }

    public List<Salida> listarSalidas() {
        return new ArrayList<>(salidas.values());
    }

    // ---------- Clientes ----------

    public void agregarCliente(Cliente cliente) {
        clientes.put(cliente.getCodigo(), cliente);
    }

    public Cliente buscarCliente(int codigo) {
        return clientes.get(codigo);
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes.values());
    }

    // ---------- Pasajeros ----------

    public void agregarPasajero(Pasajero pasajero) {
        pasajeros.put(pasajero.getCodigo(), pasajero);
    }

    public Pasajero buscarPasajero(int codigo) {
        return pasajeros.get(codigo);
    }

    public List<Pasajero> listarPasajeros() {
        return new ArrayList<>(pasajeros.values());
    }

    // ---------- Tours ----------

    public void agregarTour(Tour tour) {
        tours.put(tour.getCodigo(), tour);
    }

    public Tour buscarTour(int codigo) {
        return tours.get(codigo);
    }

    public List<Tour> listarTours() {
        return new ArrayList<>(tours.values());
    }

    // ---------- Guías ----------

    public void agregarGuia(Guia guia) {
        guias.put(guia.getLegajo(), guia);
    }

    public Guia buscarGuia(int legajo) {
        return guias.get(legajo);
    }

    public List<Guia> listarGuias() {
        return new ArrayList<>(guias.values());
    }

    // ---------- Vendedores ----------

    public void agregarVendedor(Vendedor vendedor) {
        vendedores.put(vendedor.getLegajo(), vendedor);
    }

    public Vendedor buscarVendedor(int legajo) {
        return vendedores.get(legajo);
    }

    public List<Vendedor> listarVendedores() {
        return new ArrayList<>(vendedores.values());
    }

    // ---------- Vehículos ----------

    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.put(vehiculo.getPatente(), vehiculo);
    }

    public Vehiculo buscarVehiculo(String patente) {
        return vehiculos.get(patente);
    }

    public List<Vehiculo> listarVehiculos() {
        return new ArrayList<>(vehiculos.values());
    }

    // ---------- Cancelaciones ----------

    public void agregarCancelacion(Cancelacion cancelacion) {
        cancelaciones.put(cancelacion.getCodigo(), cancelacion);
    }

    public Cancelacion buscarCancelacion(int codigo) {
        return cancelaciones.get(codigo);
    }

    public List<Cancelacion> listarCancelaciones() {
        return new ArrayList<>(cancelaciones.values());
    }

    // ---------- Liquidaciones ----------

    public void agregarLiquidacion(Liquidacion liquidacion) {
        liquidaciones.put(liquidacion.getCodigo(), liquidacion);
    }

    public Liquidacion buscarLiquidacion(int codigo) {
        return liquidaciones.get(codigo);
    }

    public List<Liquidacion> listarLiquidaciones() {
        return new ArrayList<>(liquidaciones.values());
    }
}
