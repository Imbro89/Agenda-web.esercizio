package com.weg.agenda.AgendaRepository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.weg.agenda.AgendaModels.Appuntamento;
import com.weg.agenda.AgendaModels.Giorno;





@Repository
@EnableJpaRepositories
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Integer> {
    
    Optional<Appuntamento> findByUfficioAndOra(String ufficio, LocalTime ora);
    Optional<Appuntamento> findByOra(LocalTime ora);
    Optional<Appuntamento> findByOraAndId(LocalTime ora,Integer giornoId);
    Optional<Appuntamento>findByOraAndGiorno(LocalTime ora,Giorno giorno);
    List<Appuntamento>findByGiornoAndDescrizioneContainingAndUfficioContaining (String decsrizione, String ufficio,Giorno giorno);
    List<Appuntamento>findByDescrizioneContainingAndUfficioContaining(String descrizione,String ufficio);
    List<Appuntamento>findByGiornoAndDescrizioneContaining(String descrizione,Giorno giorno);
    List<Appuntamento>findByGiornoAndUfficioContaining( String ufficio,Giorno giorno);
    List<Appuntamento>findByUfficioContainingAndGiornoData( String ufficio,LocalDate data);
    List<Appuntamento>findByDescrizioneContainingAndGiornoData(String descrizione,LocalDate data);
    List<Appuntamento> findByDescrizioneContainingOrUfficioContainingOrGiornoData(String decsrizione, String ufficio,LocalDate data);
    List<Appuntamento>findByDescrizioneContainingIgnoreCase(String decsrizione);
    List<Appuntamento>findByUfficioContainingIgnoreCase(String ufficio);
    List<Appuntamento>findByGiorno(Giorno giorno);
    List<Appuntamento>findByGiornoData(LocalDate data);
    
 



    
    
    
    
}
