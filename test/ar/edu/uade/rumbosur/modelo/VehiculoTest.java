
package ar.edu.uade.rumbosur.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehiculoTest {

    // TEST 1: Acepta una cantidad menor a su capacidad
    @Test
    public void aceptaPasajerosDentroDeCapacidad() {

        Vehiculo vehiculo = new Vehiculo(
                "AA123BB", 20, TipoVehiculo.MINIBUS
        );

        assertTrue(vehiculo.tieneCapacidad(15));
    }

    // TEST 2: Acepta exactamente su capacidad maxima
    @Test
    public void aceptaCapacidadExacta() {

        Vehiculo vehiculo = new Vehiculo(
                "AA123BB", 20, TipoVehiculo.MINIBUS
        );

        assertTrue(vehiculo.tieneCapacidad(20));
    }

    // TEST 3: Rechaza pasajeros que superan la capacidad
    @Test
    public void rechazaExcesoDePasajeros() {

        Vehiculo vehiculo = new Vehiculo(
                "AA123BB", 20, TipoVehiculo.MINIBUS
        );

        assertFalse(vehiculo.tieneCapacidad(21));
    }

    // TEST 4: Rechaza cantidades de pasajeros invalidas
    @Test
    public void rechazaCantidadCeroONegativa() {

        Vehiculo vehiculo = new Vehiculo(
                "AA123BB", 20, TipoVehiculo.MINIBUS
        );

        assertFalse(vehiculo.tieneCapacidad(0));
        assertFalse(vehiculo.tieneCapacidad(-1));
    }

    // TEST 5: No permite crear un vehiculo sin patente
    @Test
    public void rechazaPatenteVacia() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Vehiculo(
                        " ", 20, TipoVehiculo.MINIBUS
                )
        );
    }

    // TEST 6: No permite crear un vehiculo sin capacidad
    @Test
    public void rechazaCapacidadInvalida() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Vehiculo(
                        "AA123BB", 0, TipoVehiculo.MINIBUS
                )
        );
    }

    // TEST 7: No permite crear un vehiculo sin tipo
    @Test
    public void rechazaTipoNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Vehiculo(
                        "AA123BB", 20, null
                )
        );
    }

    // TEST 8: Verifica los datos del vehiculo
    @Test
    public void verificaDatosDelVehiculo() {

        Vehiculo vehiculo = new Vehiculo(
                "AA123BB", 20, TipoVehiculo.MINIBUS
        );

        assertEquals("AA123BB", vehiculo.getPatente());
        assertEquals(20, vehiculo.getCapacidad());
        assertEquals(TipoVehiculo.MINIBUS, vehiculo.getTipo());
    }
}
