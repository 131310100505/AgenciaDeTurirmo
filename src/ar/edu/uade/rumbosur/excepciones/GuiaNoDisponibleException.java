package ar.edu.uade.rumbosur.excepciones;

/**
 * Se lanza cuando el guía no está habilitado para la categoría del tour o ya tiene otra salida en esas fechas.
 *
 * Es una excepción verificada (extends Exception): quien llama al método
 * que la lanza está obligado a declararla con throws o a capturarla.
 */
public class GuiaNoDisponibleException extends Exception {

    // Exception es Serializable, así que también lleva su versión.
    private static final long serialVersionUID = 1L;

    public GuiaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
