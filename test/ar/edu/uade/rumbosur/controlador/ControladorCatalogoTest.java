package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.Actividad;
import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoActividad;
import ar.edu.uade.rumbosur.modelo.Tour;
import ar.edu.uade.rumbosur.modelo.TourAventura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** CU01 Administrar tours y CU02 Administrar itinerarios y tarifas. */
class ControladorCatalogoTest {

    private static final LocalDate DESDE = LocalDate.of(2027, 1, 1);
    private static final LocalDate HASTA = LocalDate.of(2027, 3, 31);

    private ControladorCatalogo controlador;
    private Tour tour;

    @BeforeEach
    void prepararDatos() {
        controlador = new ControladorCatalogo();
        tour = controlador.registrarTour(CategoriaTour.CULTURAL, "Bodegas de Mendoza", "Visita a bodegas",
                3, 2, 20, 0, null);
    }

    @Test
    void registrarTourDeAventuraGuardaEdadYCodigoNuevo() {
        Tour aventura = controlador.registrarTour(CategoriaTour.AVENTURA, "Rafting", "Río Mendoza",
                1, 2, 10, 14, "Alta");

        assertTrue(aventura instanceof TourAventura);
        assertEquals(CategoriaTour.AVENTURA, aventura.obtenerCategoria());
        assertEquals(tour.getCodigo() + 1, aventura.getCodigo());
        assertEquals(2, controlador.listarTours().size());
    }

    @Test
    void rechazaTourConCuposInvalidosOAventuraSinDificultad() {
        assertThrows(IllegalArgumentException.class, () -> controlador.registrarTour(
                CategoriaTour.NATURALEZA, "Esteros", "", 2, 10, 5, 0, null));
        assertThrows(IllegalArgumentException.class, () -> controlador.registrarTour(
                CategoriaTour.AVENTURA, "Escalada", "", 1, 2, 8, 16, " "));
        assertEquals(1, controlador.listarTours().size());
    }

    @Test
    void itinerarioQuedaOrdenadoPorDiaYOrden() {
        controlador.agregarActividad(tour.getCodigo(), 2, 1, LocalTime.of(9, 0), "Bodega B", "Maipú",
                TipoActividad.EXCURSION);
        controlador.agregarActividad(tour.getCodigo(), 1, 2, LocalTime.of(13, 0), "Almuerzo", "Luján",
                TipoActividad.GASTRONOMIA);
        controlador.agregarActividad(tour.getCodigo(), 1, 1, LocalTime.of(9, 0), "Bodega A", "Luján",
                TipoActividad.EXCURSION);

        List<Actividad> itinerario = controlador.listarItinerario(tour.getCodigo());

        assertEquals("Bodega A", itinerario.get(0).getDescripcion());
        assertEquals("Almuerzo", itinerario.get(1).getDescripcion());
        assertEquals("Bodega B", itinerario.get(2).getDescripcion());
    }

    @Test
    void rechazaActividadFueraDeLaDuracionORepetida() {
        controlador.agregarActividad(tour.getCodigo(), 1, 1, LocalTime.of(9, 0), "Bodega A", "Luján",
                TipoActividad.EXCURSION);

        // El tour dura 3 días
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarActividad(tour.getCodigo(),
                4, 1, LocalTime.of(9, 0), "Fuera de fecha", "", TipoActividad.OTRO));
        // Mismo día y orden que otra actividad
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarActividad(tour.getCodigo(),
                1, 1, LocalTime.of(10, 0), "Repetida", "", TipoActividad.OTRO));
        assertEquals(1, tour.getActividades().size());
    }

    @Test
    void laTarifaCargadaQuedaVigenteEnSuPeriodo() {
        Tarifa tarifa = controlador.agregarTarifa(tour.getCodigo(), Temporada.ALTA, DESDE, HASTA, 180000);

        assertSame(tarifa, tour.obtenerTarifaVigente(LocalDate.of(2027, 2, 10)));
        assertNull(tour.obtenerTarifaVigente(LocalDate.of(2027, 4, 1)));
    }

    @Test
    void rechazaTarifasSuperpuestasOConDatosInvalidos() {
        controlador.agregarTarifa(tour.getCodigo(), Temporada.ALTA, DESDE, HASTA, 180000);

        assertThrows(IllegalArgumentException.class, () -> controlador.agregarTarifa(tour.getCodigo(),
                Temporada.MEDIA, LocalDate.of(2027, 3, 31), LocalDate.of(2027, 6, 30), 150000));
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarTarifa(tour.getCodigo(),
                Temporada.BAJA, LocalDate.of(2027, 8, 1), LocalDate.of(2027, 7, 1), 100000));
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarTarifa(tour.getCodigo(),
                Temporada.BAJA, LocalDate.of(2027, 7, 1), LocalDate.of(2027, 8, 1), 0));

        // La tarifa siguiente, sin superposición, sí se acepta
        controlador.agregarTarifa(tour.getCodigo(), Temporada.MEDIA, LocalDate.of(2027, 4, 1),
                LocalDate.of(2027, 6, 30), 150000);
        assertEquals(2, tour.getTarifas().size());
    }

    @Test
    void modificarTourCambiaSusDatosPeroNoElCodigo() {
        controlador.modificarTour(tour.getCodigo(), "Bodegas y olivares", "Nueva visita", 4, 3, 25);

        assertEquals("Bodegas y olivares", tour.getNombre());
        assertEquals(4, tour.getDuracionDias());
        assertEquals(25, tour.getCupoMaximo());
        assertSame(tour, controlador.buscarTour(tour.getCodigo()));
    }

    @Test
    void modificarConDatosInvalidosNoCambiaNada() {
        controlador.agregarActividad(tour.getCodigo(), 3, 1, LocalTime.of(9, 0), "Bodega",
                "Luján de Cuyo", TipoActividad.EXCURSION);

        // Tiene una actividad el día 3: no se puede acortar a 2 días.
        assertThrows(IllegalArgumentException.class,
                () -> controlador.modificarTour(tour.getCodigo(), "Bodegas", "", 2, 2, 20));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.modificarTour(tour.getCodigo(), " ", "", 3, 2, 20));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.modificarTour(tour.getCodigo(), "Bodegas", "", 3, 10, 5));
        assertEquals("Bodegas de Mendoza", tour.getNombre());
        assertEquals(3, tour.getDuracionDias());
    }

    @Test
    void rechazaOperacionesSobreUnTourInexistente() {
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarTarifa(99, Temporada.ALTA,
                DESDE, HASTA, 1000));
        assertThrows(IllegalArgumentException.class, () -> controlador.listarItinerario(99));
    }
}
