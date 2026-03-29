package com.example.proyecto1.models;

import jakarta.persistence.*;

@Entity
@Table(name = "administrador")
public class Administrador {


    @Id
    private Integer usuarioId;

    @Column(name = "nombre_admin", nullable = false, length = 150)
    private String nombreAdmin;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Administrador() {
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

