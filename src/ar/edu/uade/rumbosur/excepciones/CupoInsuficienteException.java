package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando la salida no tiene lugares suficientes para los pasajeros de la reserva.
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class CupoInsuficienteException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public CupoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
