package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando un pago o un ajuste de pago tiene datos inválidos o la reserva no admite pagos.
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class PagoInvalidoException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public PagoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
