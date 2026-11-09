
package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TourTest {

    // Crea un pasajero para las pruebas
    private Pasajero crearPasajero(LocalDate nacimiento) {
        return new Pasajero(
                1,
                "Pasajero de prueba",
                "30111222",
                nacimiento,
                TipoPasajero.ADULTO,
                "1123456789",
                "Sin restricciones"
        );
    }

    // Crea un tour de aventura con edad minima de 18 años
    private TourAventura crearTourAventura() {
        return new TourAventura(
                1,
                "Aventura en Bariloche",
                "Excursion de aventura",
                3,
                5,
                20,
                18,
                "Media"
        );
    }

    // Crea un tour cultural
    private TourCultural crearTourCultural() {
        return new TourCultural(
                2,
                "Circuito Cultural",
                "Visita a museos",
                2,
                5,
                20
        );
    }

    // Crea un tour de naturaleza
    private TourNaturaleza crearTourNaturaleza() {
        return new TourNaturaleza(
                3,
                "Naturaleza Patagonica",
                "Excursion por parques",
                2,
                5,
                20
        );
    }

    // TEST 1: Aventura acepta pasajeros mayores de edad
    @Test
    public void aventuraAceptaMayorDeEdad() {

        TourAventura tour = crearTourAventura();

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2000, 1, 1)
        );

        assertTrue(
                tour.validarPasajero(
                        pasajero,
                        LocalDate.of(2026, 12, 10)
                )
        );
    }

    // TEST 2: Aventura rechaza menores de edad
    @Test
    public void aventuraRechazaMenorDeEdad() {

        TourAventura tour = crearTourAventura();

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2012, 1, 1)
        );

        assertFalse(
                tour.validarPasajero(
                        pasajero,
                        LocalDate.of(2026, 12, 10)
                )
        );
    }

    // TEST 3: Aventura acepta quien cumple 18 el dia de salida
    @Test
    public void aventuraAceptaCumpleaniosElDiaDeSalida() {

        TourAventura tour = crearTourAventura();

        LocalDate fechaSalida =
                LocalDate.of(2026, 12, 10);

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2008, 12, 10)
        );

        assertTrue(
                tour.validarPasajero(
                        pasajero,
                        fechaSalida
                )
        );
    }

    // TEST 4: Aventura rechaza pasajeros nulos
    @Test
    public void aventuraRechazaPasajeroNulo() {

        TourAventura tour = crearTourAventura();

        assertFalse(
                tour.validarPasajero(
                        null,
                        LocalDate.of(2026, 12, 10)
                )
        );
    }

    // TEST 5: Cultural acepta pasajeros menores de edad
    @Test
    public void culturalAceptaMenorDeEdad() {

        TourCultural tour = crearTourCultural();

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2015, 5, 20)
        );

        assertTrue(
                tour.validarPasajero(
                        pasajero,
                        LocalDate.of(2026, 12, 10)
                )
        );
    }

    // TEST 6: Naturaleza acepta pasajeros menores de edad
    @Test
    public void naturalezaAceptaMenorDeEdad() {

        TourNaturaleza tour = crearTourNaturaleza();

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2015, 5, 20)
        );

        assertTrue(
                tour.validarPasajero(
                        pasajero,
                        LocalDate.of(2026, 12, 10)
                )
        );
    }

    // TEST 7: Cultural rechaza fecha de salida nula
    @Test
    public void culturalRechazaFechaNula() {

        TourCultural tour = crearTourCultural();

        Pasajero pasajero = crearPasajero(
                LocalDate.of(2000, 1, 1)
        );

        assertFalse(
                tour.validarPasajero(
                        pasajero,
                        null
                )
        );
    }

    // TEST 8: Obtiene la tarifa vigente para una fecha
    @Test
    public void obtieneTarifaVigente() {

        TourCultural tour = crearTourCultural();

        Tarifa tarifa = new Tarifa(
                1,
                null,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31),
                150000.0
        );

        tour.agregarTarifa(tarifa);

        assertSame(
                tarifa,
                tour.obtenerTarifaVigente(
                        LocalDate.of(2026, 12, 15)
                )
        );
    }

    // TEST 9: No encuentra tarifas fuera de vigencia
    @Test
    public void noEncuentraTarifaFueraDeVigencia() {

        TourCultural tour = crearTourCultural();

        Tarifa tarifa = new Tarifa(
                2,
                null,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31),
                150000.0
        );

        tour.agregarTarifa(tarifa);

        assertNull(
                tour.obtenerTarifaVigente(
                        LocalDate.of(2027, 1, 10)
                )
        );
    }

    // TEST 10: Rechaza tarifas con codigo duplicado
    @Test
    public void rechazaTarifasConCodigoDuplicado() {

        TourCultural tour = crearTourCultural();

        Tarifa tarifa1 = new Tarifa(
                1,
                null,
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31),
                150000.0
        );

        Tarifa tarifa2 = new Tarifa(
                1,
                null,
                LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 31),
                180000.0
        );

        tour.agregarTarifa(tarifa1);

        assertThrows(
                IllegalArgumentException.class,
                () -> tour.agregarTarifa(tarifa2)
        );
    }
}
