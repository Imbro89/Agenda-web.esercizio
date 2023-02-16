package com.weg.agenda.AgendaController;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    public Giornocontroller(GiornoRepository giornoRepository) {
        this.giornoRepository = giornoRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Iterable<Giorno> giorni = giornoRepository.findAll();
        Iterable<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        model.addAttribute("appuntamenti", appuntamenti);
        model.addAttribute("giorni", giorni);
        return "index";
    }

    @PostMapping("/creaGiorno")
    public String creaGiorno(@RequestParam("data") LocalDate data, Model model) {
        if (data == null) {
            return "index";
        }
        Optional<Giorno> giornoEsistente = giornoRepository.findByData(data);
        if (giornoEsistente.isPresent()) {
            model.addAttribute("error", "Esiste già un giorno con questa data");
            return "index";
        }
        Giorno nuovoGiorno = new Giorno(data);
        giornoRepository.save(nuovoGiorno);
        return "redirect:/";
    }
    
    @Transactional
    @RequestMapping(value = "/eliminaGiorno/{id}", method = { RequestMethod.DELETE, RequestMethod.GET })
    public String eliminaGiorno(@PathVariable("id") Integer id, Model model) {
        if (id == null) {
            throw new IllegalArgumentException("Id non può essere null");
        }
        giornoRepository.deleteAppuntamentiByGiornoId(id);
        giornoRepository.deleteById(id.intValue());
    
        return "redirect:/";
    }

    @PostMapping("/creaAppuntamento")
    public String creaAppuntamento(@RequestParam("giornoId") Integer giornoId, @RequestParam("ora") LocalTime ora,
            @RequestParam("descrizione") String descrizione, @RequestParam("ufficio") String ufficio,
            Model model) {
        if (giornoId == null || ora == null || descrizione == null || ufficio == null) {
            return "index";
        }

        Optional<Giorno> giorno = giornoRepository.findById(giornoId);
        if (!giorno.isPresent()) {
            model.addAttribute("errorDay", "Il giorno specificato non esiste");
            return "index";
        }

        Optional<Appuntamento> oraEsistente = appuntamentoRepository.findByOraAndId(ora, giornoId);
        if (oraEsistente.isPresent()) {
            model.addAttribute("errorTime", "Esiste già un appuntamento per l'ora specificata");
            return "index";
        }

        Appuntamento nuovoAppuntamento = new Appuntamento();
        nuovoAppuntamento.setGiorno(giorno.get());
        nuovoAppuntamento.setOra(ora);
        nuovoAppuntamento.setDescrizione(descrizione);
        nuovoAppuntamento.setUfficio(ufficio);
        appuntamentoRepository.save(nuovoAppuntamento);
        return "redirect:/";
    }

    @GetMapping("/{id}/modifica")
    public String mostraModificaAppuntamento(@PathVariable("id") Integer id, Model model) {
        Optional<Appuntamento> appuntamentoEsistente = appuntamentoRepository.findById(id);
        if (!appuntamentoEsistente.isPresent()) {
            model.addAttribute("error", "L'appuntamento specificato non esiste");
            return "appuntamentoModifica";
        }
        model.addAttribute("appuntamento", appuntamentoEsistente.get());
        return "appuntamentoModifica";
    }

    @PostMapping("/{id}/modifica")
    public String modificaAppuntamento(@PathVariable("id") Integer id, @RequestParam("giornoId") Integer giornoId,
            @RequestParam("ora") LocalTime ora, @RequestParam("descrizione") String descrizione,
            @RequestParam("ufficio") String ufficio, Model model) {
        if (id == null || giornoId == null || ora == null || descrizione == null || ufficio == null) {
            return "appuntamentoModifica";
        }

        Optional<Appuntamento> appuntamentoEsistente = appuntamentoRepository.findById(id);
        if (!appuntamentoEsistente.isPresent()) {
            model.addAttribute("error", "L'appuntamento specificato non esiste");
            return "appuntamentoModifica";
        }

        Optional<Appuntamento> oraEsistente = appuntamentoRepository.findByOraAndId(ora, giornoId);
        if (oraEsistente.isPresent() && !oraEsistente.get().getId().equals(id)) {
            model.addAttribute("errorTime", "Esiste già un appuntamento per l'ora specificata");
            return "appuntamentoModifica";
        }

        Optional<Giorno> giorno = giornoRepository.findById(giornoId);
        if (!giorno.isPresent()) {
            model.addAttribute("errorDay", "Il giorno specificato non esiste");
            return "appuntamentoModifica";
        }

        appuntamentoEsistente.get().setGiorno(giorno.get());
        appuntamentoEsistente.get().setOra(ora);
        appuntamentoEsistente.get().setDescrizione(descrizione);
        appuntamentoEsistente.get().setUfficio(ufficio);
        appuntamentoRepository.save(appuntamentoEsistente.get());

        return "redirect:/";
    }

    @RequestMapping(value = "/eliminaAppuntamento/{id}", method = { RequestMethod.DELETE, RequestMethod.GET })
    public String eliminaAppuntamento(@PathVariable("id") Integer id, Model model) {
        if (id == null) {
            throw new IllegalArgumentException("Id non può essere null");
        }
        appuntamentoRepository.deleteById(id.intValue());
        return "redirect:/";
    }

    @GetMapping("/appuntamenti/cerca")
    public String cercaAppuntamenti(
            @RequestParam(required = false) String descrizione,
            @RequestParam(required = false) String ufficio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        List<Appuntamento> appuntamenti = new ArrayList<>();
        List<Giorno> giorni = new ArrayList<>();

        if (descrizione != null && ufficio != null && data != null && descrizione.trim().length() > 0
                && ufficio.trim().length() > 0) {
            appuntamenti = appuntamentoRepository
                    .findByDescrizioneContainingOrUfficioContainingOrGiornoData(descrizione, ufficio, data);

        } else if (descrizione != null && ufficio != null && descrizione.trim().length() > 0
                && ufficio.trim().length() > 0) {
            appuntamenti = appuntamentoRepository.findByDescrizioneContainingAndUfficioContaining(descrizione, ufficio);

        } else if (descrizione != null && data != null && descrizione.trim().length() > 0) {
            appuntamenti = appuntamentoRepository.findByDescrizioneContainingAndGiornoData(descrizione, data);

        } else if (ufficio != null && data != null && ufficio.trim().length() > 0) {
            appuntamenti = appuntamentoRepository.findByUfficioContainingAndGiornoData(ufficio, data);

        } else if (descrizione != null && descrizione.trim().length() > 0) {
            appuntamenti = appuntamentoRepository.findByDescrizioneContaining(descrizione);

        } else if (ufficio != null && ufficio.trim().length() > 0) {
            appuntamenti = appuntamentoRepository.findByUfficioContaining(ufficio);

        } else if (data != null) {
            appuntamenti = appuntamentoRepository.findByGiornoData(data);
        } else {

            appuntamenti = appuntamentoRepository.findAll();
        }

        if (appuntamenti.isEmpty()) {
            model.addAttribute("niente", "Nessun appuntamento trovato.");
        } else {
            model.addAttribute("appuntamenti", appuntamenti);
            model.addAttribute("giorni", giorni);
            model.addAttribute("descrizione", descrizione);
            model.addAttribute("ufficio", ufficio);
            model.addAttribute("data", data);
        }

        return "cerca";
    }
}
