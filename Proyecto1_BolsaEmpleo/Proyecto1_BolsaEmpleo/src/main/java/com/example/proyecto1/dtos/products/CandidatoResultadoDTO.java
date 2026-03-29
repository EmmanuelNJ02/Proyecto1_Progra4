package com.example.proyecto1.dtos.products;

public class CandidatoResultadoDTO {
    private Integer usuarioId;

    private String nombreCompleto;

    private int requisitosCumplidos;
    private int requisitosTotales;

    private double porcentajeCoincidencia;

    public CandidatoResultadoDTO(Integer usuarioId, String nombreCompleto, int requisitosCumplidos, int requisitosTotales, double porcentajeCoincidencia) {
        this.usuarioId = usuarioId;
        this.nombreCompleto = nombreCompleto;
        this.requisitosCumplidos = requisitosCumplidos;
        this.requisitosTotales = requisitosTotales;
        this.porcentajeCoincidencia = porcentajeCoincidencia;
    }

    public Integer getUsuarioId() { return usuarioId; }

    public String getNombreCompleto() { return nombreCompleto; }

    public int getRequisitosCumplidos() { return requisitosCumplidos; }
    public int getRequisitosTotales() { return requisitosTotales; }

    public double getPorcentajeCoincidencia() { return porcentajeCoincidencia; }
}
