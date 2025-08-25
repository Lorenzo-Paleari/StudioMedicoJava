package model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import model.miotrialfx.model.Parametro;
import model.miotrialfx.model.Visita;
import static org.junit.jupiter.api.Assertions.*;

public class VisitaTest {

    @Test
    public void testCostruttore() {
        LocalDate data = LocalDate.now();
        Visita visita = new Visita(data);

        assertEquals(data, visita.getDataVisita());
    }

    @Test
    public void testSetDatavisita() {
        LocalDate data = LocalDate.now();
        Visita visita = new Visita(data);

        LocalDate newData = LocalDate.of(2023, 12, 21);
        visita.setDatavisita(newData);

        assertEquals(newData, visita.getDataVisita());
    }

    @Test
    public void testSetTemperatura() {
        LocalDate data = LocalDate.now();
        Visita visita = new Visita(data);
        double newTemp = 37.0;
        visita.addValoreParametro(Parametro.TEMPERATURA,newTemp);

        assertEquals(newTemp, visita.getValoreParametro(Parametro.TEMPERATURA));
    }

    @Test
    public void testSetPressione() {
        LocalDate data = LocalDate.now();
        Visita visita = new Visita(data);
        visita.addValoreParametro(Parametro.PRESSIONE,"media");

        String newPress = "media";
        visita.addValoreParametro(Parametro.PRESSIONE,newPress);

        assertEquals(newPress, visita.getValoreParametro(Parametro.PRESSIONE));
    }

    @Test
    public void testAddValoreParametro_TemperaturaMaggioreDi50() {
        Visita visita = new Visita(LocalDate.now());

        visita.addValoreParametro(Parametro.TEMPERATURA, 55.0);

        assertNull(visita.getValoreParametro(Parametro.TEMPERATURA));
    }
}