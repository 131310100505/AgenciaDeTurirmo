package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorCatalogo;
import ar.edu.uade.rumbosur.modelo.Actividad;
import ar.edu.uade.rumbosur.modelo.CategoriaTour;
import ar.edu.uade.rumbosur.modelo.Tarifa;
import ar.edu.uade.rumbosur.modelo.Temporada;
import ar.edu.uade.rumbosur.modelo.TipoActividad;
import ar.edu.uade.rumbosur.modelo.Tour;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Menú del administrador para el catálogo de tours (CU01 y CU02).
 * MVC: solo pide datos y muestra resultados; las validaciones están en ControladorCatalogo.
 */
public class VistaCatalogo {

    private final EntradaConsola entrada;
    private final ControladorCatalogo controladorCatalogo;

    public VistaCatalogo(EntradaConsola entrada, ControladorCatalogo controladorCatalogo) {
        this.entrada = entrada;
        this.controladorCatalogo = controladorCatalogo;
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println();
            System.out.println("--- Administrador: catálogo de tours ---");
            System.out.println("1. Listar tours");
            System.out.println("2. Registrar tour");
            System.out.println("3. Ver itinerario y tarifas de un tour");
            System.out.println("4. Agregar actividad al itinerario");
            System.out.println("5. Agregar tarifa por temporada");
            System.out.println("6. Modificar tour");
            System.out.println("0. Volver");
            opcion = entrada.leerOpcion("Opción: ", 0, 6);
            try {
                switch (opcion) {
                    case 1:
                        listarTours();
                        break;
                    case 2:
                        registrarTour();
                        break;
                    case 3:
                        verDetalle();
                        break;
                    case 4:
                        agregarActividad();
                        break;
                    case 5:
                        agregarTarifa();
                        break;
                    case 6:
                        modificarTour();
                        break;
                    default:
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void listarTours() {
        List<Tour> tours = controladorCatalogo.listarTours();
        if (tours.isEmpty()) {
            System.out.println("No hay tours cargados.");
            return;
        }
        for (Tour tour : tours) {
            System.out.println(tour.getCodigo() + ". " + tour.getNombre()
                    + " | " + tour.obtenerCategoria()
                    + " | " + tour.getDuracionDias() + " día(s)"
                    + " | cupo " + tour.getCupoMinimo() + " a " + tour.getCupoMaximo()
                    + " | " + tour.getActividades().size() + " actividad(es)"
                    + " | " + tour.getTarifas().size() + " tarifa(s)");
        }
    }

    private void registrarTour() {
        System.out.println("Categoría: 1. Aventura  2. Cultural  3. Naturaleza");
        CategoriaTour categoria = CategoriaTour.values()[entrada.leerOpcion("Opción: ", 1, 3) - 1];
        String nombre = entrada.leerTexto("Nombre: ");
        String descripcion = entrada.leerTextoOpcional("Descripción: ");
        int duracion = entrada.leerOpcion("Duración en días: ", 1, 60);
        int cupoMinimo = entrada.leerOpcion("Cupo mínimo: ", 1, 500);
        int cupoMaximo = entrada.leerOpcion("Cupo máximo: ", 1, 500);
        int edadMinima = 0;
        String dificultad = null;
        if (categoria == CategoriaTour.AVENTURA) {
            edadMinima = entrada.leerOpcion("Edad mínima: ", 0, 99);
            dificultad = entrada.leerTexto("Dificultad (por ejemplo baja, media o alta): ");
        }
        Tour tour = controladorCatalogo.registrarTour(categoria, nombre, descripcion,
                duracion, cupoMinimo, cupoMaximo, edadMinima, dificultad);
        System.out.println("Tour registrado con el código " + tour.getCodigo()
                + ". Recuerde cargarle al menos una tarifa antes de programar salidas.");
    }

    private void modificarTour() {
        int codigo = leerCodigoTour();
        Tour tour = controladorCatalogo.buscarTour(codigo);
        System.out.println("Enter deja el nombre o la descripción como están.");
        String nombre = entrada.leerTextoOpcional("Nombre (" + tour.getNombre() + "): ");
        String descripcion = entrada.leerTextoOpcional("Descripción (" + tour.getDescripcion() + "): ");
        int duracion = entrada.leerOpcion("Duración en días (" + tour.getDuracionDias() + "): ", 1, 60);
        int cupoMinimo = entrada.leerOpcion("Cupo mínimo (" + tour.getCupoMinimo() + "): ", 1, 500);
        int cupoMaximo = entrada.leerOpcion("Cupo máximo (" + tour.getCupoMaximo() + "): ", 1, 500);
        controladorCatalogo.modificarTour(codigo,
                nombre.isEmpty() ? tour.getNombre() : nombre,
                descripcion.isEmpty() ? tour.getDescripcion() : descripcion,
                duracion, cupoMinimo, cupoMaximo);
        System.out.println("Tour modificado. Las salidas ya programadas conservan sus fechas y cupos.");
    }

    private void verDetalle() {
        int codigo = leerCodigoTour();
        Tour tour = controladorCatalogo.buscarTour(codigo);
        System.out.println(tour.getNombre() + " (" + tour.obtenerCategoria() + ")");
        System.out.println("Itinerario:");
        List<Actividad> itinerario = controladorCatalogo.listarItinerario(codigo);
        if (itinerario.isEmpty()) {
            System.out.println("  Sin actividades cargadas.");
        }
        for (Actividad actividad : itinerario) {
            System.out.println("  Día " + actividad.getDia() + " - " + actividad.getOrden() + ". "
                    + actividad.getHorario() + " " + actividad.getDescripcion()
                    + " (" + actividad.getTipo() + ", " + actividad.getLugar() + ")");
        }
        System.out.println("Tarifas:");
        if (tour.getTarifas().isEmpty()) {
            System.out.println("  Sin tarifas cargadas.");
        }
        for (Tarifa tarifa : tour.getTarifas()) {
            System.out.printf("  %s: del %s al %s, $%.2f por pasajero%n", tarifa.getTemporada(),
                    tarifa.getFechaInicioVigencia(), tarifa.getFechaFinVigencia(), tarifa.getPrecioBase());
        }
    }

    private void agregarActividad() {
        int codigo = leerCodigoTour();
        int dia = entrada.leerOpcion("Día del itinerario: ", 1, 60);
        int orden = entrada.leerOpcion("Orden dentro del día: ", 1, 50);
        LocalTime horario = entrada.leerHora("Horario (hh:mm): ");
        String descripcion = entrada.leerTexto("Descripción: ");
        String lugar = entrada.leerTextoOpcional("Lugar: ");
        TipoActividad[] tipos = TipoActividad.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i]);
        }
        TipoActividad tipo = tipos[entrada.leerOpcion("Tipo: ", 1, tipos.length) - 1];
        controladorCatalogo.agregarActividad(codigo, dia, orden, horario, descripcion, lugar, tipo);
        System.out.println("Actividad agregada.");
    }

    private void agregarTarifa() {
        int codigo = leerCodigoTour();
        Temporada[] temporadas = Temporada.values();
        for (int i = 0; i < temporadas.length; i++) {
            System.out.println((i + 1) + ". " + temporadas[i]);
        }
        Temporada temporada = temporadas[entrada.leerOpcion("Temporada: ", 1, temporadas.length) - 1];
        LocalDate desde = entrada.leerFecha("Vigente desde (dd/mm/aaaa): ");
        LocalDate hasta = entrada.leerFecha("Vigente hasta (dd/mm/aaaa): ");
        double precio = entrada.leerImporte("Precio base por pasajero: $");
        controladorCatalogo.agregarTarifa(codigo, temporada, desde, hasta, precio);
        System.out.println("Tarifa agregada.");
    }

    private int leerCodigoTour() {
        listarTours();
        int codigo = entrada.leerOpcion("Código del tour: ", 1, Integer.MAX_VALUE);
        if (controladorCatalogo.buscarTour(codigo) == null) {
            throw new IllegalArgumentException("No existe un tour con código " + codigo + ".");
        }
        return codigo;
    }
}
