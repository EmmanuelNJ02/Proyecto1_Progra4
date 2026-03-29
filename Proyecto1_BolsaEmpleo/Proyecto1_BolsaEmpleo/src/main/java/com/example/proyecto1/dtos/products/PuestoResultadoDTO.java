package com.example.proyecto1.dtos.products;

import java.math.BigDecimal;

public class PuestoResultadoDTO {

    private Integer id;
    private String empresa;
    private String descripcionGeneral;
    private BigDecimal salario;

    public PuestoResultadoDTO() {
    }

    public PuestoResultadoDTO(Integer id, String empresa, String descripcionGeneral, BigDecimal salario) {
        this.id = id;
        this.empresa = empresa;
        this.descripcionGeneral = descripcionGeneral;
        this.salario = salario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }
}

