package model;
import model.miotrialfx.model.*;
import model.miotrialfx.model.Record;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelControllerTest {

    @BeforeEach
    void init() {
        ModelController m = new ModelController();
    }

    @Test
    void testInserisci() {
        ModelController.inserisci("123", 1980, LocalDate.now());
        Record paziente = ModelController.ricercaid("123");
        assertNotNull(paziente);
    }

    @Test
    void testRicercaid() {
        Record paziente = new Record("456", 1990, LocalDate.now());
        ModelController.listaPazienti.add(paziente);
        Record pazienteTrovato = ModelController.ricercaid("456");
        assertNotNull(pazienteTrovato);
        assertEquals("456", pazienteTrovato.getId());
    }

    @Test
    void testRicercaEta() {
        Record paziente1 = new Record("789", 1985, LocalDate.now());
        Record paziente2 = new Record("101", 1970, LocalDate.now());
        ModelController.listaPazienti.add(paziente1);
        ModelController.listaPazienti.add(paziente2);
        String pazientiInRange = ModelController.ricercaEta(30, 50);
        assertTrue(pazientiInRange.contains("789"));
        assertFalse(pazientiInRange.contains("101"));
    }

    @Test
    void testAggiungiVisita() {
        ModelController.inserisci("527", 1980, LocalDate.now());
        String result = ModelController.aggiungiVisita("527", LocalDate.now(), 37.5, 70.5, "120/80", "normale");
        assertEquals("visita inserita", result);
    }

    @Test
    void testVisitePaziente() {
        ModelController.inserisci("777", 1980, LocalDate.now());
        ModelController.aggiungiVisita("777", LocalDate.now(), 37.5, 70.5, "media", "media");
        String visite = ModelController.visitePaziente("777");

        assertNotNull(visite);
        assertTrue(visite.contains("Data visita"));
    }

    @Test
    void testStatistichePaziente() {
        ModelController.inserisci("666", 1980, LocalDate.now());
        ModelController.aggiungiVisita("666", LocalDate.now(), 37.5, 70.5, "media", "media");
        String statistiche = ModelController.statistichePaziente("666", Parametro.TEMPERATURA);

        assertNotNull(statistiche);
        assertTrue(statistiche.contains("Valori in ordine crescente"));
    }


    @Test
    void testEliminaVisita() {
        ModelController.inserisci("222", 1980, LocalDate.now());
        ModelController.aggiungiVisita("222", LocalDate.now(), 37.5, 70.5, "media", "media");
        String result = ModelController.eliminaVisita("222", 1);
        assertEquals("Visita eliminata.\n\n", result);
    }
}