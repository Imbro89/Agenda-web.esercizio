package com.weg.agenda.AgendaControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import com.weg.agenda.AgendaController.Giornocontroller;
import com.weg.agenda.AgendaModels.Appuntamento;
import com.weg.agenda.AgendaModels.Giorno;
import com.weg.agenda.AgendaRepository.AppuntamentoRepository;
import com.weg.agenda.AgendaRepository.GiornoRepository;

@RunWith(MockitoJUnitRunner.class)
public class GiornocontrollerTest {

  @Mock
  private GiornoRepository giornoRepository;

  @Mock
  private AppuntamentoRepository appuntamentoRepository;

  @Mock
  private Model model;

  @InjectMocks
  private Giornocontroller giornocontroller;

  @Test
  public void testIndex() {
    // Dati di esempio
    List<Giorno> giorni = Arrays.asList(new Giorno(), new Giorno());
    List<Appuntamento> appuntamenti = Arrays.asList(new Appuntamento(), new Appuntamento());

    // Configurazione delle dipendenze mock
    when(giornoRepository.findAll()).thenReturn(giorni);
    when(appuntamentoRepository.findAll()).thenReturn(appuntamenti);

    // Esecuzione del metodo da testare
    String result = giornocontroller.index(model);

    // Verifica degli effetti collaterali
    verify(model).addAttribute("giorni", giorni);
    verify(model).addAttribute("appuntamenti", appuntamenti);

    // Verifica del risultato
    assertEquals("index", result);
  }

  @Test
  public void testCreaGiorno() {
    // Dati di esempio
    LocalDate data = LocalDate.now();
    Giorno nuovoGiorno = new Giorno(data);

    // Configurazione delle dipendenze mock
    when(giornoRepository.findByData(data)).thenReturn(Optional.empty());

    // Esecuzione del metodo da testare
    ResponseEntity<String> response = giornocontroller.creaGiorno(nuovoGiorno);

    // Verifica degli effetti collaterali
    verify(giornoRepository).save(nuovoGiorno);

    // Verifica del risultato
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Giorno creato", response.getBody());
  }

  @Test
  public void testCreaGiorno_GiornoEsistente() {
    // Dati di esempio
    LocalDate data = LocalDate.now();

    // Configurazione delle dipendenze mock
    when(giornoRepository.findByData(data)).thenReturn(Optional.of(new Giorno(data)));
    Model model = mock(Model.class);

    // Esecuzione del metodo da testare
    Giorno nuovoGiorno = new Giorno(data);
    String result = giornocontroller.creaGiorno(nuovoGiorno).getBody();

    // Verifica degli effetti collaterali
    verify(giornoRepository, never()).save(any());

    // Verifica del risultato
    assertEquals("Giorno creato", result);
  }

  @Test
  public void testEliminaGiorno() {
    // Dati di esempio
    Integer id = 123;

    // Esecuzione del metodo da testare
    ResponseEntity<String> result = giornocontroller.eliminaGiorno(id);

    // Verifica degli effetti collaterali
    verify(giornoRepository).deleteAppuntamentiByGiornoId(id);
    verify(giornoRepository).deleteById(id);

    // Verifica del risultato
    assertEquals("Giorno eliminato", result.getBody());
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEliminaGiorno_NullId() {
    // Esecuzione del metodo da testare con id null
    giornocontroller.eliminaGiorno(null);
  }

}

/*
 * @RunWith(MockitoJUnitRunner.class)
 * public class GiornocontrollerTest {
 * 
 * @Mock // dipendenze repository
 * private GiornoRepository giornoRepository;
 * 
 * @Mock
 * private AppuntamentoRepository appuntamentoRepository;
 * 
 * 
 * @Mock // bean fittizio
 * private Model model;
 * 
 * @InjectMocks // dipendenza della classe controller
 * private Giornocontroller giornocontroller;
 * 
 * @Test
 * public void testIndex() {
 * // dati
 * List<Giorno> giorni = Arrays.asList(new Giorno(), new Giorno());
 * List<Appuntamento> appuntamenti = Arrays.asList(new Appuntamento(),new
 * Appuntamento());
 * when(appuntamentoRepository.findAll()).thenReturn(appuntamenti);
 * //AppuntamentoRepository risulta null
 * when(giornoRepository.findAll()).thenReturn(giorni);
 * 
 * //esecuzione dei dati
 * String result = giornocontroller.index(model);
 * 
 * // verifica dei dati
 * verify(model).addAttribute("giorni", giorni);
 * assertEquals("index", result);
 * }
 * 
 * @Test
 * public void testCreaGiorno() {
 * // dati
 * LocalDate data = LocalDate.now();
 * when(giornoRepository.findByData(data)).thenReturn(Optional.empty());
 * 
 * // esecuzione dei dati
 * String result = giornocontroller.creaGiorno(data, model);
 * 
 * // Verifica dei dati
 * verify(giornoRepository).save(any(Giorno.class));
 * assertEquals("redirect:/", result);
 * }
 * 
 * @Test
 * public void testCreaGiorno_GiornoEsistente() {
 * // dati
 * LocalDate data = LocalDate.now();
 * Giorno giornoEsistente = new Giorno(data);
 * when(giornoRepository.findByData(data)).thenReturn(Optional.of(
 * giornoEsistente));
 * 
 * // Esecuzione dei dati
 * String result = giornocontroller.creaGiorno(data, model);
 * 
 * // Verifica dei dati
 * verify(model).addAttribute("error", "Esiste già un giorno con questa data");
 * assertEquals("index", result);
 * }
 * 
 * @Test
 * public void testEliminaGiorno() {
 * // dati
 * int id = 1;
 * 
 * // Esecuzione dei dati
 * String result = giornocontroller.eliminaGiorno(id, model);
 * 
 * // verifica dei dati
 * verify(giornoRepository).deleteAppuntamentiByGiornoId(id);
 * verify(giornoRepository).deleteById(id);
 * assertEquals("redirect:/", result);
 * }
 * 
 * @Test(expected = IllegalArgumentException.class)
 * public void testEliminaGiorno_NullId() {
 * // dati
 * Integer id = null;
 * 
 * // Esecuzione dei dati
 * giornocontroller.eliminaGiorno(id, model);
 * 
 * }
 * 
 * }
 */
