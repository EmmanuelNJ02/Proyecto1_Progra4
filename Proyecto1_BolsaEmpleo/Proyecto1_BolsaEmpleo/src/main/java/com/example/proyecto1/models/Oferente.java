package com.example.proyecto1.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "oferente")
public class Oferente {

    @Id
    private Integer usuarioId;

    @Column(nullable = false, unique = true, length = 50)
    private String identificacion;

    @Column(name = "nombre_oferente", nullable = false, length = 100)
    private String nombreOferente;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(nullable = false, length = 50)
    private String nacionalidad;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(name = "lugar_residencia", nullable = false, columnDefinition = "TEXT")
    private String lugarResidencia;

    @Column(name = "ruta_curriculum", length = 500)
    private String rutaCurriculum;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "oferente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OferenteHabilidad> habilidades = new ArrayList<>();

    public Oferente() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombreOferente() {
        return nombreOferente;
    }

    public void setNombreOferente(String nombreOferente) {
        this.nombreOferente = nombreOferente;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLugarResidencia() {
        return lugarResidencia;
    }

    public void setLugarResidencia(String lugarResidencia) {
        this.lugarResidencia = lugarResidencia;
    }

    public String getRutaCurriculum() {
        return rutaCurriculum;
    }

    public void setRutaCurriculum(String rutaCurriculum) {
        this.rutaCurriculum = rutaCurriculum;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<OferenteHabilidad> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<OferenteHabilidad> habilidades) {
        this.habilidades = habilidades;
    }
}

