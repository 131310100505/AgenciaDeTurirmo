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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda los datos del sistema mientras el programa está en ejecución.
 *
 * GRASP Fabricación pura: no representa nada del negocio de la agencia. Existe
 * para que las clases del modelo no tengan que encargarse de guardar sus datos.
 *
 * Los Map permiten buscar rápido por código. La clave se declara Integer (la clase
 * envoltorio) porque las colecciones no admiten tipos primitivos como int.
 */
public class Repositorio {

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
