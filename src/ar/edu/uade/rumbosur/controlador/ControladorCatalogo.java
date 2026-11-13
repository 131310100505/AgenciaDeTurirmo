package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.Actividad;
import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoActividad;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.TourAventura;
import ar.edu.uade.rumbosur.modelo.TourCultural;
import ar.edu.uade.rumbosur.modelo.TourNaturaleza;
import ar.edu.uade.rumbosur.persistencia.Repositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Controlador del catálogo de tours (GRASP Controlador).
 * CU01 Administrar tours y CU02 Administrar itinerarios y tarifas.
 *
 * Las reglas que dependen de un solo tour quedan en Tour; acá se validan las que
 * miran el conjunto (código repetido, actividades del mismo día y orden, tarifas
 * que se superponen).
 */
public class ControladorCatalogo {

    // TreeMap: los tours quedan ordenados por código
    private final Map<Integer, Tour> tours = new TreeMap<>();

    // Itinerario ordenado por día y, dentro del día, por orden (Comparator)
    private static final Comparator<Actividad> POR_DIA_Y_ORDEN =
            Comparator.comparingInt(Actividad::getDia).thenComparingInt(Actividad::getOrden);

    // ---------- Persistencia ----------

    public void cargarDesde(Repositorio repositorio) {
        tours.clear();
        for (Tour tour : repositorio.listarTours()) {
            tours.put(tour.getCodigo(), tour);
        }
    }

    public void guardarEn(Repositorio repositorio) {
        for (Tour tour : tours.values()) {
            repositorio.agregarTour(tour);
        }
    }

    // ---------- CU01 Administrar tours ----------

    /**
     * Da de alta un tour de la categoría indicada. La edad mínima y la dificultad
     * solo se usan en los tours de aventura.
     */
    public Tour registrarTour(CategoriaTour categoria, String nombre, String descripcion,
                              int duracionDias, int cupoMinimo, int cupoMaximo,
                              int edadMinima, String dificultad) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría del tour es obligatoria.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tour es obligatorio.");
        }
        if (duracionDias < 1) {
            throw new IllegalArgumentException("El tour debe durar al menos un día.");
        }
        if (cupoMinimo < 1 || cupoMaximo < cupoMinimo) {
            throw new IllegalArgumentException("El cupo mínimo debe ser al menos 1 y no superar al máximo.");
        }

        int codigo = siguienteCodigoTour();
        Tour tour;
        switch (categoria) {
            case AVENTURA:
                if (edadMinima < 0) {
                    throw new IllegalArgumentException("La edad mínima no puede ser negativa.");
                }
                if (dificultad == null || dificultad.isBlank()) {
                    throw new IllegalArgumentException("El tour de aventura debe indicar la dificultad.");
                }
                tour = new TourAventura(codigo, nombre, descripcion, duracionDias,
                        cupoMinimo, cupoMaximo, edadMinima, dificultad);
                break;
            case CULTURAL:
                tour = new TourCultural(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
                break;
            default:
                tour = new TourNaturaleza(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
                break;
        }
        tours.put(codigo, tour);
        return tour;
    }

    /**
     * Modifica nombre, descripción, duración y cupos de un tour. La duración no puede
     * quedar más corta que el último día que ya tiene actividades en el itinerario.
     */
    public void modificarTour(int codigo, String nombre, String descripcion,
                              int duracionDias, int cupoMinimo, int cupoMaximo) {
        Tour tour = obtenerTour(codigo);
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tour es obligatorio.");
        }
        if (cupoMinimo < 1 || cupoMaximo < cupoMinimo) {
            throw new IllegalArgumentException("El cupo mínimo debe ser al menos 1 y no superar al máximo.");
        }
        for (Actividad actividad : tour.getActividades()) {
            if (actividad.getDia() > duracionDias) {
                throw new IllegalArgumentException("El itinerario tiene actividades en el día "
                        + actividad.getDia() + ": la duración no puede ser menor.");
            }
        }
        if (duracionDias < 1) {
            throw new IllegalArgumentException("El tour debe durar al menos un día.");
        }
        tour.modificarDatos(nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    public Tour buscarTour(int codigo) {
        return tours.get(codigo);
    }

    public List<Tour> listarTours() {
        return new ArrayList<>(tours.values());
    }

    // ---------- CU02 Administrar itinerarios y tarifas ----------

    /** Agrega una actividad al itinerario del tour. */
    public Actividad agregarActividad(int codigoTour, int dia, int orden, LocalTime horario,
                                      String descripcion, String lugar, TipoActividad tipo) {
        Tour tour = obtenerTour(codigoTour);
        if (dia < 1 || dia > tour.getDuracionDias()) {
            throw new IllegalArgumentException("El día debe estar entre 1 y " + tour.getDuracionDias() + ".");
        }
        if (orden < 1) {
            throw new IllegalArgumentException("El orden debe ser 1 o mayor.");
        }
        if (horario == null || tipo == null || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Faltan datos de la actividad: horario, descripción y tipo.");
        }
        for (Actividad existente : tour.getActividades()) {
            if (existente.getDia() == dia && existente.getOrden() == orden) {
                throw new IllegalArgumentException("Ya hay una actividad en el día " + dia
                        + " con el orden " + orden + ".");
            }
        }
        int codigo = 1;
        for (Actividad existente : tour.getActividades()) {
            codigo = Math.max(codigo, existente.getCodigo() + 1);
        }
        Actividad actividad = new Actividad(codigo, dia, orden, horario, descripcion, lugar, tipo);
        tour.agregarActividad(actividad);
        return actividad;
    }

    /** Devuelve el itinerario del tour ordenado por día y orden. */
    public List<Actividad> listarItinerario(int codigoTour) {
        List<Actividad> itinerario = new ArrayList<>(obtenerTour(codigoTour).getActividades());
        itinerario.sort(POR_DIA_Y_ORDEN);
        return itinerario;
    }

    /**
     * Agrega una tarifa por temporada. No puede superponerse con otra tarifa del
     * mismo tour: para cada fecha tiene que haber una sola tarifa vigente.
     */
    public Tarifa agregarTarifa(int codigoTour, Temporada temporada, LocalDate desde,
                                LocalDate hasta, double precioBase) {
        Tour tour = obtenerTour(codigoTour);
        if (temporada == null || desde == null || hasta == null) {
            throw new IllegalArgumentException("Faltan datos de la tarifa: temporada y fechas de vigencia.");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha final de vigencia no puede ser anterior a la inicial.");
        }
        if (precioBase <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor que cero.");
        }
        int codigo = 1;
        for (Tarifa existente : tour.getTarifas()) {
            boolean seSuperponen = !desde.isAfter(existente.getFechaFinVigencia())
                    && !hasta.isBefore(existente.getFechaInicioVigencia());
            if (seSuperponen) {
                throw new IllegalArgumentException("Se superpone con la tarifa vigente del "
                        + existente.getFechaInicioVigencia() + " al " + existente.getFechaFinVigencia() + ".");
            }
            codigo = Math.max(codigo, existente.getCodigo() + 1);
        }
        Tarifa tarifa = new Tarifa(codigo, temporada, desde, hasta, precioBase);
        tour.agregarTarifa(tarifa);
        return tarifa;
    }

    private Tour obtenerTour(int codigoTour) {
        Tour tour = tours.get(codigoTour);
        if (tour == null) {
            throw new IllegalArgumentException("No existe un tour con código " + codigoTour + ".");
        }
        return tour;
    }

    private int siguienteCodigoTour() {
        int mayor = 0;
        for (int codigo : tours.keySet()) {
            mayor = Math.max(mayor, codigo);
        }
        return mayor + 1;
    }
}
