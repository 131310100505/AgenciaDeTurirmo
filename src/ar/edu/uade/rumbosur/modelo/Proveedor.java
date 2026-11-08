
package ar.edu.uade.rumbosur.modelo;

import java.io.Serializable;

public class Proveedor implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos del proveedor
    private int codigo;
    private String nombre;
    private String cuit;
    private String contacto;
    private String tipoServicio;

    // Constructor
    public Proveedor(int codigo, String nombre, String cuit,
                     String contacto, String tipoServicio) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.cuit = cuit;
        this.contacto = contacto;
        this.tipoServicio = tipoServicio;
    }

    // Getters
    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public String getContacto() {
        return contacto;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }
}
