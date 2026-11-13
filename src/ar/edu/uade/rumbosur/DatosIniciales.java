package ar.edu.uade.rumbosur;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.modelo.*;
import ar.edu.uade.rumbosur.persistencia.Repositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Datos de prueba que se cargan la primera vez que se usa el sistema
 * (cuando todavía no existe el archivo rumbosur.dat). Los precios son de ejemplo.
 */
public class DatosIniciales {

    public static void cargarEn(Repositorio repositorio) {
        LocalDate hoy = LocalDate.now();

        // Tours, cada uno con una tarifa vigente por un año
        Tour lagos = new TourNaturaleza(1, "Lagos de Bariloche",
                "Recorrido por los lagos y paisajes de Bariloche", 3, 2, 20);
        Tour trekking = new TourAventura(2, "Trekking en la Patagonia",
                "Caminata guiada por senderos de montaña", 2, 2, 15, 16, "Media");
        Tour historia = new TourCultural(3, "Historia de Bariloche",
                "Visita a lugares históricos y culturales", 1, 2, 25);
        lagos.agregarTarifa(new Tarifa(1, Temporada.MEDIA, hoy, hoy.plusDays(365), 150000));
        trekking.agregarTarifa(new Tarifa(1, Temporada.MEDIA, hoy, hoy.plusDays(365), 200000));
        historia.agregarTarifa(new Tarifa(1, Temporada.MEDIA, hoy, hoy.plusDays(365), 80000));
        // Itinerario: una actividad por tour; la del trekking la presta un proveedor externo
        Proveedor patagonia = new Proveedor(1, "Patagonia Aventura", "30-71234567-8", "2944000100", "Excursiones");
        Actividad ascenso = new Actividad(1, 1, 1, LocalTime.of(8, 0), "Ascenso a Laguna Torre",
                "El Chaltén", TipoActividad.EXCURSION);
        ascenso.asignarProveedor(patagonia);
        trekking.agregarActividad(ascenso);
        lagos.agregarActividad(new Actividad(1, 1, 1, LocalTime.of(9, 0), "Navegación por el lago Nahuel Huapi",
                "Puerto Pañuelo", TipoActividad.EXCURSION));
        historia.agregarActividad(new Actividad(1, 1, 1, LocalTime.of(10, 0), "Visita al Centro Cívico",
                "Bariloche", TipoActividad.EXCURSION));
        repositorio.agregarTour(lagos);
        repositorio.agregarTour(trekking);
        repositorio.agregarTour(historia);

        // Guías habilitados para una categoría cada uno
        Guia martin = new Guia(201, "Martín López", "30111222", "2944000001",
                List.of("Español", "Inglés"), Set.of(CategoriaTour.NATURALEZA), 45000);
        Guia lucia = new Guia(202, "Lucía Fernández", "30222333", "2944000002",
                List.of("Español", "Inglés"), Set.of(CategoriaTour.AVENTURA), 50000);
        Guia carlos = new Guia(203, "Carlos Gómez", "30333444", "2944000003",
                List.of("Español", "Portugués"), Set.of(CategoriaTour.CULTURAL), 40000);
        repositorio.agregarGuia(martin);
        repositorio.agregarGuia(lucia);
        repositorio.agregarGuia(carlos);

        Vehiculo minibus = new Vehiculo("AB123CD", 20, TipoVehiculo.MINIBUS);
        Vehiculo combi = new Vehiculo("AC456EF", 15, TipoVehiculo.COMBI);
        Vehiculo micro = new Vehiculo("AD789GH", 40, TipoVehiculo.MICRO);
        repositorio.agregarVehiculo(minibus);
        repositorio.agregarVehiculo(combi);
        repositorio.agregarVehiculo(micro);

        // Salidas con su guía y su vehículo
        LocalDate fecha = hoy.plusDays(20);
        Salida salida101 = new Salida(101, fecha, fecha.plusDays(2), 2, 20, lagos);
        Salida salida102 = new Salida(102, fecha.plusDays(5), fecha.plusDays(6), 2, 15, trekking);
        Salida salida103 = new Salida(103, fecha.plusDays(10), fecha.plusDays(10), 2, 25, historia);
        salida101.asignarGuia(martin);
        salida101.asignarVehiculo(minibus);
        salida102.asignarGuia(lucia);
        salida102.asignarVehiculo(combi);
        salida103.asignarGuia(carlos);
        salida103.asignarVehiculo(micro);
        repositorio.agregarSalida(salida101);
        repositorio.agregarSalida(salida102);
        repositorio.agregarSalida(salida103);

        repositorio.agregarCliente(new Cliente(301, "Ana", "Pérez", "40111222",
                "1123456789", "ana@example.com", "Argentina"));
        repositorio.agregarCliente(new Cliente(302, "Pedro", "García", "40222333",
                "1134567890", "pedro@example.com", "Argentina"));
        repositorio.agregarCliente(new Cliente(303, "María", "Rodríguez", "40333444",
                "1145678901", "maria@example.com", "Chile"));

        repositorio.agregarVendedor(new Vendedor(401, "Sofía Martínez", "35111222", "1156789012", 0.05));
        repositorio.agregarVendedor(new Vendedor(402, "Diego Ramírez", "35222333", "1167890123", 0.05));

        System.out.println("Se cargaron los datos iniciales de prueba.");
    }

    /**
     * Reserva de prueba en la salida 101: dos pasajeros, pagada y confirmada.
     * Se crea con el controlador, igual que desde el menú del vendedor.
     */
    public static void cargarReservaPrueba(Repositorio repositorio, ControladorReservas controladorReservas) {
        Pasajero juan = new Pasajero(501, "Juan Pérez", "45111222", LocalDate.of(2000, 5, 15),
                TipoPasajero.ADULTO, "1123456789", "Ninguna");
        Pasajero lucia = new Pasajero(502, "Lucía Gómez", "45222333", LocalDate.of(2001, 8, 20),
                TipoPasajero.ADULTO, "1134567890", "Ninguna");
        try {
            controladorReservas.registrarPasajero(juan);
            controladorReservas.registrarPasajero(lucia);
            Reserva reserva = controladorReservas.solicitarCrearReserva(
                    controladorReservas.buscarCliente("40111222"), repositorio.buscarSalida(101),
                    repositorio.buscarVendedor(401), List.of(juan, lucia));
            controladorReservas.registrarPago(reserva,
                    new Pago(901, LocalDate.now(), reserva.getTotal(), MedioPago.TRANSFERENCIA, 1));
            controladorReservas.confirmarReserva(reserva);
            System.out.println("Reserva de prueba " + reserva.getNumero() + " confirmada con 2 pasajeros.");
        } catch (Exception e) {
            System.out.println("No se pudo crear la reserva de prueba: " + e.getMessage());
        }
    }
}
