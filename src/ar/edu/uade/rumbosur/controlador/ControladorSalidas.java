
package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.EstadoSalida;
import ar.edu.uade.rumbosur.modelo.Guia;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.Vehiculo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControladorSalidas {

    // Lista de salidas administradas por el controlador
    private final List<Salida> salidas;

    // Constructor
    public ControladorSalidas() {
        this.salidas = new ArrayList<>();
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

    // Cancela una salida
    public void cancelarSalida(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        salida.cancelarSalida();
    }

    // Consulta el estado actual de una salida
    public EstadoSalida consultarEstado(int codigoSalida) {

        Salida salida = obtenerSalidaObligatoria(codigoSalida);

        return salida.getEstado();
    }
}
