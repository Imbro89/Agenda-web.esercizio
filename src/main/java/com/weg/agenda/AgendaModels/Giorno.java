package com.weg.agenda.AgendaModels;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;


@Entity
public class Giorno {

    @OneToMany(mappedBy = "giorno")
    private List<Appuntamento> appuntamenti;
    
    public List<Appuntamento> getAppuntamenti() {
        return appuntamenti;
    }
    
    public void setAppuntamenti(List<Appuntamento> appuntamenti) {
        this.appuntamenti = appuntamenti;
    }
    





    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Giorno() {
    }

    public Giorno(LocalDate data) {
        this.data = data;
    }

    
    
    
}
