package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando el vehículo no tiene capacidad para el cupo máximo de la salida.
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class VehiculoSinCapacidadException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public VehiculoSinCapacidadException(String mensaje) {
        super(mensaje);
    }
}
