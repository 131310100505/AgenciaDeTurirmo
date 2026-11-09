package ar.edu.uade.rumbosur.vista;

import ar.edu.uade.rumbosur.controlador.ControladorReservas;
import ar.edu.uade.rumbosur.controlador.ControladorSalidas;
import ar.edu.uade.rumbosur.excepciones.CupoInsuficienteException;
import ar.edu.uade.rumbosur.excepciones.PagoInvalidoException;
import ar.edu.uade.rumbosur.excepciones.PasajeroNoHabilitadoException;
import ar.edu.uade.rumbosur.excepciones.ReservaNoCancelableException;
import ar.edu.uade.rumbosur.modelo.AdicionalContratado;
import ar.edu.uade.rumbosur.modelo.AjustePago;
import ar.edu.uade.rumbosur.modelo.Cancelacion;
import ar.edu.uade.rumbosur.modelo.Cliente;
import ar.edu.uade.rumbosur.modelo.MedioPago;
import ar.edu.uade.rumbosur.modelo.OrigenCancelacion;
import ar.edu.uade.rumbosur.modelo.Pago;
import ar.edu.uade.rumbosur.modelo.Pasajero;
import ar.edu.uade.rumbosur.modelo.Reserva;
import ar.edu.uade.rumbosur.modelo.Salida;
import ar.edu.uade.rumbosur.modelo.ServicioAdicional;
import ar.edu.uade.rumbosur.modelo.TipoPasajero;
import ar.edu.uade.rumbosur.modelo.Vendedor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Menú de consola del Vendedor (MVC: es la Vista).
 *
 * Solo pide datos y muestra resultados: toda la lógica la hacen los controladores
 * y las entidades. Los errores de negocio llegan como excepciones y se muestran
 * con e.getMessage(), sin que el programa se corte.
 */
public class VistaVendedor {

    private static final int EDAD_MENOR = 12;

    private EntradaConsola entrada;
    private ControladorReservas controladorReservas;
    private ControladorSalidas controladorSalidas;
    private Vendedor vendedor;
    private List<ServicioAdicional> catalogoAdicionales;

    public VistaVendedor(EntradaConsola entrada, ControladorReservas controladorReservas,
                         ControladorSalidas controladorSalidas, Vendedor vendedor,
                         List<ServicioAdicional> catalogoAdicionales) {
        this.entrada = entrada;
        this.controladorReservas = controladorReservas;
        this.controladorSalidas = controladorSalidas;
        this.vendedor = vendedor;
        this.catalogoAdicionales = new ArrayList<>(catalogoAdicionales);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println();
            System.out.println("===== MENÚ VENDEDOR - " + vendedor.getNombre() + " =====");
            System.out.println(" 1. Registrar cliente");
            System.out.println(" 2. Registrar pasajero");
            System.out.println(" 3. Ver salidas");
            System.out.println(" 4. Crear reserva");
            System.out.println(" 5. Consultar reserva");
            System.out.println(" 6. Contratar servicio adicional");
            System.out.println(" 7. Registrar pago");
            System.out.println(" 8. Confirmar reserva");
            System.out.println(" 9. Agregar pasajero a una reserva");
            System.out.println("10. Reemplazar pasajero");
            System.out.println("11. Cancelar reserva");
            System.out.println("12. Registrar ajuste de un pago");
            System.out.println(" 0. Volver al menú principal");
            opcion = entrada.leerOpcion("Opción: ", 0, 12);
            ejecutar(opcion);
        } while (opcion != 0);
    }

    private void ejecutar(int opcion) {
        switch (opcion) {
            case 1:
                registrarCliente();
                break;
            case 2:
                registrarPasajero(entrada.leerTexto("Documento del pasajero: "));
                break;
            case 3:
                mostrarSalidas();
                break;
            case 4:
                crearReserva();
                break;
            case 5:
                consultarReserva();
                break;
            case 6:
                contratarAdicional();
                break;
            case 7:
                registrarPago();
                break;
            case 8:
                confirmarReserva();
                break;
            case 9:
                agregarPasajero();
                break;
            case 10:
                reemplazarPasajero();
                break;
            case 11:
                cancelarReserva();
                break;
            case 12:
                registrarAjuste();
                break;
            default:
                break; // 0: vuelve al menú principal
        }
    }

    // ---------- CU04 Registrar cliente y pasajero ----------

    private void registrarCliente() {
        String documento = entrada.leerTexto("Documento: ");
        if (controladorReservas.buscarCliente(documento) != null) {
            System.out.println("Ya existe un cliente con ese documento.");
            return;
        }
        String nombre = entrada.leerTexto("Nombre: ");
        String apellido = entrada.leerTexto("Apellido: ");
        String telefono = entrada.leerTexto("Teléfono: ");
        String correo = entrada.leerTexto("Correo electrónico: ");
        String pais = entrada.leerTexto("País de residencia: ");
        try {
            int codigo = controladorReservas.listarClientes().size() + 1;
            Cliente cliente = new Cliente(codigo, nombre, apellido, documento, telefono, correo, pais);
            controladorReservas.registrarCliente(cliente);
            System.out.println("Cliente registrado: " + cliente);
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    /** Registra un pasajero y lo devuelve; devuelve null si no se pudo. */
    private Pasajero registrarPasajero(String documento) {
        Pasajero existente = controladorReservas.buscarPasajero(documento);
        if (existente != null) {
            System.out.println("Ya existe el pasajero " + existente + ".");
            return existente;
        }
        String nombre = entrada.leerTexto("Nombre y apellido: ");
        LocalDate nacimiento = entrada.leerFecha("Fecha de nacimiento (dd/mm/aaaa): ");
        String contacto = entrada.leerTexto("Contacto de emergencia: ");
        String restricciones = entrada.leerTextoOpcional("Restricciones alimentarias o de salud (Enter si no tiene): ");
        TipoPasajero tipo = elegirTipo(nacimiento);
        try {
            int codigo = controladorReservas.listarPasajeros().size() + 1;
            Pasajero pasajero = new Pasajero(codigo, nombre, documento, nacimiento, tipo, contacto, restricciones);
            controladorReservas.registrarPasajero(pasajero);
            System.out.println("Pasajero registrado: " + pasajero);
            return pasajero;
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
            return null;
        }
    }

    /**
     * La categoría para el precio se calcula por edad a la fecha de la salida (S08).
     * Acá solo se registra si es jubilado, que es un dato que no sale de la edad.
     */
    private TipoPasajero elegirTipo(LocalDate nacimiento) {
        if (entrada.confirmar("¿Es jubilado?")) {
            return TipoPasajero.JUBILADO;
        }
        int edad = java.time.Period.between(nacimiento, LocalDate.now()).getYears();
        return edad < EDAD_MENOR ? TipoPasajero.MENOR : TipoPasajero.ADULTO;
    }

    // ---------- Salidas ----------

    private void mostrarSalidas() {
        List<Salida> salidas = controladorSalidas.listarSalidas(); // ordenadas por fecha (Comparable)
        if (salidas.isEmpty()) {
            System.out.println("No hay salidas cargadas.");
            return;
        }
        System.out.println("Código | Tour | Fecha | Ocupación | Estado");
        for (Salida salida : salidas) {
            System.out.println(describirSalida(salida));
        }
    }

    private String describirSalida(Salida salida) {
        return salida.getCodigo() + " | " + salida.getTour().getNombre() + " | " + salida.getFechaInicio()
                + " | " + salida.calcularOcupacion() + "/" + salida.getCupoMaximo() + " | " + salida.getEstado();
    }

    // ---------- CU05 Crear reserva ----------

    private void crearReserva() {
        Cliente cliente = pedirCliente();
        if (cliente == null) {
            return;
        }
        mostrarSalidas();
        Salida salida = pedirSalida();
        if (salida == null) {
            return;
        }
        int cantidad = entrada.leerOpcion("Cantidad de pasajeros: ", 1, salida.getCupoMaximo());
        List<Pasajero> pasajeros = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            Pasajero pasajero = pedirPasajero("Documento del pasajero " + i + ": ");
            if (pasajero == null) {
                System.out.println("Se cancela la carga de la reserva.");
                return;
            }
            pasajeros.add(pasajero);
        }
        try {
            Reserva reserva = controladorReservas.solicitarCrearReserva(cliente, salida, vendedor, pasajeros);
            System.out.println("Reserva creada:");
            System.out.println("  Número: " + reserva.getNumero());
            System.out.println("  Total: " + importe(reserva.getTotal()));
            System.out.println("  Estado: " + reserva.getEstado());
            System.out.println("  Seña mínima para confirmar (30 %): " + importe(reserva.calcularSenaMinima()));
        } catch (CupoInsuficienteException | PasajeroNoHabilitadoException e) { // multi-catch: igual tratamiento
            System.out.println("No se pudo crear la reserva: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en los datos: " + e.getMessage());
        }
    }

    // ---------- Consultar reserva ----------

    private void consultarReserva() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        System.out.println(reserva);
        System.out.println("Cliente: " + reserva.getCliente());
        System.out.println("Salida: " + describirSalida(reserva.getSalida()));
        System.out.println("Pasajeros:");
        for (Pasajero pasajero : reserva.getPasajeros()) {
            System.out.println("  - " + pasajero);
        }
        if (!reserva.getAdicionales().isEmpty()) {
            System.out.println("Adicionales:");
            for (AdicionalContratado adicional : reserva.getAdicionales()) {
                System.out.println("  - " + adicional);
            }
        }
        if (!reserva.getPagos().isEmpty()) {
            System.out.println("Pagos:");
            for (Pago pago : reserva.getPagos()) {
                System.out.println("  - " + pago);
            }
        }
        System.out.println("Total: " + importe(reserva.getTotal()) + " | Pagado: "
                + importe(reserva.calcularTotalPagado()) + " | Saldo: " + importe(reserva.calcularSaldo()));
        System.out.println("Historial:");
        for (String cambio : reserva.getHistorial()) {
            System.out.println("  " + cambio);
        }
    }

    // ---------- CU07 Contratar servicio adicional ----------

    private void contratarAdicional() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        for (int i = 0; i < catalogoAdicionales.size(); i++) {
            System.out.println((i + 1) + ". " + catalogoAdicionales.get(i));
        }
        int numero = entrada.leerOpcion("Servicio: ", 1, catalogoAdicionales.size());
        Pasajero pasajero = elegirPasajeroDe(reserva, "¿Para qué pasajero? ");
        int cantidad = entrada.leerOpcion("Cantidad: ", 1, 10);
        try {
            controladorReservas.contratarAdicional(reserva, catalogoAdicionales.get(numero - 1), pasajero, cantidad);
            System.out.println("Adicional contratado. Nuevo total: " + importe(reserva.getTotal()));
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo contratar: " + e.getMessage());
        }
    }

    // ---------- CU08 Registrar pago ----------

    private void registrarPago() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        System.out.println("Total: " + importe(reserva.getTotal()) + " | Pagado: "
                + importe(reserva.calcularTotalPagado()) + " | Saldo: " + importe(reserva.calcularSaldo()));
        double monto = entrada.leerImporte("Importe del pago: ");
        MedioPago medio = elegirMedioPago();
        int cuotas = 1;
        if (medio == MedioPago.TARJETA) {
            cuotas = entrada.leerOpcion("Cantidad de cuotas (1 a 12): ", 1, 12);
        }
        try {
            // Código único por reserva: número de reserva seguido del número de pago.
            int codigo = reserva.getNumero() * 1000 + reserva.getPagos().size() + 1;
            Pago pago = new Pago(codigo, LocalDate.now(), monto, medio, cuotas);
            controladorReservas.registrarPago(reserva, pago);
            System.out.println("Pago registrado: " + pago);
            if (pago.getRecargo() > 0) {
                System.out.println("  Recargo por cuotas (7 %): " + importe(pago.getRecargo()));
            }
            System.out.println("Pagado: " + importe(reserva.calcularTotalPagado())
                    + " | Saldo: " + importe(reserva.calcularSaldo()));
        } catch (PagoInvalidoException e) {
            System.out.println("No se pudo registrar el pago: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en los datos del pago: " + e.getMessage());
        }
    }

    private MedioPago elegirMedioPago() {
        MedioPago[] medios = MedioPago.values();
        for (int i = 0; i < medios.length; i++) {
            System.out.println((i + 1) + ". " + medios[i]);
        }
        return medios[entrada.leerOpcion("Medio de pago: ", 1, medios.length) - 1];
    }

    // ---------- CU09 Confirmar reserva ----------

    private void confirmarReserva() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        if (controladorReservas.confirmarReserva(reserva)) {
            System.out.println("Reserva " + reserva.getNumero() + " CONFIRMADA.");
        } else {
            System.out.println("No se pudo confirmar. Estado actual: " + reserva.getEstado()
                    + ". Pagado: " + importe(reserva.calcularTotalPagado())
                    + " | Seña mínima: " + importe(reserva.calcularSenaMinima()));
        }
    }

    // ---------- CU10 Agregar pasajero ----------

    private void agregarPasajero() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        Pasajero pasajero = pedirPasajero("Documento del nuevo pasajero: ");
        if (pasajero == null) {
            return;
        }
        try {
            controladorReservas.agregarPasajeroAReserva(reserva, pasajero);
            System.out.println("Pasajero agregado. Nuevo total: " + importe(reserva.getTotal()));
        } catch (CupoInsuficienteException | PasajeroNoHabilitadoException e) {
            System.out.println("No se pudo agregar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------- CU11 Reemplazar pasajero ----------

    private void reemplazarPasajero() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        Pasajero actual = elegirPasajeroDe(reserva, "¿Qué pasajero se reemplaza? ");
        Pasajero nuevo = pedirPasajero("Documento del pasajero nuevo: ");
        if (nuevo == null) {
            return;
        }
        try {
            controladorReservas.reemplazarPasajero(reserva, actual, nuevo);
            System.out.println("Pasajero reemplazado. Total: " + importe(reserva.getTotal()));
        } catch (PasajeroNoHabilitadoException e) {
            System.out.println("El nuevo pasajero no cumple las condiciones: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo reemplazar: " + e.getMessage());
        }
    }

    // ---------- CU12 Cancelar reserva ----------

    private void cancelarReserva() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        String motivo = entrada.leerTexto("Motivo de la cancelación: ");
        System.out.println("1. La pide el cliente");
        System.out.println("2. La decide la agencia");
        OrigenCancelacion origen = entrada.leerOpcion("Origen: ", 1, 2) == 1
                ? OrigenCancelacion.CLIENTE : OrigenCancelacion.AGENCIA;
        if (!entrada.confirmar("Se va a cancelar la reserva " + reserva.getNumero() + ". ¿Continuar?")) {
            return;
        }
        try {
            Cancelacion cancelacion = controladorReservas.cancelarReserva(reserva, motivo, origen);
            System.out.println("Reserva cancelada. Importe a devolver: " + importe(cancelacion.getImporteDevuelto()));
        } catch (ReservaNoCancelableException e) {
            System.out.println("No se puede cancelar: " + e.getMessage());
        }
    }

    // ---------- CU19 Registrar ajuste ----------

    private void registrarAjuste() {
        Reserva reserva = pedirReserva();
        if (reserva == null) {
            return;
        }
        List<Pago> pagos = reserva.getPagos();
        if (pagos.isEmpty()) {
            System.out.println("La reserva no tiene pagos.");
            return;
        }
        for (int i = 0; i < pagos.size(); i++) {
            System.out.println((i + 1) + ". " + pagos.get(i));
        }
        Pago pago = pagos.get(entrada.leerOpcion("Pago a ajustar: ", 1, pagos.size()) - 1);
        double monto = entrada.leerImporte("Importe del ajuste (sin signo): ");
        if (entrada.confirmar("¿El ajuste resta del pago?")) {
            monto = -monto;
        }
        String motivo = entrada.leerTexto("Motivo: ");
        try {
            int codigo = pago.getCodigo() * 100 + pago.getAjustes().size() + 1;
            controladorReservas.registrarAjuste(reserva, pago, new AjustePago(codigo, LocalDate.now(), monto, motivo));
            System.out.println("Ajuste registrado. Pagado neto: " + importe(reserva.calcularTotalPagado()));
        } catch (PagoInvalidoException e) {
            System.out.println("No se pudo registrar el ajuste: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en los datos: " + e.getMessage());
        }
    }

    // ---------- Auxiliares para pedir datos ----------

    private Cliente pedirCliente() {
        String documento = entrada.leerTexto("Documento del cliente: ");
        Cliente cliente = controladorReservas.buscarCliente(documento);
        if (cliente == null) {
            System.out.println("No existe un cliente con ese documento. Regístrelo con la opción 1.");
        }
        return cliente;
    }

    private Salida pedirSalida() {
        int codigo = entrada.leerEntero("Código de la salida: ");
        Salida salida = controladorSalidas.buscarSalida(codigo);
        if (salida == null) {
            System.out.println("No existe una salida con ese código.");
        }
        return salida;
    }

    private Reserva pedirReserva() {
        int numero = entrada.leerEntero("Número de reserva: ");
        Reserva reserva = controladorReservas.buscarReserva(numero);
        if (reserva == null) {
            System.out.println("No existe la reserva " + numero + ".");
        }
        return reserva;
    }

    /** Busca el pasajero por documento; si no existe, ofrece registrarlo en el momento. */
    private Pasajero pedirPasajero(String mensaje) {
        String documento = entrada.leerTexto(mensaje);
        Pasajero pasajero = controladorReservas.buscarPasajero(documento);
        if (pasajero != null) {
            System.out.println("  " + pasajero);
            return pasajero;
        }
        if (entrada.confirmar("  El pasajero no está registrado. ¿Registrarlo ahora?")) {
            return registrarPasajero(documento);
        }
        return null;
    }

    private Pasajero elegirPasajeroDe(Reserva reserva, String mensaje) {
        List<Pasajero> pasajeros = reserva.getPasajeros();
        for (int i = 0; i < pasajeros.size(); i++) {
            System.out.println((i + 1) + ". " + pasajeros.get(i));
        }
        return pasajeros.get(entrada.leerOpcion(mensaje, 1, pasajeros.size()) - 1);
    }

    private String importe(double valor) {
        return String.format("$%.2f", valor);
    }
}
