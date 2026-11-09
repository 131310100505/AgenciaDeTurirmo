package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando un pasajero no cumple las condiciones del tour (por ejemplo, la edad mínima en aventura).
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class PasajeroNoHabilitadoException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public PasajeroNoHabilitadoException(String mensaje) {
        super(mensaje);
    }
}
