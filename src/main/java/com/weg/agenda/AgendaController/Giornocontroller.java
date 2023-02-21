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
    public String index(Model model) {
        Iterable<Giorno> giorni = giornoRepository.findAll();
        Iterable<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        model.addAttribute("appuntamenti", appuntamenti);
        model.addAttribute("giorni", giorni);
        return "index";
    }
//crea il giorno
    @PostMapping(path = "/creaGiorno", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> creaGiorno(@RequestBody Giorno nuovoGiorno) {
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
    public ResponseEntity<String> eliminaGiorno(@PathVariable("id") Integer id) {
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
    public String mostraModificaAppuntamento(@PathVariable("id") Integer id,
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
    public String cercaAppuntamenti(@RequestParam(name = "giorno", required = false) String giornoStr,
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

// 

/*
 * @Controller
 * public class Giornocontroller {
 * 
 * @Autowired
 * GiornoRepository giornoRepository;
 * 
 * @Autowired
 * AppuntamentoRepository appuntamentoRepository;
 * 
 * public Giornocontroller(GiornoRepository giornoRepository) {
 * this.giornoRepository = giornoRepository;
 * }
 * 
 * @GetMapping("/")
 * public String index(Model model) {
 * Iterable<Giorno> giorni = giornoRepository.findAll();
 * Iterable<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
 * model.addAttribute("appuntamenti", appuntamenti);
 * model.addAttribute("giorni", giorni);
 * return "index";
 * }
 * 
 * @PostMapping("/creaGiorno")
 * public String creaGiorno(@RequestParam("data") LocalDate data,
 * Model model) {
 * if (data == null) {
 * return "index";
 * }
 * Optional<Giorno> giornoEsistente = giornoRepository.findByData(data);
 * if (giornoEsistente.isPresent()) {
 * model.addAttribute("error", "Esiste già un giorno con questa data");
 * return "index";
 * }
 * Giorno nuovoGiorno = new Giorno(data);
 * giornoRepository.save(nuovoGiorno);
 * return "redirect:/";
 * }
 * 
 * @Transactional
 * 
 * @RequestMapping(value = "/eliminaGiorno/{id}", method = {
 * RequestMethod.DELETE, RequestMethod.GET })
 * public String eliminaGiorno(@PathVariable("id") Integer id, Model model) {
 * if (id == null) {
 * throw new IllegalArgumentException("Id non può essere null");
 * }
 * giornoRepository.deleteAppuntamentiByGiornoId(id);
 * giornoRepository.deleteById(id.intValue());
 * 
 * return "redirect:/";
 * }
 * 
 * 
 * @PostMapping("/creaAppuntamento")
 * public String creaAppuntamento(@RequestParam("giornoId") Integer
 * giornoId, @RequestParam("ora") LocalTime ora,
 * 
 * @RequestParam("descrizione") String descrizione, @RequestParam("ufficio")
 * String ufficio,
 * Model model) {
 * if (giornoId == null || ora == null || descrizione == null || ufficio ==
 * null) {
 * return "index";
 * }
 * 
 * Optional<Giorno> giorno = giornoRepository.findById(giornoId);
 * if (!giorno.isPresent()) {
 * model.addAttribute("errorDay", "Il giorno specificato non esiste");
 * return "index";
 * }
 * 
 * Optional<Appuntamento> oraEsistente =
 * appuntamentoRepository.findByOraAndId(ora, giornoId);
 * if (oraEsistente.isPresent()) {
 * model.addAttribute("errorTime",
 * "Esiste già un appuntamento per l'ora specificata");
 * return "index";
 * }
 * 
 * Appuntamento nuovoAppuntamento = new Appuntamento();
 * nuovoAppuntamento.setGiorno(giorno.get());
 * nuovoAppuntamento.setOra(ora);
 * nuovoAppuntamento.setDescrizione(descrizione);
 * nuovoAppuntamento.setUfficio(ufficio);
 * appuntamentoRepository.save(nuovoAppuntamento);
 * return "redirect:/";
 * }
 */

/*
 * @GetMapping("/{id}/modifica")
 * public String mostraModificaAppuntamento(@PathVariable("id") Integer id,
 * Model model) {
 * Optional<Appuntamento> appuntamentoEsistente =
 * appuntamentoRepository.findById(id);
 * if (!appuntamentoEsistente.isPresent()) {
 * model.addAttribute("error", "L'appuntamento specificato non esiste");
 * return "appuntamentoModifica";
 * }
 * model.addAttribute("appuntamento", appuntamentoEsistente.get());
 * return "appuntamentoModifica";
 * }
 * 
 * @PostMapping("/{id}/modifica")
 * public String modificaAppuntamento(@PathVariable("id") Integer
 * id, @RequestParam("giornoId") Integer giornoId,
 * 
 * @RequestParam("ora") LocalTime ora, @RequestParam("descrizione") String
 * descrizione,
 * 
 * @RequestParam("ufficio") String ufficio, Model model) {
 * if (id == null || giornoId == null || ora == null || descrizione == null ||
 * ufficio == null) {
 * return "appuntamentoModifica";
 * }
 * 
 * Optional<Appuntamento> appuntamentoEsistente =
 * appuntamentoRepository.findById(id);
 * if (!appuntamentoEsistente.isPresent()) {
 * model.addAttribute("error", "L'appuntamento specificato non esiste");
 * return "appuntamentoModifica";
 * }
 * 
 * Optional<Appuntamento> oraEsistente =
 * appuntamentoRepository.findByOraAndId(ora, giornoId);
 * if (oraEsistente.isPresent() && !oraEsistente.get().getId().equals(id)) {
 * model.addAttribute("errorTime",
 * "Esiste già un appuntamento per l'ora specificata");
 * return "appuntamentoModifica";
 * }
 * 
 * Optional<Giorno> giorno = giornoRepository.findById(giornoId);
 * if (!giorno.isPresent()) {
 * model.addAttribute("errorDay", "Il giorno specificato non esiste");
 * return "appuntamentoModifica";
 * }
 * 
 * appuntamentoEsistente.get().setGiorno(giorno.get());
 * appuntamentoEsistente.get().setOra(ora);
 * appuntamentoEsistente.get().setDescrizione(descrizione);
 * appuntamentoEsistente.get().setUfficio(ufficio);
 * appuntamentoRepository.save(appuntamentoEsistente.get());
 * 
 * return "redirect:/";
 * }
 * 
 * @RequestMapping(value = "/eliminaAppuntamento/{id}", method = {
 * RequestMethod.DELETE, RequestMethod.GET })
 * public String eliminaAppuntamento(@PathVariable("id") Integer id, Model
 * model) {
 * if (id == null) {
 * throw new IllegalArgumentException("Id non può essere null");
 * }
 * appuntamentoRepository.deleteById(id.intValue());
 * return "redirect:/";
 * }
 */
/*
 * @GetMapping("/appuntamenti/cerca")
 * public String cercaAppuntamenti(
 * 
 * @RequestParam(required = false) String descrizione,
 * 
 * @RequestParam(required = false) String ufficio,
 * 
 * @RequestParam(required = false) @DateTimeFormat(iso =
 * DateTimeFormat.ISO.DATE) LocalDate data,
 * Model model) {
 * 
 * List<Appuntamento> appuntamenti = new ArrayList<>();
 * List<Giorno> giorni = new ArrayList<>();
 * 
 * if (descrizione != null && ufficio != null && data != null &&
 * descrizione.trim().length() > 0
 * && ufficio.trim().length() > 0) {
 * appuntamenti = appuntamentoRepository
 * .findByDescrizioneContainingOrUfficioContainingOrGiornoData(descrizione,
 * ufficio, data);
 * 
 * } else if (descrizione != null && ufficio != null &&
 * descrizione.trim().length() > 0
 * && ufficio.trim().length() > 0) {
 * appuntamenti =
 * appuntamentoRepository.findByDescrizioneContainingAndUfficioContaining(
 * descrizione, ufficio);
 * 
 * } else if (descrizione != null && data != null && descrizione.trim().length()
 * > 0) {
 * appuntamenti =
 * appuntamentoRepository.findByDescrizioneContainingAndGiornoData(descrizione,
 * data);
 * 
 * } else if (ufficio != null && data != null && ufficio.trim().length() > 0) {
 * appuntamenti =
 * appuntamentoRepository.findByUfficioContainingAndGiornoData(ufficio, data);
 * 
 * } else if (descrizione != null && descrizione.trim().length() > 0) {
 * appuntamenti =
 * appuntamentoRepository.findByDescrizioneContaining(descrizione);
 * 
 * } else if (ufficio != null && ufficio.trim().length() > 0) {
 * appuntamenti = appuntamentoRepository.findByUfficioContaining(ufficio);
 * 
 * } else if (data != null) {
 * appuntamenti = appuntamentoRepository.findByGiornoData(data);
 * } else {
 * 
 * appuntamenti = appuntamentoRepository.findAll();
 * }
 * 
 * if (appuntamenti.isEmpty()) {
 * model.addAttribute("niente", "Nessun appuntamento trovato.");
 * } else {
 * model.addAttribute("appuntamenti", appuntamenti);
 * model.addAttribute("giorni", giorni);
 * model.addAttribute("descrizione", descrizione);
 * model.addAttribute("ufficio", ufficio);
 * model.addAttribute("data", data);
 * }
 * 
 * return "cerca";
 * }
 * }
 */
