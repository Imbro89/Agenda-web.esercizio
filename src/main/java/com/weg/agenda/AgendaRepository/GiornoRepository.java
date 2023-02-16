package com.weg.agenda.AgendaRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.weg.agenda.AgendaModels.Giorno;

import jakarta.transaction.Transactional;

@Repository
public interface GiornoRepository extends JpaRepository<Giorno, Integer> {
    @Query("SELECT g FROM Giorno g WHERE g.data = :data")
    Optional<Giorno> findByData(@Param("data") LocalDate data);

    
    @Modifying
@Query("delete from Appuntamento a where a.giorno.id = :giornoId")
void deleteAppuntamentiByGiornoId(@Param("giornoId") Integer giornoId);

    
    @Override
    @Transactional
    void deleteById(Integer id);


       
    }
    
    

