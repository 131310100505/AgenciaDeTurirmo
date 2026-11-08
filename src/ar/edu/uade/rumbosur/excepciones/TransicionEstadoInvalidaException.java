package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando se pide un cambio de estado que no está permitido (por ejemplo, iniciar una salida sin guía o sin lista de pasajeros).
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class TransicionEstadoInvalidaException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
