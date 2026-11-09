package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Cliente titular de las reservas. No usa el sistema directamente:
 * sus pedidos los carga el vendedor.
 */
public class Cliente implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nombre;
    private String apellido;
    private String documento;
    private String telefono;
    private String correo;
    private String paisResidencia;

    // Asociación 1 a 0..*: un cliente puede tener muchas reservas.
    private List<Reserva> reservas;

    public Cliente(int codigo, String nombre, String apellido, String documento,
                   String telefono, String correo, String paisResidencia) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del cliente es obligatorio.");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
        this.paisResidencia = paisResidencia;
        this.reservas = new ArrayList<>();
    }

    /**
     * GRASP Creador: el cliente agrupa y registra sus reservas,
     * por eso es quien las crea. Las validaciones (cupo, edad mínima)
     * las hace antes ControladorReservas, y los pasajeros los agrega
     * el controlador uno por uno (loop del diagrama de secuencia CU05).
     */
    public Reserva crearReserva(Salida salida, Vendedor vendedor) {
        Reserva reserva = new Reserva(this, salida, vendedor);
        reservas.add(reserva);
        return reserva;
    }

    public void actualizarContacto(String telefono, String correo) {
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPaisResidencia() {
        return paisResidencia;
    }

    /** Devuelve una vista de solo lectura para no romper el encapsulamiento. */
    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // Dos clientes son el mismo si tienen el mismo documento.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cliente)) {
            return false;
        }
        Cliente otro = (Cliente) obj;
        return documento.equals(otro.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return codigo + " - " + apellido + ", " + nombre + " (DNI " + documento + ")";
    }
}
