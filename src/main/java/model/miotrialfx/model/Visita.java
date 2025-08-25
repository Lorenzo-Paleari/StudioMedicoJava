package model.miotrialfx.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * La classe Visita rappresenta le informazioni registrate a una visita medica.
 */
public class Visita extends VisitaAstratta {

    private LocalDate datavisita;
    private Map<Parametro, Object> valori;
    /**
     * Generatore: costruisce un'istanza della classe Visita con i parametri specificati.
     *
     * @param datavisita La data della visita.
     */
    public Visita(LocalDate datavisita) {
        this.datavisita = datavisita;
        this.valori = new HashMap<>();
    }
    /**
     * Aggiunge un valore per un determinato parametro.
     *
     * @param parametro il parametro per cui aggiungere il valore.
     * @param valore il valore da aggiungere.
     */
    @Override
    public void addValoreParametro(Parametro parametro, Object valore) {
        if(parametro.getType() == Parametro.Type.NUMERIC){
            if(valore instanceof Number && checkNumeric(parametro,(double) valore)) valori.put(parametro, valore); }
        else if(parametro.getType() == Parametro.Type.SCALAR){
            if(checkScalar((String) valore)) valori.put(parametro, valore);}
    }
    /**
     * Controlla se il valore per un parametro numerico è valido.
     *
     * @param valore il valore da controllare.
     * @param parametro che dato è.
     * @return true se il valore è valido, altrimenti false.
     */
    boolean checkNumeric(Parametro parametro, double valore) {
        return (parametro == Parametro.TEMPERATURA && valore < 50) || (parametro == Parametro.PESO && valore < 600);
    }
    /**
     * Controlla se il valore per un parametro scalare è valido.
     *
     * @param valore il valore da controllare.
     * @return true se il valore è valido, altrimenti false.
     */
    boolean checkScalar(String valore){
        return valore.equals("bassa") || valore.equals("media") || valore.equals("alta");
    }
    /**
     * Restituisce la data della visita.
     *
     * @return la data della visita.
     */
    public LocalDate getDataVisita() {
        return datavisita;
    }
    /**
     * Restituisce il valore associato a un parametro specifico.
     *
     * @param parametro il parametro di cui ottenere il valore.
     * @return il valore associato al parametro, o null se non esiste.
     */
    public Object getValoreParametro(Parametro parametro) {
        return valori.getOrDefault(parametro, null); //mi dava errore se non esisteva
    }
    /**
     * Imposta una nuova data per la visita.
     *
     * @param newData la nuova data della visita.
     */
    public void setDatavisita(LocalDate newData) {
        this.datavisita = newData;
    }
    /**
     * Restituisce la mappa dei valori dei parametri.
     *
     * @return la mappa dei valori dei parametri.
     */
    public Map<Parametro, Object> getValori() {
        return valori;
    }

    @Override
    public String toString() {
        return "Visita{" +
                "datavisita=" + datavisita +
                ", valori=" + valori +
                '}';
    }
}