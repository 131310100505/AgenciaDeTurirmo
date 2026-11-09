package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando se intenta cancelar una reserva que ya está anulada, finalizada o fuera de condiciones.
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class ReservaNoCancelableException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public ReservaNoCancelableException(String mensaje) {
        super(mensaje);
    }
}
