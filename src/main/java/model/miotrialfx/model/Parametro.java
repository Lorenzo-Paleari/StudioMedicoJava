package model.miotrialfx.model;

/**
 * Enumerazione che rappresenta i vari parametri utilizzati negli esperimenti.
 */
public enum Parametro {
    TEMPERATURA(Type.NUMERIC),
    PRESSIONE(Type.SCALAR),
    CAPACITA_POLMONARE(Type.SCALAR),
    PESO(Type.NUMERIC);

    final Type tipo;

    /**
     * Costruttore per il parametro con il tipo specificato.
     * @param tipo Il tipo del parametro
     */
    Parametro(Type tipo) {
        this.tipo = tipo;
    }
    /**
     * Metodo per ottenere il tipo del parametro.
     * @return Il tipo del parametro
     */
    public Type getType() {
        return tipo;
    }
    /**
     * Enumerazione che rappresenta i tipi di parametro.
     */
    public enum Type {SCALAR, NUMERIC}
}