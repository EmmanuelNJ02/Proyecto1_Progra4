package com.example.proyecto1.dtos.products;

import java.math.BigDecimal;

public class PuestoForm {

    private String descripcionGeneral;

    private BigDecimal salario;

    private String tipoPublicacion;

    private Integer caracteristicaId1;
    private Integer nivel1;
    private Integer caracteristicaId2;
    private Integer nivel2;
    private Integer caracteristicaId3;
    private Integer nivel3;

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

    public String getTipoPublicacion() {
        return tipoPublicacion;
    }

    public void setTipoPublicacion(String tipoPublicacion) {
        this.tipoPublicacion = tipoPublicacion;
    }

    public Integer getCaracteristicaId1() { return caracteristicaId1; }
    public void setCaracteristicaId1(Integer caracteristicaId1) { this.caracteristicaId1 = caracteristicaId1; }
    public Integer getNivel1() { return nivel1; }
    public void setNivel1(Integer nivel1) { this.nivel1 = nivel1; }
    public Integer getCaracteristicaId2() { return caracteristicaId2; }
    public void setCaracteristicaId2(Integer caracteristicaId2) { this.caracteristicaId2 = caracteristicaId2; }
    public Integer getNivel2() { return nivel2; }
    public void setNivel2(Integer nivel2) { this.nivel2 = nivel2; }
    public Integer getCaracteristicaId3() { return caracteristicaId3; }
    public void setCaracteristicaId3(Integer caracteristicaId3) { this.caracteristicaId3 = caracteristicaId3; }
    public Integer getNivel3() { return nivel3; }
    public void setNivel3(Integer nivel3) { this.nivel3 = nivel3; }
}
