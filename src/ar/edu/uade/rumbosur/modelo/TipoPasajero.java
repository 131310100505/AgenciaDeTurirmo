
package ar.edu.uade.rumbosur.modelo;

public enum TipoPasajero {

    ADULTO(1.0),
    MENOR(0.6),
    JUBILADO(1.0);

    private final double porcentajeTarifa;

    TipoPasajero(double porcentajeTarifa) {
        this.porcentajeTarifa = porcentajeTarifa;
    }

    public double getPorcentajeTarifa() {
        return porcentajeTarifa;
    }
}
