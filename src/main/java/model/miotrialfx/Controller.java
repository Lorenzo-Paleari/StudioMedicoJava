package model.miotrialfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.miotrialfx.model.ModelController;
import model.miotrialfx.model.Parametro;
import model.miotrialfx.model.Record;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

import static model.miotrialfx.model.ModelController.annoCorrente;

public class Controller implements Initializable, PropertyChangeListener {

    @FXML
    private TextField TextVisitePaziente;

    @FXML
    private TextArea outputVisitePaziente;

    @FXML
    private TextField TextAnnoNascita;

    @FXML
    private TextField TextId;

    @FXML
    private DatePicker DataIscrizioneSelection;

    @FXML
    private TextArea TextAreaInserisci;

    @FXML
    private TextArea ListaPazienti;

    @FXML
    private ComboBox<String> BoxCapacita;

    @FXML
    private ComboBox<Parametro> BoxParametroStatistiche;

    @FXML
    private ComboBox<String> BoxPressione;

    @FXML
    private TextField TextVisitaDaCancellare;

    @FXML
    private TextArea TextAreaRicerca;

    @FXML
    private TextArea TextAreaStatistiche;

    @FXML
    private TextField TextIdAggiungiVisita;

    @FXML
    private TextField TextIdStatistiche;

    @FXML
    private TextField TextPeso;

    @FXML
    private TextArea TextAreaAggiungiVisita;

    @FXML
    private DatePicker DataAggiungiVisita;

    @FXML
    private TextField TextRicercaEtaMax;

    @FXML
    private TextField TextRicercaID;

    @FXML
    private TextField TextRicertaEtaMin;

    @FXML
    private TextField TextTemperatura;

    private final ObservableList<String> scalVal = FXCollections.observableArrayList("bassa", "media", "alta","null");
    private final ObservableList<Parametro> parSel = FXCollections.observableArrayList(Parametro.TEMPERATURA,Parametro.PESO,Parametro.PRESSIONE,Parametro.CAPACITA_POLMONARE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ModelController c = new ModelController();
        c.addPropertyChangeListener(this); //Property
        UpdateMostraPazienti();

        BoxPressione.setItems(scalVal);
        BoxPressione.getSelectionModel().selectLast();
        BoxCapacita.setItems(scalVal);
        BoxCapacita.getSelectionModel().selectLast();
        BoxParametroStatistiche.setItems(parSel);
        BoxParametroStatistiche.getSelectionModel().selectFirst();
    }

    @FXML
    void ReadFileAction() {
        ModelController.readFile();
    }

    @FXML
    void SaveFileAction() {
        ModelController.SaveFile();
    }

    @FXML
    void InserisciPaziente() {
        String ID = TextId.getText();
        try {
            int anno = Integer.parseInt(TextAnnoNascita.getText());
            LocalDate data = DataIscrizioneSelection.getValue();

            if (ID.isEmpty() || Record.getIds().contains(ID) || anno <= 0 || anno > annoCorrente || data==null) {
                if (Record.getIds().contains(ID))
                    TextAreaInserisci.setText("ID già presente");
                else
                    TextAreaInserisci.setText("Dati errati, inserire valori validi");
            } else {
                ModelController.inserisci(ID, anno, data);
                TextAreaInserisci.setText("Paziente inserito");
            }
        } catch (NumberFormatException e) {
            TextAreaInserisci.setText("Errore: inserire un valore numerico nell'anno di nascita");
        }
        //UpdateMostraPazienti(); //sostituito dal PropertyChangeEvent
    }

    @FXML
    void VisitePazienteAction() {
        String id = TextVisitePaziente.getText();
        if(Record.ids.contains(id))
            outputVisitePaziente.setText(ModelController.visitePaziente(id));
        else outputVisitePaziente.setText("Id non presente nella lista");
    }

    @FXML
    void AggiungiVisita() {
        try {
            String id = TextIdAggiungiVisita.getText();
            LocalDate data = DataAggiungiVisita.getValue();

            String testoTemperatura = TextTemperatura.getText();
            String testoPeso = TextPeso.getText();

            double temperatura;
            if (testoTemperatura.isEmpty()) {
                temperatura = 0.0;
            } else {
                temperatura = Double.parseDouble(testoTemperatura);
            }

            double peso;
            if (testoPeso.isEmpty()) {
                peso = 0.0;
            } else {
                peso = Double.parseDouble(testoPeso);
            }

            String pressione = BoxPressione.getValue();
            String capacitaPolmonare = BoxCapacita.getValue();
            if(data!=null)
                TextAreaAggiungiVisita.setText(ModelController.aggiungiVisita(id, data, temperatura, peso, pressione, capacitaPolmonare));
            else TextAreaAggiungiVisita.setText("inserire una data perfavore");
        }catch (NumberFormatException e){TextAreaAggiungiVisita.setText("inserire dati validi perfavore");}
    }

    @FXML
    void CercaID() {
        Record paz = ModelController.ricercaid(TextRicercaID.getText());
        if(paz==null) TextAreaRicerca.setText("Utente non trovato");
        else{
            TextAreaRicerca.setText(paz.toString());
        }
    }


    @FXML
    void EliminaPaziente() {
        if(ModelController.cancellaPaziente(TextRicercaID.getText())){
            TextAreaRicerca.setText("paziente eliminato");
            UpdateMostraPazienti();
        }
    }

    @FXML
    void RicercaPerEta() {
        try {
            int min = Integer.parseInt(TextRicertaEtaMin.getText());
            int max = Integer.parseInt(TextRicercaEtaMax.getText());

            TextAreaRicerca.setText(ModelController.ricercaEta(min,max));

        } catch (NumberFormatException e) {
            TextAreaRicerca.setText("Errore: Inserire un numero valido.");
        }
    }

    @FXML
    void Statistiche() {
        String id = TextIdStatistiche.getText();
        Parametro par = BoxParametroStatistiche.getValue();
        if(Record.ids.contains(id)) {
            TextAreaStatistiche.setText(ModelController.statistichePaziente(id, par));
        }else if(Objects.equals(id, "")) TextAreaStatistiche.setText(ModelController.statistichePaziente(null, par));
        else TextAreaStatistiche.setText("ID non presente");
    }

    @FXML
    void CancellaVisita() {
        try {
            String id = TextVisitePaziente.getText();
            int numVisita = Integer.parseInt(TextVisitaDaCancellare.getText());
            if (Record.ids.contains(id))
            {
                outputVisitePaziente.setText(ModelController.eliminaVisita(id,numVisita) + ModelController.visitePaziente(id));
            }
            else outputVisitePaziente.setText("Id non presente nella lista");
        } catch (NumberFormatException e) {outputVisitePaziente.setText("Errore: inserire un valore numerico");}
    }

    @FXML
    void UpdateMostraPazienti() {
        ListaPazienti.setText(ModelController.getIds());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("listapazienti")) {
            UpdateMostraPazienti();
        }
    }


}
