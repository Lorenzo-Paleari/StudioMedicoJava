package model.miotrialfx.model;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe che rappresenta un record di informazioni mediche di un paziente.
 */
public class Record {
    /**
     * Insieme di ID univoci associati ai record,
     * utilizzato per verificare che non ci siano rindondanze di ID.
     */
    public static Set<String> ids;

    static {
        ids = new HashSet<>();
    }
    private final String id;
    private final int nascita;
    private final LocalDate iscrizione;
    private List<Visita> visite;

    /**
     * Costruttore per un record
     *
     * @param id          ID del record
     * @param nascita     Anno di nascita del paziente
     * @param iscrizione  Data di iscrizione del paziente
     * @throws IllegalArgumentException se l'ID è già presente o se i valori passati sono errati
     */
    public Record(String id,int nascita,LocalDate iscrizione) {
        if (id == null || nascita < 0 || nascita > LocalDate.now().getYear() || !DataValida(iscrizione)){
            throw new IllegalArgumentException("Creazione Oggetto fallita per valori sbagliati");
        }
        if (ids.contains(id)) {
            throw new IllegalArgumentException("ID già presente");
        }
        if (iscrizione.isBefore(LocalDate.ofYearDay(nascita, 1))) {
            throw new IllegalArgumentException("La data di iscrizione non può essere antecedente alla data di nascita");
        }
        this.id = id;
        ids.add(id);
        this.nascita = nascita;
        this.iscrizione = iscrizione;
        this.visite = new ArrayList<>();
    }
    /**
     * Costruttore per un record generato con ID casuale.
     *
     * @param nascita     Anno di nascita del paziente
     * @param iscrizione  Data di iscrizione del paziente
     * @throws IllegalArgumentException se i valori passati sono errati
     */
    public Record(int nascita,LocalDate iscrizione) {
        if (nascita < 0 || nascita > LocalDate.now().getYear() || !DataValida(iscrizione)){
            throw new IllegalArgumentException("Creazione Oggetto fallita per valori sbagliati");
        }

        SecureRandom random = new SecureRandom();
        String newId;

        do {
            int randomNumber = 1000 + random.nextInt(9000);
            newId = "" + randomNumber;
        } while (ids.contains(newId));

        this.id = newId;
        ids.add(newId);
        this.nascita=nascita;
        this.iscrizione = iscrizione;
        this.visite = new ArrayList<>();
    }
    //setter
    public void setVisite(List<Visita> visite) {
        this.visite = visite;
    }
    /**
     * Aggiunge una visita alla lista delle visite associate al record.
     *
     * @param v la visita da aggiungere
     */
    public void addVisit(Visita v) {
        this.visite.add(v);
    }
    //Getter
    /**
     * @return l'ID del record
     */
    public String getId(){
        return this.id;
    }
    /**
     * @return int: l'anno di nascita
     */
    public int getNascita() {
        return this.nascita;
    }
    /**
     * @return LocalDate: data d'iscrizione
     */
    public LocalDate getIscrizione() {
        return this.iscrizione;
    }
    /**
     * @return una lista di visite
     */
    public List<Visita> getVisite() {
        return this.visite;
    }
    /** ottiene tutti i valori di un parametro numerico
     * @return Lista di Double
     */
    public List<Double> getValoriParametroNumeric(Parametro parametro) {
        List<Double> values = new ArrayList<>();
        for (Visita visita : visite) {
            if(visita.getValoreParametro(parametro) != null)
            {
                Double valore = (double) visita.getValoreParametro(parametro);
                values.add(valore);
            }
        }
        return values;
    }
    /** ottiene tutti i valori di un parametro Scalare
     * @return Lista di String
     */
    public List<String> getValoriParametroScalar(Parametro parametro) {
        List<String> values = new ArrayList<>();
        for (Visita visita : visite) {
            String valore = (String) visita.getValoreParametro(parametro);
            if (valore != null) values.add(valore);
        }
        return values;
    }
    /**
     * Restituisce l'insieme di ID univoci.
     *
     * @return un insieme contenente gli ID univoci dei record
     */
    public static Set<String> getIds() {
        return ids;
    }
    /**
     * Verifica se una data è valida trasformandolo in numero e ritrasformandolo in LocalDate
     * se esegue le istruzioni senza problemi è valida, se generano un eccezione non è valida
     * @param date la data da verificare
     * @return true se la data è valida, altrimenti false
     */
    public static boolean DataValida(LocalDate date){
        try {
            LocalDate.parse(date.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /** Per ottenere tutti i valori di un parametro, ordinati in ordine crescente o decrescente
     * @return Lista di Double ordinata
     */
    public List<Double> getValoriNumericOrdinati(Parametro parametro, boolean inversa){
        List<Double> val = getValoriParametroNumeric(parametro);
        Collections.sort(val);
        if(inversa) Collections.reverse(val);
        return val;
    }
    /**
     * @return Lista delle visite ordinate per data
     */
    public List<Visita> creaListaOrdinataPerData() {
        List<Visita> listaOrdinata = new ArrayList<>(visite);
        listaOrdinata.sort(Comparator.comparing(Visita::getDataVisita));
        return listaOrdinata;
    }
    //Gli override di Object@Override
    /**
     * Restituisce una rappresentazione testuale del record.
     *
     * @return una stringa con l'ID, l'anno di nascita e la data di iscrizione del paziente
     */
    @Override
    public String toString(){
        return "Id: "+getId()+"\n  anno nascita: "+getNascita()+"\n  data iscrizione: "+getIscrizione();
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}