package model;

import model.miotrialfx.model.Parametro;
import model.miotrialfx.model.Visita;
import model.miotrialfx.model.Record;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {

    Record r;
    Visita v;


    @Test
    void testGet() {
        Record rInfo = new Record("0000", 2002, LocalDate.now());

        assertEquals(rInfo.getId(), "0000");
        assertEquals(rInfo.getNascita(), 2002);
        assertEquals(rInfo.getIscrizione(), LocalDate.now());
        r = new Record(2002, LocalDate.now());
        assertNotNull(r.getId());
    }

    @Test
    void testGetVisita(){
        r = new Record(2002, LocalDate.now());
        v = new Visita(LocalDate.now());
        v.addValoreParametro(Parametro.TEMPERATURA, 36.9);
        v.addValoreParametro(Parametro.PRESSIONE, "media");
        r.addVisit(v);
        assertNotNull(r.getVisite());
        assertEquals(r.getVisite().size(),1);
    }

    @Test
    void creaRecordNotNull(){
        r = new Record(2002, LocalDate.now());
        assertNotNull(r);
    }

    @Test
    void testCostruttoreConIDPresente() {
        LocalDate dataIscrizione = LocalDate.of(2022, 1, 1);
        Record record1 = new Record("002", 1990, dataIscrizione);
        assertThrows(IllegalArgumentException.class, () -> new Record("002", 1985, dataIscrizione));
    }

    @Test
    void testCostruttoreConAnnoNascitaNonValido() {
        LocalDate dataIscrizione = LocalDate.of(2022, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> new Record("003", -5, dataIscrizione));
    }

    @Test
    void testAggiungiVisita() {
        LocalDate dataIscrizione = LocalDate.of(2022, 1, 1);
        Record record = new Record("004", 1980, dataIscrizione);
        Visita visita = new Visita(LocalDate.of(2023, 1, 1));
        visita.addValoreParametro(Parametro.TEMPERATURA, 37.5);
        visita.addValoreParametro(Parametro.PRESSIONE, "alta");
        record.addVisit(visita);
        assertTrue(record.getVisite().contains(visita));
    }

    @Test
    void testGetTemperature() {
        LocalDate dataIscrizione = LocalDate.of(2022, 2, 3);
        Record record = new Record("005", 2002, dataIscrizione);
        Visita visita1 = new Visita(LocalDate.of(2023, 10, 25));
        visita1.addValoreParametro(Parametro.TEMPERATURA, 37.5);
        Visita visita2 = new Visita(LocalDate.of(2023, 10, 25));
        visita2.addValoreParametro(Parametro.TEMPERATURA, 36.9);
        record.addVisit(visita1);
        record.addVisit(visita2);

        List<Double> temperature = record.getValoriParametroNumeric(Parametro.TEMPERATURA);
        assertEquals(2, temperature.size());
        assertTrue(temperature.contains(37.5));
        assertTrue(temperature.contains(36.9));
    }

    @Test
    void testGetPressioni() {
        LocalDate dataIscrizione = LocalDate.of(2022, 2, 3);
        Record record = new Record("006", 2002, dataIscrizione);
        Visita visita1 = new Visita(LocalDate.of(2023, 10, 25));
        visita1.addValoreParametro(Parametro.PRESSIONE, "alta");
        Visita visita2 = new Visita(LocalDate.of(2023, 10, 25));
        visita2.addValoreParametro(Parametro.PRESSIONE, "bassa");
        record.addVisit(visita1);
        record.addVisit(visita2);

        List<String> pressioni = record.getValoriParametroScalar(Parametro.PRESSIONE);
        assertEquals(2, pressioni.size());
        assertTrue(pressioni.contains("alta"));
        assertTrue(pressioni.contains("bassa"));
    }

}
