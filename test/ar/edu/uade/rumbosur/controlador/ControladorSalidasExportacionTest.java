
package ar.edu.uade.rumbosur.controlador;

import ar.edu.uade.rumbosur.modelo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ControladorSalidasExportacionTest {

    @TempDir
    Path carpetaTemporal;

    // Crea un tour de naturaleza para las pruebas
    private Tour crearTour() {
        return new Tour(
                1, "Bariloche", "Tour de prueba",
                5, 5, 20
        ) {
            @Override
            public CategoriaTour obtenerCategoria() {
                return CategoriaTour.NATURALEZA;
            }

            @Override
            public boolean validarPasajero(
                    Pasajero pasajero, LocalDate fechaSalida) {
                return true;
            }
        };
    }

    // Crea una salida con un minimo de 5 pasajeros
    private Salida crearSalida(ControladorSalidas controlador) {
        return controlador.crearSalida(
                100,
                LocalDate.of(2026, 12, 10),
                LocalDate.of(2026, 12, 12),
                5,
                20,
                crearTour()
        );
    }

    // Crea un pasajero con documento unico
    private Pasajero crearPasajero(int numero) {
        return new Pasajero(
                numero,
                "Pasajero " + numero,
                "DNI" + numero,
                LocalDate.of(2000, 1, 1),
                TipoPasajero.ADULTO,
                "1123456789",
                "Sin restricciones"
        );
    }

    // Prepara una salida con 5 pasajeros confirmados
    private Salida prepararSalidaConfirmada(
            ControladorSalidas controlador) {

        Salida salida = crearSalida(controlador);

        Cliente cliente = new Cliente(
                1, "Juan", "Perez",
                "30111222", "1123456789",
                "juan@test.com", "Argentina"
        );

        Vendedor vendedor = new Vendedor(
                10, "Carlos", "25111222",
                "1198765432", 0.05
        );

        Reserva reserva = new Reserva(
                cliente, salida, vendedor
        );

        for (int i = 1; i <= 5; i++) {
            reserva.agregarPasajero(crearPasajero(i));
        }

        reserva.setTotal(100000.0);

        Pago pago = new Pago(
                1,
                LocalDate.of(2026, 12, 1),
                30000.0,
                MedioPago.TARJETA,
                1
        );

        reserva.registrarPago(pago);

        assertTrue(reserva.confirmar());

        salida.agregarReserva(reserva);

        assertEquals(5, salida.calcularOcupacion());

        controlador.generarListaPasajeros(100);

        assertTrue(salida.isListaPasajerosGenerada());

        return salida;
    }

    // TEST 1: Exporta correctamente un archivo TXT
    @Test
    void exportaArchivoCorrectamente() throws IOException {

        ControladorSalidas controlador =
                new ControladorSalidas();

        prepararSalidaConfirmada(controlador);

        Path archivo = carpetaTemporal.resolve("pasajeros.txt");

        controlador.exportarListaPasajeros(100, archivo);

        assertTrue(Files.exists(archivo));
        assertTrue(Files.size(archivo) > 0);
    }

    // TEST 2: Exporta los nombres y documentos
    @Test
    void exportaDatosDePasajeros() throws IOException {

        ControladorSalidas controlador =
                new ControladorSalidas();

        prepararSalidaConfirmada(controlador);

        Path archivo = carpetaTemporal.resolve("datos.txt");

        controlador.exportarListaPasajeros(100, archivo);

        String contenido = Files.readString(
                archivo, StandardCharsets.UTF_8
        );

        for (int i = 1; i <= 5; i++) {
            assertTrue(contenido.contains("Pasajero " + i));
            assertTrue(contenido.contains("DNI" + i));
        }
    }

    // TEST 3: Exporta los contactos de emergencia
    @Test
    void exportaContactosDeEmergencia() throws IOException {

        ControladorSalidas controlador =
                new ControladorSalidas();

        prepararSalidaConfirmada(controlador);

        Path archivo = carpetaTemporal.resolve("emergencias.txt");

        controlador.exportarListaPasajeros(100, archivo);

        String contenido = Files.readString(
                archivo, StandardCharsets.UTF_8
        );

        assertTrue(contenido.contains(
                "Contacto de emergencia: 1123456789"
        ));

        assertEquals(
                5,
                contenido.split(
                        "Contacto de emergencia:", -1
                ).length - 1
        );
    }

    // TEST 4: Exporta los datos generales de la salida
    @Test
    void exportaEncabezadoYFechas() throws IOException {

        ControladorSalidas controlador =
                new ControladorSalidas();

        prepararSalidaConfirmada(controlador);

        Path archivo = carpetaTemporal.resolve("salida.txt");

        controlador.exportarListaPasajeros(100, archivo);

        String contenido = Files.readString(
                archivo, StandardCharsets.UTF_8
        );

        assertTrue(contenido.contains(
                "RUMBO SUR - LISTA DE PASAJEROS"
        ));

        assertTrue(contenido.contains("Salida: 100"));
        assertTrue(contenido.contains(
                "Fecha de inicio: 2026-12-10"
        ));
        assertTrue(contenido.contains(
                "Fecha de fin: 2026-12-12"
        ));
        assertTrue(contenido.contains(
                "Cantidad de pasajeros: 5"
        ));
    }

    // TEST 5: No exporta una lista que no fue generada
    @Test
    void rechazaListaNoGenerada() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        crearSalida(controlador);

        Path archivo = carpetaTemporal.resolve("sin_lista.txt");

        assertThrows(
                IllegalStateException.class,
                () -> controlador.exportarListaPasajeros(
                        100, archivo
                )
        );

        assertFalse(Files.exists(archivo));
    }

    // TEST 6: No permite exportar una salida inexistente
    @Test
    void rechazaSalidaInexistente() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        Path archivo = carpetaTemporal.resolve("inexistente.txt");

        assertThrows(
                IllegalArgumentException.class,
                () -> controlador.exportarListaPasajeros(
                        999, archivo
                )
        );

        assertFalse(Files.exists(archivo));
    }

    // TEST 7: No permite una ruta nula
    @Test
    void rechazaRutaNula() {

        ControladorSalidas controlador =
                new ControladorSalidas();

        prepararSalidaConfirmada(controlador);

        assertThrows(
                IllegalArgumentException.class,
                () -> controlador.exportarListaPasajeros(
                        100, null
                )
        );
    }
}
