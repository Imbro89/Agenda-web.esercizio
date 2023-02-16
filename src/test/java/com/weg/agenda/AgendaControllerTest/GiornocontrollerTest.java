package com.weg.agenda.AgendaControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import static org.mockito.ArgumentMatchers.any;
import com.weg.agenda.AgendaController.Giornocontroller;
import com.weg.agenda.AgendaModels.Appuntamento;
import com.weg.agenda.AgendaModels.Giorno;
import com.weg.agenda.AgendaRepository.AppuntamentoRepository;
import com.weg.agenda.AgendaRepository.GiornoRepository;

@RunWith(MockitoJUnitRunner.class)
public class GiornocontrollerTest {

    @Mock // dipendenze repository
    private GiornoRepository giornoRepository;
    @Mock
    private AppuntamentoRepository appuntamentoRepository;


    @Mock // bean fittizio
    private Model model;

    @InjectMocks // dipendenza della classe controller
    private Giornocontroller giornocontroller;

    @Test
    public void testIndex() {
        // dati
        List<Giorno> giorni = Arrays.asList(new Giorno(), new Giorno());
        List<Appuntamento> appuntamenti = Arrays.asList(new Appuntamento(),new Appuntamento());
        when(appuntamentoRepository.findAll()).thenReturn(appuntamenti); //AppuntamentoRepository risulta null 
        when(giornoRepository.findAll()).thenReturn(giorni);

        // esecuzione dei dati
        String result = giornocontroller.index(model);

        // verifica dei dati
        verify(model).addAttribute("giorni", giorni);
        assertEquals("index", result);
    }

    @Test
    public void testCreaGiorno() {
        // dati
        LocalDate data = LocalDate.now();
        when(giornoRepository.findByData(data)).thenReturn(Optional.empty());

        // esecuzione dei dati
        String result = giornocontroller.creaGiorno(data, model);

        // Verifica dei dati
        verify(giornoRepository).save(any(Giorno.class));
        assertEquals("redirect:/", result);
    }

    @Test
    public void testCreaGiorno_GiornoEsistente() {
        // dati
        LocalDate data = LocalDate.now();
        Giorno giornoEsistente = new Giorno(data);
        when(giornoRepository.findByData(data)).thenReturn(Optional.of(giornoEsistente));

        // Esecuzione dei dati
        String result = giornocontroller.creaGiorno(data, model);

        // Verifica dei dati
        verify(model).addAttribute("error", "Esiste già un giorno con questa data");
        assertEquals("index", result);
    }

    @Test
    public void testEliminaGiorno() {
        // dati
        int id = 1;

        // Esecuzione dei dati
        String result = giornocontroller.eliminaGiorno(id, model);

        // verifica dei dati
        verify(giornoRepository).deleteAppuntamentiByGiornoId(id);
        verify(giornoRepository).deleteById(id);
        assertEquals("redirect:/", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEliminaGiorno_NullId() {
        // dati
        Integer id = null;

        // Esecuzione dei dati
        giornocontroller.eliminaGiorno(id, model);

    }

}
