package com.weg.agenda.AgendaModels;

import java.time.LocalTime;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;




@Entity
public class Appuntamento {

    @ManyToOne
    @JoinColumn
    private Giorno giorno;
    
    public Giorno getGiorno() {
        return giorno;
    }
    
    public void setGiorno(Giorno giorno) {
        this.giorno = giorno;
    }
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    
    private LocalTime ora;

    private String descrizione;

    private String ufficio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getUfficio() {
        return ufficio;
    }

    public void setUfficio(String ufficio) {
        this.ufficio = ufficio;
    }

    

    public Appuntamento(LocalTime ora, String descrizione, String ufficio, Giorno giorno) {
        this.ora = ora;
        this.descrizione = descrizione;
        this.ufficio = ufficio;
        
    }

    public Appuntamento() {
    }

    

    

    
}