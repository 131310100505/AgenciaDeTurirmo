package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Persona que viaja. Una misma persona puede participar en varias reservas.
 */
public class Pasajero implements Serializable {

    // Versión de la clase para la serialización: así el archivo guardado se puede volver a leer.
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nombre;
    private String documento;
    private LocalDate fechaNacimiento;
    private TipoPasajero tipo;
    private String contactoEmergencia;
    private String restricciones; // alimentarias o de salud, para el guía

    public Pasajero(int codigo, String nombre, String documento, LocalDate fechaNacimiento,
                    TipoPasajero tipo, String contactoEmergencia, String restricciones) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("El documento del pasajero es obligatorio.");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
    }

    /** Edad cumplida a una fecha dada (se usa la fecha de inicio de la salida, supuesto S08). */
    public int calcularEdad(LocalDate fecha) {
        return Period.between(fechaNacimiento, fecha).getYears();
    }

    public boolean cumpleEdadMinima(int edadMinima, LocalDate fecha) {
        return calcularEdad(fecha) >= edadMinima;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public TipoPasajero getTipo() {
        return tipo;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getRestricciones() {
        return restricciones;
    }

    // Dos pasajeros son la misma persona si tienen el mismo documento.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pasajero)) {
            return false;
        }
        Pasajero otro = (Pasajero) obj;
        return documento.equals(otro.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }

    @Override
    public String toString() {
        return nombre + " (DNI " + documento + ", " + tipo + ")";
    }
}
