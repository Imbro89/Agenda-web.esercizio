package com.weg.agenda.AgendaRepository;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.weg.agenda.AgendaModels.Appuntamento;



@Repository
@EnableJpaRepositories
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Integer> {
    
    Optional<Appuntamento> findByUfficioAndOra(String ufficio, LocalTime ora);
    Optional<Appuntamento> findByOra(LocalTime ora);
    Optional<Appuntamento> findByOraAndId(LocalTime ora,Integer giornoId);

    List<Appuntamento> findByDescrizioneContainingOrUfficioContainingOrGiornoData(String decsrizione, String ufficio,LocalDate data);
    List<Appuntamento>findByDescrizioneContainingAndUfficioContaining(String descrizione,String ufficio);
    List<Appuntamento>findByDescrizioneContainingAndGiornoData(String descrizione,LocalDate data);
    List<Appuntamento>findByUfficioContainingAndGiornoData( String ufficio,LocalDate data);


    List<Appuntamento>findByDescrizioneContaining(String decsrizione);
    List<Appuntamento>findByUfficioContaining(String ufficio);
    List<Appuntamento>findByGiornoData(LocalDate data);
    

    
    
    
    
}
