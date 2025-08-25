package model.miotrialfx.model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class MioTrial {
    static Scanner tastiera = new Scanner(System.in);
    static LocalDate iscrizione;
    public static List<Record> listaPazienti = new ArrayList<>();
    static int annoCorrente = LocalDate.now().getYear();

    //visualizzare il menu delle opzioni
    public static void menu() {
        System.out.print("""
                Cosa vuoi fare?
                1) Inserisci
                2) Ricerca
                3) Ricerca per età
                4) Aggiungi visita
                5) Statistiche un paziente
                6) Visite di un paziente
                7) Esporto pazienti e visite
                //extra
                8) Mostra tutti i pazienti
                9) Cancello visita
                10) Statistiche Totali
                11) Esci
                Digita qui: \s""");
    }
    //eseguire l'azione selezionata dal menu
    public static void esegui(int scelta) {
        System.out.println("\nHai scelto: "+scelta);
        switch (scelta) {
            case 1:
                inserisci();
                break;
            case 2:
                ricerca();
                break;
            case 3:
                ricercaEta();
                break;
            case 4:
                aggiungiVisita();
                break;
            case 5:
                statistichePaziente();
                break;
            case 6:
                visitePaziente();
                break;
            case 7:
                SaveFile();
                break;
            case 8:
                mostraPazienti();
                break;
            case 9:
                eliminaVisita();
                break;
            case 10:
                statisticheSelezionaParametro(null);
                break;
            default:
                System.out.println("Arrivederci");
                System.exit(0);
        }
    }
    //visualizzare le visite di un paziente
    public static void visitePaziente() {
        System.out.println("\nInserisci l'ID del paziente: ");
        String idRicerca = tastiera.nextLine();
        if(Record.ids.contains(idRicerca)) //è presente nella lista di tutti gli id ?
        {
            Record paziente = ricercaid(idRicerca);
            if(paziente != null && !paziente.getVisite().isEmpty()) //è vuota ?
            {
                for (Visita visita : paziente.getVisite()) { //per ogni visita
                    System.out.println("Data visita: " + visita.getDataVisita());
                    for (Parametro parametro : visita.getValori().keySet()) //per ogni parametro della visita
                        System.out.println("   "+parametro + ": " + visita.getValoreParametro(parametro));
                    System.out.println();
                }
            } else System.out.println("Non sono ancora state effettuate visite");
        }else{System.out.println("ID non trovato");}

    }
    //visualizzare le statistiche di un paziente
    public static void statistichePaziente(){
        System.out.println("\nInserisci l'ID del paziente: ");
        String idRicerca = tastiera.nextLine();
        if(Record.ids.contains(idRicerca))
        {
            Record paziente = ricercaid(idRicerca); //trova paziente dall'id
            statisticheSelezionaParametro(paziente);
        }else{System.out.println("ID non trovato");}
    }
    //selezione di quale parametro cercare
    public static void statisticheSelezionaParametro(Record paz) {
        System.out.println("per quale di questi parametri vorresti le statistiche?");
        for ( Parametro parametro : Parametro.values() ) System.out.println(parametro);

        String inputParametro = tastiera.nextLine();

        Parametro parametroSelezionato = null;
        for (Parametro parametro : Parametro.values()) {
            if (parametro.name().equalsIgnoreCase(inputParametro)) {
                parametroSelezionato = parametro;
                break;
            }
        }
        if (parametroSelezionato != null) {
            System.out.println("Hai selezionato il parametro: " + parametroSelezionato);
            if(parametroSelezionato.getType() == Parametro.Type.NUMERIC){statisticheNumber(parametroSelezionato,paz);}
            else if(parametroSelezionato.getType() == Parametro.Type.SCALAR){statisticheScalar(parametroSelezionato,paz);}
        } else {
            System.out.println("Parametro non valido.");
        }

    }
    //visualizzare le statistiche numeriche
    public static void statisticheNumber(Parametro par, Record paz) {
        List<Double> valori = new ArrayList<>();
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
            System.out.println("Valori in ordine crescente:");
            System.out.print("[");
            for (Double temp : valori) {
                System.out.print(temp+" ");
                somma += temp;
            }System.out.println("]");

            printValoriDecr(valori);

            double media = somma / valori.size();
            System.out.println("Media: " + media);
            System.out.println("Minimo: " + valori.getFirst());
            System.out.println("Massimo: " + valori.getLast());

            if(paz!=null){ //ordina per date del singolo paziente
                stampaOrdineData(par,paz);
            }
        }else{System.out.println("Nessun valore trovato");}
    }
    //stampare in ordine di data
    public static void stampaOrdineData(Parametro par, Record paz){
        System.out.println("In ordine di data visita:");
        for (Visita visita : paz.creaListaOrdinataPerData()) {
            LocalDate dataVisita = visita.getDataVisita();
            Object valoreParametro = visita.getValoreParametro(par);
            System.out.println("[" + dataVisita + ": " + valoreParametro + "]");
        }
    }
    //visualizzare le statistiche scalari
    public static void statisticheScalar(Parametro par, Record paz) {
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

            System.out.println("Minimo: " + valori.getFirst());
            System.out.println("Massimo: " + valori.getLast());

            System.out.println("Valori in ordine crescente:");
            System.out.print("[");
            for (String temp : valori) {
                System.out.print(temp+" ");
            }System.out.println("]");

            printValoriDecr(valori);

            if(paz!=null){ //ordina per date del singolo paziente
                stampaOrdineData(par,paz);
            }
        }else{System.out.println("Nessun valore trovato");}
    }
    //metodo generico per stampa inversa
    public static <T> void printValoriDecr(List<T> valori){
        Collections.reverse(valori);
        System.out.println("Valori in ordine decrescente:");
        System.out.print("[");
        for (T val : valori) {
            System.out.print(val+" ");
        }
        System.out.println("]");
        Collections.reverse(valori);
    }
    //inserimento nuovo paziente
    public static void inserisci() {
        String idInput = null;
        int nascitaInput;

        do {
            try {
                System.out.println("\nInserisci l'ID: ");
                idInput = tastiera.nextLine();
                if ((idInput == null) || (idInput.isEmpty())) {
                    System.out.println("Dato errato, reinserire perfavore");
                }
                if (Record.getIds().contains(idInput)) System.out.println("ID già presente, reinserire perfavore");
            }catch (Exception e){System.out.println("Errore Input");}
        }while((idInput == null) || idInput.isEmpty() || Record.getIds().contains(idInput));

        do {
            nascitaInput  = getInputInt("\nInserisci l'anno di nascita: ");
            if (nascitaInput <= 0 || nascitaInput > annoCorrente) {
                System.out.println("Dato errato, reinserire perfavore");
            }
        }while(nascitaInput <= 0 || nascitaInput > annoCorrente );

        System.out.println("\ninserisci la data di registrazione:");

        LocalDate inputDataVisita = chiediData();

        Record record = new Record(idInput,nascitaInput,inputDataVisita);
        listaPazienti.add(record);
    }
    //ricerca paziente
    public static void ricerca() {
        System.out.println("\nInserisci l'ID del paziente: ");
        String idRicerca = tastiera.nextLine();

        Record paziente = ricercaid(idRicerca);
        if (paziente != null) {
            System.out.println("ID: " + paziente.getId());
            System.out.println("Età: " + (LocalDate.now().getYear() - paziente.getNascita()));
            System.out.println("Registrato il: " + paziente.getIscrizione());
        } else {
            System.out.println("ID non trovato");
        }
    }
    //restituisce un paziente cercando il suo id
    public static Record ricercaid(String idcercato) {
        for (Record record : listaPazienti){
            if (record.getId().equals(idcercato)){
                return record;
            }
        }
        return null;
    }
    //stampa i pazienti compresi in un range di età
    public static void ricercaEta(){
        int etamin = getInputInt("\nEta' minima: ");
        int etamax = getInputInt("\nEta' massima: ");

        for (Record paziente : listaPazienti){
            int eta = (annoCorrente - paziente.getNascita());
            if (eta >= etamin && eta <= etamax) {
                System.out.println("ID: " + paziente.getId());
                System.out.println("Età: " + (LocalDate.now().getYear() - paziente.getNascita()));
                System.out.println("Registrato il: " + paziente.getIscrizione());
            }
        }
    }
    //aggiungi una visita a un paziente
    public static void aggiungiVisita() {
        System.out.println("\nInserisci l'ID del paziente: ");
        String idRicerca = tastiera.nextLine();

        Record record = ricercaid(idRicerca);
        if (record != null) {
            LocalDate inputDataVisita = chiediData();
            Visita v = new Visita(inputDataVisita);
            for ( Parametro parametro : Parametro.values() )
            {
                System.out.println("vuoi inserire un valore per: "+ parametro +"? (SI/NO)");
                if (tastiera.nextLine().equalsIgnoreCase("SI"))
                {
                    if(parametro.tipo == Parametro.Type.NUMERIC){v.addValoreParametro(parametro,chiediNumeric());}
                    if(parametro.tipo == Parametro.Type.SCALAR){v.addValoreParametro(parametro,chiediScalare());}
                }
            }
            record.addVisit(v);
        } else {
            System.out.println("ID non trovato");
        }
    }
    //input double
    public static double chiediNumeric() {
        double input = -1;
        do {
            try {
                System.out.print("Inserisci dato: ");
                input = tastiera.nextDouble();
                if (input < 0) System.out.println("dato non valido");
                tastiera.nextLine();
            } catch (Exception e) {
                System.out.println("Input non valido! Assicurati di inserire un numero.");
                tastiera.nextLine();
            }
        } while (input < 0);
        return input;
    }
    //input scalare
    public static String chiediScalare() {
        String inputPressione;
        do {
            System.out.print("Inserisci (bassa/media/alta): ");
            inputPressione = tastiera.nextLine();
            if (!inputPressione.equals("bassa") && !inputPressione.equals("media") && !inputPressione.equals("alta")){
                System.out.println("pressione non valida");
            }
        } while (!inputPressione.equals("bassa") && !inputPressione.equals("media") && !inputPressione.equals("alta"));
        return inputPressione;
    }
    //ordina gli scalari
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
    //assegna alle String un valore numerico
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
    //input data
    public static LocalDate chiediData(){
        int giornoInput;
        int meseInput;
        int annoInput;

        do {
            giornoInput = getInputInt("giorno:");
            if ((giornoInput <= 0) || (giornoInput > 31)) {
                System.out.println("Dato errato, reinserire perfavore");
            }
        }while((giornoInput <= 0) || (giornoInput > 31));
        do {
            meseInput = getInputInt("mese: ");
            if ((meseInput <= 0) || (meseInput > 12)) {
                System.out.println("Dato errato, reinserire perfavore");
            }
        }while((meseInput <= 0) || (meseInput > 12));
        do {
            annoInput = getInputInt("anno: ");
            if (annoInput <= 0 || annoInput > annoCorrente) {
                System.out.println("Dato errato, reinserire perfavore");
            }
        }while(annoInput <= 0 || annoInput > annoCorrente);

        iscrizione = LocalDate.of(annoInput,meseInput,giornoInput);
        return iscrizione;
    }
    //input int
    public static int getInputInt(String prompt) {
        int input = 0;
        do {
            try {
                System.out.print(prompt);
                input = tastiera.nextInt();
                tastiera.nextLine();
                if(input <= 0)System.out.println("Input non valido");
            } catch (InputMismatchException e) {
                System.out.println("Input non valido, assicurati di inserire un numero.");
                tastiera.nextLine();
            }
        } while (input <= 0);
        return input;
    }
    //esporta pazienti e visite su file
    public static void SaveFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\palea\\IdeaProjects\\MioTrialFX\\src\\main\\java\\upo20051826\\trials\\miotrialfx\\model\\Pazienti&Visite.txt"))) {
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
    //leggi file, importa visite e pazienti
    public static void readFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\palea\\IdeaProjects\\MioTrialFX\\src\\main\\java\\upo20051826\\trials\\miotrialfx\\model\\Pazienti&Visite.txt"))) {
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
    //mostra tutti gli ID presenti
    public static void mostraPazienti(){
        for(Record paziente : listaPazienti){
            System.out.println("ID: " + paziente.getId());
        }
    }
    //elimina una visita di un paziente
    public static void eliminaVisita() {
        System.out.println("\nInserisci l'ID del paziente: ");
        String idRicerca = tastiera.nextLine();

        Record record = ricercaid(idRicerca);
        if (record != null) {
            List<Visita> visitePaziente = record.getVisite();
            if (!visitePaziente.isEmpty()) {
                System.out.println("Elenco delle visite del paziente:");
                for (int i = 0; i < visitePaziente.size(); i++) {
                    System.out.println((i+1) + ") Data visita: " + visitePaziente.get(i).getDataVisita());
                }
                System.out.println("Seleziona il numero della visita da eliminare:");
                int numeroVisita = getInputInt("");
                if (numeroVisita > 0 && numeroVisita <= visitePaziente.size()) {
                    Visita visitaDaEliminare = visitePaziente.get(numeroVisita - 1);
                    visitePaziente.remove(visitaDaEliminare);
                    System.out.println("Visita eliminata con successo.");
                } else {
                    System.out.println("Numero di visita non valido.");
                }
            } else {
                System.out.println("Il paziente non ha visite registrate.");
            }
        } else {
            System.out.println("ID del paziente non trovato.");
        }
    }
    public static void main(String[] args) {
        System.out.println("MioTrial avviato");
        readFile();
        int scelta = -1;

        while (scelta != 100) {
            menu();
            try {
                scelta = tastiera.nextInt();
                tastiera.nextLine();
            }catch (Exception e){
                System.out.println("\nErrore Input");
                return;
            }
            esegui(scelta);
        }
    }
}