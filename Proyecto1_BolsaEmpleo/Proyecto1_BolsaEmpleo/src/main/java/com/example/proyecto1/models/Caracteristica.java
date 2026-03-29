package com.example.proyecto1.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "caracteristica")
public class Caracteristica {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_caracteristica", nullable = false, length = 100)
    private String nombreCaracteristica;

    @ManyToOne
    @JoinColumn(name = "caracteristica_padre_id")
    private Caracteristica caracteristicaPadre;

    @OneToMany(mappedBy = "caracteristicaPadre")
    private List<Caracteristica> subCaracteristicas = new ArrayList<>();

    public Caracteristica() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreCaracteristica() {
        return nombreCaracteristica;
    }

    public void setNombreCaracteristica(String nombreCaracteristica) {
        this.nombreCaracteristica = nombreCaracteristica;
    }

    public Caracteristica getCaracteristicaPadre() {
        return caracteristicaPadre;
    }

    public void setCaracteristicaPadre(Caracteristica caracteristicaPadre) {
        this.caracteristicaPadre = caracteristicaPadre;
    }

    public List<Caracteristica> getSubCaracteristicas() {
        return subCaracteristicas;
    }

    public void setSubCaracteristicas(List<Caracteristica> subCaracteristicas) {
        this.subCaracteristicas = subCaracteristicas;
    }
}

