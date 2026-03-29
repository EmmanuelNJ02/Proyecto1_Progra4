package com.example.proyecto1.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "puesto_caracteristica")
public class PuestoCaracteristica {

    @EmbeddedId
    private PuestoCaracteristicaId id;

    @ManyToOne
    @MapsId("puestoId")
    @JoinColumn(name = "puesto_id")
    private Puesto puesto;

    @ManyToOne
    @MapsId("caracteristicaId")
    @JoinColumn(name = "caracteristica_id")
    private Caracteristica caracteristica;

    @Column(name = "nivel_deseado", nullable = false)
    private Integer nivelDeseado;

    @Embeddable
    public static class PuestoCaracteristicaId implements Serializable {
        @Column(name = "puesto_id")
        private Integer puestoId;

        @Column(name = "caracteristica_id")
        private Integer caracteristicaId;

        public PuestoCaracteristicaId() {}

        public PuestoCaracteristicaId(Integer puestoId, Integer caracteristicaId) {
            this.puestoId = puestoId;
            this.caracteristicaId = caracteristicaId;
        }


        public Integer getPuestoId() { return puestoId; }
        public void setPuestoId(Integer puestoId) { this.puestoId = puestoId; }
        public Integer getCaracteristicaId() { return caracteristicaId; }
        public void setCaracteristicaId(Integer caracteristicaId) { this.caracteristicaId = caracteristicaId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PuestoCaracteristicaId that = (PuestoCaracteristicaId) o;
            return Objects.equals(puestoId, that.puestoId) &&
                    Objects.equals(caracteristicaId, that.caracteristicaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(puestoId, caracteristicaId);
        }
    }

    public PuestoCaracteristica() {}


    public PuestoCaracteristicaId getId() {
        return id;
    }

    public void setId(PuestoCaracteristicaId id) {
        this.id = id;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public Integer getNivelDeseado() {
        return nivelDeseado;
    }

    public void setNivelDeseado(Integer nivelDeseado) {
        this.nivelDeseado = nivelDeseado;
    }
}
