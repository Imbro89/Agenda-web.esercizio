package com.weg.agenda.AgendaController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.List;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.weg.agenda.AgendaModels.Appuntamento;
import com.weg.agenda.AgendaModels.Giorno;
import com.weg.agenda.AgendaRepository.AppuntamentoRepository;
import com.weg.agenda.AgendaRepository.GiornoRepository;
import jakarta.transaction.Transactional;

@Controller
public class Giornocontroller {

    @Autowired
    GiornoRepository giornoRepository;

    @Autowired
    AppuntamentoRepository appuntamentoRepository;

    public Giornocontroller() {
    }

//home agenda
    @GetMapping("/")
    public Object index(Model model) {
        Iterable<Giorno> giorni = giornoRepository.findAll();
        Iterable<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        model.addAttribute("appuntamenti", appuntamenti);
        model.addAttribute("giorni", giorni);
        return "index";
    }
//crea il giorno
    @PostMapping(path = "/creaGiorno", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> creaGiorno(@RequestBody Giorno nuovoGiorno) {
        Optional<Giorno> giornoEsistente = giornoRepository.findByData(nuovoGiorno.getData());
        if (giornoEsistente.isPresent()) {
            return ResponseEntity.badRequest().body("Esiste già un giorno con questa data");
        }
        giornoRepository.save(nuovoGiorno);
        return ResponseEntity.ok("Giorno creato");
    }
//elimina il giorno
    @Transactional
    @RequestMapping(value = "/eliminaGiorno/{id}", method = { RequestMethod.DELETE, RequestMethod.GET })
    public ResponseEntity<Object> eliminaGiorno(@PathVariable("id") Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id non può essere null");
        }
        giornoRepository.deleteAppuntamentiByGiornoId(id);
        giornoRepository.deleteById(id.intValue());
        return ResponseEntity.ok("Giorno eliminato");
    }
//crea un appuntamento 
    @PostMapping(path = "/creaAppuntamento", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Object> creaAppuntamento(@RequestBody Appuntamento request) {
        Giorno giorno = request.getGiorno();
        LocalTime ora = request.getOra();
        String descrizione = request.getDescrizione();
        String ufficio = request.getUfficio();

        if (giorno == null || ora == null || descrizione == null || ufficio == null) {
            return new ResponseEntity<>("Dati mancanti", HttpStatus.BAD_REQUEST);
        }

        Optional<Giorno> giornoEsistente = giornoRepository.findById(giorno.getId());
        if (!giornoEsistente.isPresent()) {
            return new ResponseEntity<>("Il giorno specificato non esiste", HttpStatus.BAD_REQUEST);
        }

        Optional<Appuntamento> oraEsistente = appuntamentoRepository.findByOraAndGiorno(ora, giorno);
        if (oraEsistente.isPresent()) {
            return new ResponseEntity<>("Esiste già un appuntamento per l'ora specificata", HttpStatus.BAD_REQUEST);
        }

        Appuntamento nuovoAppuntamento = new Appuntamento();
        nuovoAppuntamento.setGiorno(giornoEsistente.get());
        nuovoAppuntamento.setOra(ora);
        nuovoAppuntamento.setDescrizione(descrizione);
        nuovoAppuntamento.setUfficio(ufficio);
        appuntamentoRepository.save(nuovoAppuntamento);

        return new ResponseEntity<>("Appuntamento creato con successo", HttpStatus.OK);
    }
//visualizza l'appuntamento da modificare
    @GetMapping("/{id}/modifica")
    public Object mostraModificaAppuntamento(@PathVariable("id") Integer id,
            Model model) {
        Optional<Appuntamento> appuntamentoEsistente = appuntamentoRepository.findById(id);
        if (!appuntamentoEsistente.isPresent()) {
            model.addAttribute("error", "L'appuntamento specificato non esiste");
            return "appuntamentoModifica";
        }
        model.addAttribute("appuntamento", appuntamentoEsistente.get());
        return "appuntamentoModifica";
    }
// modifica l'appuntamento 
    @PostMapping(path = "/{id}/modifica", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Object> modificaAppuntamento(@PathVariable("id") Integer id,
            @RequestBody Appuntamento request) {
        Giorno giorno = request.getGiorno();
        LocalTime ora = request.getOra();
        String descrizione = request.getDescrizione();
        String ufficio = request.getUfficio();

        if (giorno == null || ora == null || descrizione == null || ufficio == null) {
            return new ResponseEntity<>("Dati mancanti", HttpStatus.BAD_REQUEST);
        }

        Optional<Giorno> giornoEsistente = giornoRepository.findById(giorno.getId());
        if (!giornoEsistente.isPresent()) {
            return new ResponseEntity<>("Il giorno specificato non esiste", HttpStatus.BAD_REQUEST);
        }

        Optional<Appuntamento> appuntamentoEsistente = appuntamentoRepository.findById(id);
        if (!appuntamentoEsistente.isPresent()) {
            return new ResponseEntity<>("L'appuntamento specificato non esiste", HttpStatus.NOT_FOUND);
        }

        Optional<Appuntamento> oraEsistente = appuntamentoRepository.findByOraAndGiorno(ora, giorno);
        if (oraEsistente.isPresent() && !oraEsistente.get().getId().equals(id)) {
            return new ResponseEntity<>("Esiste già un appuntamento per l'ora specificata", HttpStatus.CONFLICT);
        }

        Appuntamento appuntamento = appuntamentoEsistente.get();
        appuntamento.setGiorno(giornoEsistente.get());
        appuntamento.setOra(ora);
        appuntamento.setDescrizione(descrizione);
        appuntamento.setUfficio(ufficio);
        appuntamentoRepository.save(appuntamento);

        return new ResponseEntity<>("Appuntamento modificato con successo", HttpStatus.OK);
    }
    //Elimina l'appuntamento 
    @PostMapping(path = "/eliminaAppuntamento/{id}", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Object> eliminaAppuntamento(@PathVariable("id") Integer id) {
        Optional<Appuntamento> appuntamento = appuntamentoRepository.findById(id);
        if (!appuntamento.isPresent()) {
            return new ResponseEntity<>("L'appuntamento specificato non esiste", HttpStatus.NOT_FOUND);
        }
        appuntamentoRepository.deleteById(id);
        return new ResponseEntity<>("Appuntamento eliminato con successo", HttpStatus.OK);
    }
//ricerca appuntamento per data-ufficio-descrizione tramite url
    @GetMapping(path = "/appuntamenti/cerca", produces = "text/html")
    public Object cercaAppuntamenti(@RequestParam(name = "giorno", required = false) String giornoStr,
            @RequestParam(name = "descrizione", required = false) String descrizione,
            @RequestParam(name = "ufficio", required = false) String ufficio,
            Model model) {
        List<Appuntamento> appuntamenti = new ArrayList<>();

        if (giornoStr != null && !giornoStr.isEmpty()) {
            try {
                LocalDate giorno = LocalDate.parse(giornoStr);
                List<Appuntamento> appuntamentiByGiorno = appuntamentoRepository.findByGiornoData(giorno);
                appuntamenti.addAll(appuntamentiByGiorno);
            } catch (DateTimeParseException e) {
                // la data passata non è nel formato corretto
                // si può gestire l'eccezione in modo adeguato
            }
        }

        if (ufficio != null && !ufficio.isEmpty()) {
            List<Appuntamento> appuntamentiByUfficioAndOra = appuntamentoRepository
                    .findByUfficioContainingIgnoreCase(ufficio);
            appuntamenti.addAll(appuntamentiByUfficioAndOra);
        }

        if (descrizione != null && !descrizione.isEmpty()) {
            List<Appuntamento> appuntamentiByDescrizione = appuntamentoRepository
                    .findByDescrizioneContainingIgnoreCase(descrizione);
            appuntamenti.addAll(appuntamentiByDescrizione);
        }

        model.addAttribute("appuntamenti", appuntamenti);
        return "cerca";
    }

}

