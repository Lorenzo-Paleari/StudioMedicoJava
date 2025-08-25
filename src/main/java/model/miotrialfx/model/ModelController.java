package model.miotrialfx.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ModelController {

    private static PropertyChangeSupport support;

    public static List<Record> listaPazienti = new ArrayList<>();
    public static int annoCorrente = LocalDate.now().getYear();
    /**
     * Costruttore del controller del modello.
     * Inizializza il supporto per il cambio di proprietà e legge i dati da file.
     */
    public ModelController(){
        this.support = new PropertyChangeSupport(this);
        readFile();
    }

    /**
     * Esporta i pazienti e le visite su un file di testo.
     */
    public static void SaveFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Pazienti&Visite.txt"))) {
            for (Record paziente : listaPazienti) {
                writer.write(paziente.getId() + "," + paziente.getNascita() + "," + paziente.getIscrizione());
                writer.newLine();
                for (Visita visita : paziente.getVisite()) {
                    writer.write(visita.getDataVisita().toString());
                    for (Parametro parametro : visita.getValori().keySet()) {
                        writer.write("," + parametro.name() + ":" + visita.getValoreParametro(parametro));
                    }
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Errore salvataggio");
        }
    }

    /**
     * Legge i dati da un file di testo e importa visite e pazienti.
     */
    public static void readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Pazienti&Visite.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    String id = parts[0];
                    int nascita = Integer.parseInt(parts[1]);
                    LocalDate iscrizione = LocalDate.parse(parts[2]);

                    Record record = new Record(id, nascita, iscrizione);
                    listaPazienti.add(record);

                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        String[] visitaParts = line.split(",");
                        LocalDate dataVisita = LocalDate.parse(visitaParts[0]);
                        Visita visita = new Visita(dataVisita);
                        for (int i = 1; i < visitaParts.length; i++) {
                            String[] valoreParametro = visitaParts[i].split(":");
                            Parametro parametro = Parametro.valueOf(valoreParametro[0]);
                            Object valore = valoreParametro[1];
                            if (parametro.getType() == Parametro.Type.NUMERIC) {valore = Double.parseDouble((String) valore);}
                            visita.addValoreParametro(parametro, valore);
                        }
                        record.addVisit(visita);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore o file non trovato");
        }
    }

    /**
     * Restituisce una stringa con tutti gli ID dei pazienti presenti.
     *
     * @return una stringa contenente gli ID dei pazienti
     */
    public static String getIds(){
        StringBuilder pazienti = new StringBuilder();
        for(Record paziente : listaPazienti){
            pazienti.append("ID:").append(paziente.getId()).append("\n");
        }
        return pazienti.toString();
    }

    /**
     * Inserisce un nuovo paziente nel sistema.
     *
     * @param idInput l'ID del paziente
     * @param nascitaInput l'anno di nascita del paziente
     * @param inputDataVisita la data della visita iniziale del paziente
     */
    public static void inserisci(String idInput,int nascitaInput,LocalDate inputDataVisita) {
        Record record = new Record(idInput,nascitaInput,inputDataVisita);
        List<Record> OldLista = new ArrayList<>(listaPazienti); //Property
        listaPazienti.add(record);
        support.firePropertyChange("listapazienti", OldLista, listaPazienti); //Property
    }

    /**
     * Restituisce un paziente cercando il suo ID.
     *
     * @param idcercato l'ID del paziente da cercare
     * @return il paziente corrispondente all'ID specificato, null se non trovato
     */
    public static Record ricercaid(String idcercato) {
        for (Record record : listaPazienti){
            if (record.getId().equals(idcercato)){
                return record;
            }
        }
        return null;
    }

    /**
     * Restituisce in una stringa i pazienti nel range di età specificato.
     *
     * @param min l'età minima
     * @param max l'età massima
     * @return una stringa contenente i pazienti nel range di età specificato
     */
    public static String ricercaEta(int min, int max){
        StringBuilder str= new StringBuilder();
        for (Record paziente : listaPazienti){
            int eta = (annoCorrente - paziente.getNascita());
            if (eta >= min && eta <= max) {
                str.append("\n").append(paziente);
            }
        }
        return str.toString();
    }

    /**
     * Aggiunge una visita a un paziente.
     *
     * @param id l'ID del paziente
     * @param data la data della visita
     * @param temp la temperatura registrata durante la visita
     * @param peso il peso registrato durante la visita
     * @param pressione la pressione registrata durante la visita
     * @param capacita la capacità polmonare registrata durante la visita
     * @return un messaggio di conferma o di errore
     */
    public static String aggiungiVisita(String id,LocalDate data, Double temp, Double peso, String pressione, String capacita) {
        Record record = ricercaid(id);
        if (record != null) {
            Visita v = new Visita(data);

            if(temp!=0){v.addValoreParametro(Parametro.TEMPERATURA,temp);}
            if(peso!=0){v.addValoreParametro(Parametro.PESO,peso);}
            if(!Objects.equals(pressione, "null")){v.addValoreParametro(Parametro.PRESSIONE,pressione);}
            if(!Objects.equals(capacita, "null")){v.addValoreParametro(Parametro.CAPACITA_POLMONARE,capacita);}

            record.addVisit(v);
            return "visita inserita";
        } else {
            return "paziente non trovato";
        }
    }

    /**
     * Mostra le visite di un paziente.
     *
     * @param id l'ID del paziente
     * @return una stringa contenente le visite del paziente o un messaggio se non sono presenti visite
     */
    public static String visitePaziente(String id) {
        StringBuilder str = new StringBuilder();
        int i=1;
        Record paziente = ricercaid(id);
        if(paziente != null && !paziente.getVisite().isEmpty()) //è vuota ?
        {
            for (Visita visita : paziente.getVisite()) { //per ogni visita
                str.append(i).append(") Data visita: ").append(visita.getDataVisita());
                i++;
                for (Parametro parametro : visita.getValori().keySet()) //per ogni parametro della visita
                    str.append("   ").append(parametro).append(": ").append(visita.getValoreParametro(parametro));
                str.append("\n");
            }
        } else return "Non sono ancora state effettuate visite";

        return str.toString();
    }

    /**
     * Visualizza le statistiche di un parametro per un paziente.
     *
     * @param id l'ID del paziente
     * @param parametro il parametro per il quale si vogliono visualizzare le statistiche
     * @return una stringa contenente le statistiche del parametro per il paziente
     */
    public static String statistichePaziente(String id, Parametro parametro){
        Record paziente = ricercaid(id);

        if(parametro.getType() == Parametro.Type.NUMERIC){return statisticheNumber(parametro,paziente);}
        else if(parametro.getType() == Parametro.Type.SCALAR){return statisticheScalar(parametro,paziente);}
        else return "parametro inesistente";
    }

    /**
     * Calcola e restituisce le statistiche numeriche per un dato parametro e un dato paziente o per tutti i pazienti.
     *
     * @param par il parametro per il quale si vogliono calcolare le statistiche
     * @param paz il paziente per il quale si vogliono calcolare le statistiche o null per tutti i pazienti
     * @return una stringa contenente le statistiche numeriche
     */
    public static String statisticheNumber(Parametro par, Record paz) {
        List<Double> valori = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        if(paz!=null){ //un paziente
            valori.addAll(paz.getValoriParametroNumeric(par));
        }else{ //tutti i pazienti
            for (Record record : listaPazienti) {
                valori.addAll(record.getValoriParametroNumeric(par));
            }
        }
        if(!valori.isEmpty()) { //mi dava errore se non ne trovava
            Collections.sort(valori);
            double somma = 0;
            str.append("Valori in ordine crescente:\n");
            str.append("[");
            for (Double temp : valori) {
                str.append(temp).append(" ");
                somma += temp;
            }
            str.append("]\n");

            printValoriDecr(valori,str);

            double media = somma / valori.size();
            String formattedMedia = String.format("%.3f", media);
            str.append("Media: ").append(formattedMedia).append("\n");
            str.append("Minimo: ").append(valori.getFirst()).append("\n");
            str.append("Massimo: ").append(valori.getLast()).append("\n\n");

            if(paz!=null){ //ordina per date del singolo paziente
                stampaOrdineData(str,par,paz);
            }
        }else{return "Nessun valore trovato";}
        return str.toString();
    }

    /**
     * Aggiunge alla stringa le visite di un paziente in ordine di data.
     *
     * @param str il StringBuilder su cui aggiungere le informazioni sulle visite
     * @param par il parametro per il quale si vogliono stampare le visite
     * @param paz il paziente di cui si vogliono stampare le visite
     */
    public static void stampaOrdineData(StringBuilder str, Parametro par, Record paz){
        str.append("In ordine di data visita:\n");
        for (Visita visita : paz.creaListaOrdinataPerData()) {
            LocalDate dataVisita = visita.getDataVisita();
            Object valoreParametro = visita.getValoreParametro(par);
            str.append("[").append(dataVisita).append(": ").append(valoreParametro).append("]\n");
        }
    }

    /**
     * Calcola e restituisce le statistiche scalari per un dato parametro e un dato paziente o per tutti i pazienti.
     *
     * @param par il parametro per il quale si vogliono calcolare le statistiche
     * @param paz il paziente per il quale si vogliono calcolare le statistiche o null per tutti i pazienti
     * @return una stringa contenente le statistiche scalari
     */
    public static String statisticheScalar(Parametro par, Record paz) {
        StringBuilder str = new StringBuilder();
        List<String> valori = new ArrayList<>();
        if(paz!=null){ //un paziente
            valori.addAll(paz.getValoriParametroScalar(par));
        }else { //tutti i pazienti
            for (Record record : listaPazienti) {
                valori.addAll(record.getValoriParametroScalar(par));
            }
        }
        if(!valori.isEmpty()) { //mi dava errore se non ne trovava
            sortScalar(valori);

            str.append("Minimo: ").append(valori.getFirst()).append("\n");
            str.append("Massimo: ").append(valori.getLast()).append("\n\n");

            str.append("Valori in ordine crescente:\n");
            str.append("[");
            for (String temp : valori) {
                str.append(temp).append(" ");
            }str.append("]\n\n");

            printValoriDecr(valori,str);

            if(paz!=null){ //ordina per date del singolo paziente
                stampaOrdineData(str,par,paz);
            }
        }else{return "Nessun valore trovato";}
        return str.toString();
    }

    /**
     * Stampa i valori in ordine decrescente.
     *
     * @param valori la lista dei valori da stampare
     * @param str il StringBuilder su cui aggiungere i valori
     * @param <T> il tipo di valori nella lista
     */
    public static <T> void printValoriDecr(List<T> valori,StringBuilder str){
        Collections.reverse(valori);
        str.append("Valori in ordine decrescente:\n");
        str.append("[");
        for (T val : valori) {
            str.append(val).append(" ");
        }
        str.append("]\n\n");
        Collections.reverse(valori);
    }

    /**
     * Ordina una lista di valori scalari in base a un valore numerico assegnato a ciascun valore scalare.
     *
     * @param list la lista di valori scalari da ordinare
     */
    public static void sortScalar(List<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                String s1 = list.get(j);
                String s2 = list.get(j + 1);
                int s1Value = assegnaValore(s1);
                int s2Value = assegnaValore(s2);
                if (s1Value > s2Value) {//swap
                    list.set(j, s2);
                    list.set(j + 1, s1);
                }
            }
        }
    }

    /**
     * Assegna un valore numerico a una stringa scalare.
     *
     * @param str la stringa scalare a cui assegnare il valore numerico
     * @return il valore numerico assegnato alla stringa scalare
     */
    public static int assegnaValore(String str) {
        switch (str) {
            case "bassa":
                return 0;
            case "media":
                return 1;
            case "alta":
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Elimina una visita di un paziente.
     *
     * @param id l'ID del paziente
     * @param numeroVisita il numero della visita da eliminare
     * @return un messaggio di conferma o errore
     */
    public static String eliminaVisita(String id,int numeroVisita) {
        Record record = ricercaid(id);
        if (record != null) {
            List<Visita> visitePaziente = record.getVisite();
            if (!visitePaziente.isEmpty()) {
                if (numeroVisita > 0 && numeroVisita <= visitePaziente.size()) {
                    Visita visitaDaEliminare = visitePaziente.get(numeroVisita - 1);
                    visitePaziente.remove(visitaDaEliminare);
                    return "Visita eliminata.\n\n";
                } else {
                    return "Numero di visita non valido.\n";
                }
            } else {
                return "Il paziente non ha visite registrate.\n";
            }
        } else {
             return "ID del paziente non trovato.\n";
        }
    }

    /**
     * Cancella un paziente
     *
     * @param id l'ID del paziente da cancellare
     * @return true se il paziente è stato cancellato con successo, false altrimenti
     */
    public static boolean cancellaPaziente(String id) {
        Record paziente = ricercaid(id);
        if (paziente != null) {
            listaPazienti.remove(paziente);
            Record.ids.remove(id);
            return true;
        }
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener("listapazienti", listener);
    }

}