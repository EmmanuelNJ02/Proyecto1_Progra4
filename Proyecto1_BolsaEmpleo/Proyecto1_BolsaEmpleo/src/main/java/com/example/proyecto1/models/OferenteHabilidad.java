package com.example.proyecto1.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "oferente_habilidad")
public class OferenteHabilidad {

    @EmbeddedId
    private OferenteHabilidadId id;

    @ManyToOne
    @MapsId("oferenteId")
    @JoinColumn(name = "oferente_id")
    private Oferente oferente;

    @ManyToOne
    @MapsId("caracteristicaId")
    @JoinColumn(name = "caracteristica_id")
    private Caracteristica caracteristica;

    @Column(nullable = false)
    private Integer nivel;

    @Embeddable
    public static class OferenteHabilidadId implements Serializable {
        @Column(name = "oferente_id")
        private Integer oferenteId;

        @Column(name = "caracteristica_id")
        private Integer caracteristicaId;

        public OferenteHabilidadId() {}

        public OferenteHabilidadId(Integer oferenteId, Integer caracteristicaId) {
            this.oferenteId = oferenteId;
            this.caracteristicaId = caracteristicaId;
        }


        public Integer getOferenteId() { return oferenteId; }
        public void setOferenteId(Integer oferenteId) { this.oferenteId = oferenteId; }
        public Integer getCaracteristicaId() { return caracteristicaId; }
        public void setCaracteristicaId(Integer caracteristicaId) { this.caracteristicaId = caracteristicaId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OferenteHabilidadId that = (OferenteHabilidadId) o;
            return Objects.equals(oferenteId, that.oferenteId) &&
                    Objects.equals(caracteristicaId, that.caracteristicaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(oferenteId, caracteristicaId);
        }
    }

    public OferenteHabilidad() {}

    // Getters y Setters
    public OferenteHabilidadId getId() {
        return id;
    }

    public void setId(OferenteHabilidadId id) {
        this.id = id;
    }

    public Oferente getOferente() {
        return oferente;
    }

    public void setOferente(Oferente oferente) {
        this.oferente = oferente;
    }

    public Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}