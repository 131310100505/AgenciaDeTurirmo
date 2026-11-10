
package ar.edu.uade.rumbosur;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.modelo.*;
import ar.edu.uade.rumbosur.persistencia.Repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DatosIniciales {

    public static void cargarEn(Repositorio repositorio) {

        // ---------- TOURS ----------

        if (repositorio.buscarTour(1) == null) {
            repositorio.agregarTour(new TourNaturaleza(
                    1, "Lagos de Bariloche",
                    "Recorrido por los lagos y paisajes de Bariloche",
                    3, 2, 20));
        }

        if (repositorio.buscarTour(2) == null) {
            repositorio.agregarTour(new TourAventura(
                    2, "Trekking en la Patagonia",
                    "Caminata guiada por senderos de montaña",
                    2, 2, 15, 16, "Media"));
        }

        if (repositorio.buscarTour(3) == null) {
            repositorio.agregarTour(new TourCultural(
                    3, "Historia de Bariloche",
                    "Visita a lugares históricos y culturales",
                    1, 2, 25));
        }

        // ---------- TARIFAS ----------

        // Se agregan tarifas únicamente si el tour todavía
        // no tiene una vigente para la fecha de su salida.
        // Los precios son valores de ejemplo para pruebas.

        cargarTarifaSiFalta(
                repositorio.buscarTour(1),
                LocalDate.now().plusDays(20),
                1001,
                150000
        );

        cargarTarifaSiFalta(
                repositorio.buscarTour(2),
                LocalDate.now().plusDays(25),
                1002,
                200000
        );

        cargarTarifaSiFalta(
                repositorio.buscarTour(3),
                LocalDate.now().plusDays(30),
                1003,
                80000
        );

        // ---------- SALIDAS ----------

        LocalDate fecha = LocalDate.now().plusDays(20);

        if (repositorio.buscarSalida(101) == null) {
            repositorio.agregarSalida(new Salida(
                    101, fecha, fecha.plusDays(2),
                    2, 20, repositorio.buscarTour(1)));
        }

        if (repositorio.buscarSalida(102) == null) {
            repositorio.agregarSalida(new Salida(
                    102, fecha.plusDays(5), fecha.plusDays(6),
                    2, 15, repositorio.buscarTour(2)));
        }

        if (repositorio.buscarSalida(103) == null) {
            repositorio.agregarSalida(new Salida(
                    103, fecha.plusDays(10), fecha.plusDays(10),
                    2, 25, repositorio.buscarTour(3)));
        }

        // Verificar tarifas según las fechas reales de las salidas.
        asegurarTarifaDeSalida(
                repositorio, 101, 1001, 150000
        );

        asegurarTarifaDeSalida(
                repositorio, 102, 1002, 200000
        );

        asegurarTarifaDeSalida(
                repositorio, 103, 1003, 80000
        );

        // ---------- GUÍAS ----------

        if (repositorio.buscarGuia(201) == null) {
            repositorio.agregarGuia(new Guia(
                    201, "Martín López", "30111222",
                    "2944000001",
                    List.of("Español", "Inglés"),
                    Set.of(CategoriaTour.NATURALEZA),
                    45000));
        }

        if (repositorio.buscarGuia(202) == null) {
            repositorio.agregarGuia(new Guia(
                    202, "Lucía Fernández", "30222333",
                    "2944000002",
                    List.of("Español", "Inglés"),
                    Set.of(CategoriaTour.AVENTURA),
                    50000));
        }

        if (repositorio.buscarGuia(203) == null) {
            repositorio.agregarGuia(new Guia(
                    203, "Carlos Gómez", "30333444",
                    "2944000003",
                    List.of("Español", "Portugués"),
                    Set.of(CategoriaTour.CULTURAL),
                    40000));
        }

        // ---------- VEHÍCULOS ----------

        if (repositorio.buscarVehiculo("AB123CD") == null) {
            repositorio.agregarVehiculo(new Vehiculo(
                    "AB123CD", 20, TipoVehiculo.MINIBUS));
        }

        if (repositorio.buscarVehiculo("AC456EF") == null) {
            repositorio.agregarVehiculo(new Vehiculo(
                    "AC456EF", 15, TipoVehiculo.COMBI));
        }

        if (repositorio.buscarVehiculo("AD789GH") == null) {
            repositorio.agregarVehiculo(new Vehiculo(
                    "AD789GH", 40, TipoVehiculo.MICRO));
        }

        // ---------- CLIENTES ----------

        if (repositorio.buscarCliente("40111222") == null) {
            repositorio.agregarCliente(new Cliente(
                    301, "Ana", "Pérez", "40111222",
                    "1123456789", "ana@example.com", "Argentina"));
        }

        if (repositorio.buscarCliente("40222333") == null) {
            repositorio.agregarCliente(new Cliente(
                    302, "Pedro", "García", "40222333",
                    "1134567890", "pedro@example.com", "Argentina"));
        }

        if (repositorio.buscarCliente("40333444") == null) {
            repositorio.agregarCliente(new Cliente(
                    303, "María", "Rodríguez", "40333444",
                    "1145678901", "maria@example.com", "Chile"));
        }

        // ---------- VENDEDORES ----------

        if (repositorio.buscarVendedor(401) == null) {
            repositorio.agregarVendedor(new Vendedor(
                    401, "Sofía Martínez", "35111222",
                    "1156789012", 0.05));
        }

        if (repositorio.buscarVendedor(402) == null) {
            repositorio.agregarVendedor(new Vendedor(
                    402, "Diego Ramírez", "35222333",
                    "1167890123", 0.05));
        }

        // ---------- ASIGNACIÓN DE RECURSOS ----------

        asignarRecursos(repositorio, 101, 201, "AB123CD");
        asignarRecursos(repositorio, 102, 202, "AC456EF");
        asignarRecursos(repositorio, 103, 203, "AD789GH");

        System.out.println(
                "Datos iniciales verificados correctamente."
        );
    }

    // ---------- TARIFAS AUXILIARES ----------

    private static void asegurarTarifaDeSalida(
            Repositorio repositorio,
            int codigoSalida,
            int codigoTarifa,
            double precioBase) {

        Salida salida = repositorio.buscarSalida(codigoSalida);

        if (salida == null) {
            return;
        }

        cargarTarifaSiFalta(
                salida.getTour(),
                salida.getFechaInicio(),
                codigoTarifa,
                precioBase
        );
    }

    private static void cargarTarifaSiFalta(
            Tour tour,
            LocalDate fecha,
            int codigoTarifa,
            double precioBase) {

        if (tour == null || fecha == null) {
            return;
        }

        // Si ya existe una tarifa vigente, se conserva.
        if (tour.obtenerTarifaVigente(fecha) != null) {
            return;
        }

        // Evita repetir el código de una tarifa existente.
        int codigoDisponible = codigoTarifa;

        boolean codigoOcupado;

        do {
            codigoOcupado = false;

            for (Tarifa tarifa : tour.getTarifas()) {
                if (tarifa.getCodigo() == codigoDisponible) {
                    codigoOcupado = true;
                    codigoDisponible++;
                    break;
                }
            }

        } while (codigoOcupado);

        Tarifa tarifa = new Tarifa(
                codigoDisponible,
                Temporada.MEDIA,
                fecha.minusDays(30),
                fecha.plusDays(365),
                precioBase
        );

        tour.agregarTarifa(tarifa);
    }

    // ---------- RESERVA DE PRUEBA ----------

    public static void cargarReservaPrueba(
            Repositorio repositorio,
            ControladorReservas controladorReservas) {

        Salida salida = repositorio.buscarSalida(101);

        if (salida == null) {
            System.out.println("No existe la salida 101.");
            return;
        }

        // No duplicar la reserva de prueba.
        for (Reserva reserva : controladorReservas.listarReservas()) {
            if (reserva.getSalida().getCodigo() == 101) {

                for (Pasajero pasajero : reserva.getPasajeros()) {
                    if ("45111222".equals(pasajero.getDocumento())) {
                        System.out.println(
                                "La reserva de prueba ya existe."
                        );
                        return;
                    }
                }
            }
        }

        Cliente cliente =
                repositorio.buscarCliente("40111222");

        Vendedor vendedor =
                repositorio.buscarVendedor(401);

        if (cliente == null || vendedor == null) {
            System.out.println(
                    "Falta el cliente o vendedor de prueba."
            );
            return;
        }

        Pasajero pasajero1 = new Pasajero(
                501,
                "Juan Pérez",
                "45111222",
                LocalDate.of(2000, 5, 15),
                TipoPasajero.ADULTO,
                "1123456789",
                "Ninguna"
        );

        Pasajero pasajero2 = new Pasajero(
                502,
                "Lucía Gómez",
                "45222333",
                LocalDate.of(2001, 8, 20),
                TipoPasajero.ADULTO,
                "1134567890",
                "Ninguna"
        );

        try {

            Reserva reserva =
                    controladorReservas.solicitarCrearReserva(
                            cliente,
                            salida,
                            vendedor,
                            List.of(pasajero1, pasajero2)
                    );

            // Pagar el total para cumplir con la seña y saldo.
            double importe =
                    Math.max(reserva.getTotal(), 0.01);

            Pago pago = new Pago(
                    901,
                    LocalDate.now(),
                    importe,
                    MedioPago.TRANSFERENCIA,
                    1
            );

            controladorReservas.registrarPago(reserva, pago);

            boolean confirmada =
                    controladorReservas.confirmarReserva(reserva);

            if (!confirmada) {
                System.out.println(
                        "No se pudo confirmar la reserva de prueba."
                );
                return;
            }

            repositorio.agregarPasajero(pasajero1);
            repositorio.agregarPasajero(pasajero2);

            System.out.println(
                    "Reserva de prueba "
                            + reserva.getNumero()
                            + " confirmada con 2 pasajeros."
            );

        } catch (Exception e) {
            System.out.println(
                    "Error al preparar la reserva de prueba: "
                            + e.getMessage()
            );
        }
    }

    // ---------- ASIGNACIÓN DE RECURSOS ----------

    private static void asignarRecursos(
            Repositorio repositorio,
            int codigoSalida,
            int legajoGuia,
            String patente) {

        Salida salida = repositorio.buscarSalida(codigoSalida);

        if (salida == null) {
            return;
        }

        if (salida.getGuia() == null) {
            salida.asignarGuia(
                    repositorio.buscarGuia(legajoGuia)
            );
        }

        if (salida.getVehiculo() == null) {
            salida.asignarVehiculo(
                    repositorio.buscarVehiculo(patente)
            );
        }
    }
}
