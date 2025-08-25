package model.miotrialfx.model;

public abstract class VisitaAstratta {
    /**
     * Aggiunge un valore per un determinato parametro.
     *
     * @param parametro il parametro per cui aggiungere il valore.
     * @param valore    il valore da aggiungere.
     */
    public abstract void addValoreParametro(Parametro parametro, Object valore);

}
