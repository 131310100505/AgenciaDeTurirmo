package ar.edu.uade.rumbosur.modelo;

import ar.edu.uade.rumbosur.excepciones.PagoInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagoTest {

    private static final double MARGEN = 0.001;
    private static final LocalDate FECHA = LocalDate.of(2026, 11, 8);

    private Pago crearPago(double importe, MedioPago medio, int cuotas) {
        return new Pago(1, FECHA, importe, medio, cuotas);
    }

    private AjustePago crearAjuste(double importe) {
        return new AjustePago(1, FECHA, importe, "Corrección de carga");
    }

    @Test
    void tarjetaEnCuotasTieneRecargoDelSietePorCiento() {
        Pago pago = crearPago(1000, MedioPago.TARJETA, 3);
        assertEquals(70, pago.getRecargo(), MARGEN);
        assertEquals(1070, pago.obtenerImporteTotal(), MARGEN);
    }

    @Test
    void tarjetaEnUnPagoNoTieneRecargo() {
        assertEquals(0, crearPago(1000, MedioPago.TARJETA, 1).getRecargo(), MARGEN);
    }

    @Test
    void otrosMediosNoTienenRecargo() {
        assertEquals(0, crearPago(1000, MedioPago.EFECTIVO, 1).getRecargo(), MARGEN);
        assertEquals(0, crearPago(1000, MedioPago.TRANSFERENCIA, 3).getRecargo(), MARGEN);
    }

    @Test
    void elRecargoNoCuentaEnElImporteNeto() {
        assertEquals(1000, crearPago(1000, MedioPago.TARJETA, 3).getImporteNeto(), MARGEN);
    }

    @Test
    void losAjustesSeAplicanAlImporteNeto() throws PagoInvalidoException {
        Pago pago = crearPago(1000, MedioPago.EFECTIVO, 1);
        pago.agregarAjuste(crearAjuste(-200));
        pago.agregarAjuste(crearAjuste(50));
        assertEquals(850, pago.getImporteNeto(), MARGEN);
        assertEquals(2, pago.getAjustes().size());
    }

    @Test
    void unAjusteNoModificaElPagoOriginal() throws PagoInvalidoException {
        Pago pago = crearPago(1000, MedioPago.TARJETA, 3);
        pago.agregarAjuste(crearAjuste(-200));
        assertEquals(1000, pago.getImporte(), MARGEN);
        assertEquals(70, pago.getRecargo(), MARGEN);
    }

    @Test
    void unAjusteQueAnulaElPagoEsValido() throws PagoInvalidoException {
        Pago pago = crearPago(1000, MedioPago.EFECTIVO, 1);
        pago.agregarAjuste(crearAjuste(-1000));
        assertEquals(0, pago.getImporteNeto(), MARGEN);
    }

    @Test
    void rechazaUnAjusteQueDejaElPagoEnNegativo() {
        Pago pago = crearPago(1000, MedioPago.EFECTIVO, 1);
        assertThrows(PagoInvalidoException.class, () -> pago.agregarAjuste(crearAjuste(-1000.01)));
        assertEquals(1000, pago.getImporteNeto(), MARGEN);
    }

    @Test
    void rechazaUnAjusteEnCero() {
        Pago pago = crearPago(1000, MedioPago.EFECTIVO, 1);
        assertThrows(PagoInvalidoException.class, () -> pago.agregarAjuste(crearAjuste(0)));
    }

    @Test
    void losAjustesNoSePuedenBorrarDesdeAfuera() throws PagoInvalidoException {
        Pago pago = crearPago(1000, MedioPago.EFECTIVO, 1);
        pago.agregarAjuste(crearAjuste(-200));
        assertThrows(UnsupportedOperationException.class, () -> pago.getAjustes().clear());
    }

    @Test
    void rechazaDatosInvalidosAlCrearElPago() {
        assertThrows(IllegalArgumentException.class, () -> crearPago(0, MedioPago.EFECTIVO, 1));
        assertThrows(IllegalArgumentException.class, () -> crearPago(-1, MedioPago.EFECTIVO, 1));
        assertThrows(IllegalArgumentException.class, () -> crearPago(1000, null, 1));
        assertThrows(IllegalArgumentException.class, () -> crearPago(1000, MedioPago.TARJETA, 0));
    }
}
